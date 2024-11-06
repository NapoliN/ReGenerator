package rengar.generator;

import org.junit.jupiter.api.Test;

import rengar.generator.path.Path;
import rengar.generator.StringGenerator;
import rengar.parser.RegexParser;
import rengar.parser.ast.RegexExpr;
import rengar.parser.charutil.CharUtil;
import rengar.parser.exception.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.*;

public class StringGeneratorTest {
    @Test
    public void testGen() {
        String patternStr = "(a|b)+a";
        RegexParser parser = RegexParser.createParser(RegexParser.Language.Java, patternStr);
        try {
            RegexExpr regexExpr = parser.parse();
            Set<Path> pathList = StringGenerator.gen(regexExpr, 2, 5, true);

            Set<String> pathListStr = new HashSet();
            for (Path path : pathList) {
                pathListStr.add(CharUtil.toPrintableString(path.genValue()));
            }

            // 期待される結果を生成
            Set<String> expected = new HashSet();
            expected.addAll(Arrays.asList("aa", "ba", "aba", "aaa", "baa", "bba"));
            assertIterableEquals(pathListStr, expected);

        } catch (PatternSyntaxException e) {
            assertEquals("Syntax error", e.getMessage());
        }

    }
}
