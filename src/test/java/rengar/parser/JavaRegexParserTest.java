package rengar.parser;

import rengar.parser.ReDosHunterPreProcess;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;

import rengar.parser.ast.*;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.*;

public class JavaRegexParserTest {
    @Test
    /**
     * 全部に一意なIDが振られているかのテスト
     */
    public void testParseIdSet() {
        assertDoesNotThrow(() -> {
            String patternStr = "num_externe\\s*=(?'num_externe'.*?)\\s*xfi_cd.*ins_numins\\s*=(?'ins_numins'.*?)\\s*apt_nom\\s*=(?'apt_nom'.*?)\\s*apt_type\\s*=(?'apt_type'.*?)\\s*apt_telephone\\s*=(?'apt_telephone'.*?)\\s*tsy_cd\\s*=(?'tsy_cd'.*?)\\s*qsy_cd\\s*=(?'qsy_cd'.*?)\\s*commentaire_externe\\s*=(?'commentaire_externe'.*(?:\\n" + //
                                ".*)*?)\\s*apt_localisation\\s*=(?'apt_localisation'.*?)\\s*urg_cd";
            patternStr = ReDosHunterPreProcess.process(patternStr);
            System.out.println(patternStr);
            RegexParser parser = JavaRegexParser.createParser(RegexParser.Language.Java, patternStr);
            RegexExpr expr = parser.parse();
            Deque<Expr> exprQueue = new ArrayDeque<>();
            exprQueue.add(expr);
            Set<Integer> idSet = new HashSet<>();
            while (!exprQueue.isEmpty()){
                Expr current = exprQueue.pop();
                //if(current.parent != null)
                assertNotEquals(-1, current.getExprId());
                assertFalse(idSet.contains(current.getExprId()));
                idSet.add(current.getExprId());
                switch (current) {
                    case RegexExpr regexExpr:
                        exprQueue.push(regexExpr.getExpr());
                        break;
                    case BranchExpr branchExpr:
                        for (int i=branchExpr.getSize()-1;i>=0;i--){
                            exprQueue.push(branchExpr.getBranchs().get(i));
                        }
                        break;
                    case GroupExpr groupExpr:
                        // FIXME getBody().getExpr()で取り出すの無駄すぎ～～～～～
                        exprQueue.push(groupExpr.getBody().getExpr());
                        break;
                    case SequenceExpr seqExpr:
                        for (int i=seqExpr.getSize()-1;i>=0;i--){
                            exprQueue.push(seqExpr.get(i));
                        }
                        break;
                    case LoopExpr loopExpr:
                        exprQueue.push(loopExpr.getBody());
                        break;
                    default:
                        continue;
                }
            }
        });        
    }

    //@Test
    /**
     * 全てのExprに親が設定されているかのテスト
     */
    public void testParseParentSet(){
        assertDoesNotThrow(() -> {
            Field field = Expr.class.getDeclaredField("parent");
            field.setAccessible(true);

            //String patternStr = "num_externe\\s*=(?'num_externe'.*?)\\s*xfi_cd.*ins_numins\\s*=(?'ins_numins'.*?)\\s*apt_nom\\s*=(?'apt_nom'.*?)\\s*apt_type\\s*=(?'apt_type'.*?)\\s*apt_telephone\\s*=(?'apt_telephone'.*?)\\s*tsy_cd\\s*=(?'tsy_cd'.*?)\\s*qsy_cd\\s*=(?'qsy_cd'.*?)\\s*commentaire_externe\\s*=(?'commentaire_externe'.*(?:\\n" + //".*)*?)\\s*apt_localisation\\s*=(?'apt_localisation'.*?)\\s*urg_cd";
            String patternStr = "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)(:[0-9]{0,5})?(#[\\w]*)?((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[.\\!\\/\\\\w]*))?)";
            patternStr = ReDosHunterPreProcess.process(patternStr);
            System.out.println(patternStr);
            RegexParser parser = JavaRegexParser.createParser(RegexParser.Language.Java, patternStr);
            RegexExpr expr = parser.parse();
            Deque<Expr> exprQueue = new ArrayDeque<>();
            exprQueue.add(expr.getExpr());
            Set<Integer> idSet = new HashSet<>();
            while (!exprQueue.isEmpty()){
                Expr current = exprQueue.pop();
                Expr parent = (Expr)field.get(current);
                assertNotNull(parent);
                assertNotEquals(-1, parent.getExprId());
                idSet.add(current.getExprId());
                switch (current) {
                    case RegexExpr regexExpr:
                        exprQueue.push(regexExpr.getExpr());
                        break;
                    case BranchExpr branchExpr:
                        for (int i=branchExpr.getSize()-1;i>=0;i--){
                            exprQueue.push(branchExpr.getBranchs().get(i));
                        }
                        break;
                    case GroupExpr groupExpr:
                        // FIXME getBody().getExpr()で取り出すの無駄すぎ～～～～～
                        exprQueue.push(groupExpr.getBody().getExpr());
                        break;
                    case SequenceExpr seqExpr:
                        for (int i=seqExpr.getSize()-1;i>=0;i--){
                            exprQueue.push(seqExpr.get(i));
                        }
                        break;
                    case LoopExpr loopExpr:
                        exprQueue.push(loopExpr.getBody());
                        break;
                    default:
                        continue;
                }
            }
        });
    }
}
