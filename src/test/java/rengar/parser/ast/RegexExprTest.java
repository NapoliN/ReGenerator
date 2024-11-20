package rengar.parser.ast;

import org.junit.jupiter.api.Test;
import rengar.parser.RegexParser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import rengar.parser.exception.*;

import java.lang.IndexOutOfBoundsException;

public class RegexExprTest {
    //@Test
    public void testGetCommonAncestor() {
        String patternStr = "a*a*b*b*";
        assertDoesNotThrow(() -> {
            RegexParser parser = RegexParser.createParser(RegexParser.Language.Java, patternStr);
            RegexExpr regexExpr = parser.parse();
            Expr ancestor = regexExpr.getCommonAncestor(3,7);
            assertEquals(2,ancestor.getExprId());
        });
    }

    @Test
    public void testGetCommonAncestor2() {
        String patternStr = "&#8212;</span> (.+?) <a.+?href=\\\"(.+?)\\\">CH(.+?)<.+?/>(.+?) <";
        RegexParser parser = RegexParser.createParser(RegexParser.Language.Java, patternStr);
        try{
            RegexExpr regexExpr = parser.parse();
            Expr ancestor = regexExpr.getCommonAncestor(62,62);
            assertEquals(62,ancestor.getExprId());
        }
        catch (PatternSyntaxException e) {
            assertTrue(false);
        }
    }


    @Test
    public void testGetCommonAncestorException() {
        String patternStr = "a*a*b*b*";
        RegexParser parser = RegexParser.createParser(RegexParser.Language.Java, patternStr);
        try{
            RegexExpr regexExpr = parser.parse();
            assertThrows(IndexOutOfBoundsException.class, () -> regexExpr.getCommonAncestor(3,20));
        }
        catch (PatternSyntaxException e) {
            assertTrue(false);
        }
    }
}
