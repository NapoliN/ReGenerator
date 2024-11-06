package rengar.parser.ast;

import java.util.*;
import java.lang.IndexOutOfBoundsException;

// just a wrapper
public class RegexExpr extends Expr {
    private final BranchExpr expr;

    // cache index to each expr
    private Map<Integer, Expr> exprMap = new HashMap<>();

    public RegexExpr(BranchExpr expr) {
        this.expr = expr;
        this.setExprId(0);
    }

    public BranchExpr getExpr() {
        return expr;
    }

    public String genJsonExpression() {
        StringBuilder sb = new StringBuilder();
        sb.append(expr.genJsonExpression());
        return sb.toString();
    }

    @Override
    public String genString() {
        return expr.genString();
    }

    public RegexExpr copy() {
        RegexExpr newRegexExpr = new RegexExpr(expr.copy());
        newRegexExpr.setExprId(getExprId());
        return newRegexExpr;
    }

    /**
     * 2つのExprの共通の祖先Exprのうち、最も近いExprを取得する
     * @param id1
     * @param id2
     * @return 2つのExprの共通の祖先Expr
     * @throws IndexOutOfBoundsException 指定したIDが存在しない場合
     */
    public Expr getCommonAncestor(int id1, int id2) throws IndexOutOfBoundsException {
        Expr expr1 = getExprById(id1, expr);
        Expr expr2 = getExprById(id2, expr);
        if (expr1 == null || expr2 == null) {
            throw new IndexOutOfBoundsException();
        }
        if (id1 == id2) {
            return expr1;
        }

        List<Integer> ancestors1 = expr1.getAncestors();
        List<Integer> ancestors2 = expr2.getAncestors();
        // ancestor1を表示
        System.out.println("ancestors1" + ancestors1);
        // ancestor2を表示
        System.out.println("ancestors2" + ancestors2);
        
        int i = 0;
        while (i < ancestors1.size() && i < ancestors2.size() && ancestors1.get(i) == ancestors2.get(i)) {
            i++;
        }
        System.out.println("anecstor_i" + i);
        return getExprById(ancestors1.get(i - 1), expr);
    }

    /**
     * 指定したIDのExprを取得する
     * @param id 取得したいExprのID
     * @param expr 探索対象のroot Expr
     * @return 指定したIDのExpr、存在しない場合はnull
     */
    private Expr getExprById(int id, Expr expr) {
        if (exprMap.containsKey(id)) {
            return exprMap.get(id);
        }
        Expr tmp = expr;
        exprMap.put(tmp.getExprId(), tmp);
        if (tmp.getExprId() == id) {
            return tmp;
        }
        switch (tmp) {
            case BranchExpr branchExpr:
                for (Expr e : branchExpr.getBranchs()) {
                    var result = getExprById(id, e);
                    if (result != null)
                        return result;
                }
                break;
            case LoopExpr loopExpr:
                var result = getExprById(id, loopExpr.getBody());
                if (result != null)
                    return result;
                break;
            case SequenceExpr sequenceExpr:
                for (Expr e : sequenceExpr.getExprs()) {
                    result = getExprById(id, e);
                    if (result != null)
                        return result;
                    }
                break;
            case GroupExpr groupExpr:
                Expr groupBody = getExprById(id, groupExpr.getBody().getExpr());
                if (groupBody != null)
                    return groupBody;
            default:
                break;
        }
        return null;
    }
}
