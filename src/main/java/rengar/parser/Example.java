package rengar.parser;

import rengar.parser.ast.RegexExpr;
import rengar.parser.exception.PatternSyntaxException;

public class Example {
    public static void main(String[] args) throws PatternSyntaxException {
        //String patternStr = "(?:aiueo)|(?:kakikukeko)";
        String patternStr = "num_externe\\s*=(?'num_externe'.*?)\\s*xfi_cd.*ins_numins\\s*=(?'ins_numins'.*?)\\s*apt_nom\\s*=(?'apt_nom'.*?)\\s*apt_type\\s*=(?'apt_type'.*?)\\s*apt_telephone\\s*=(?'apt_telephone'.*?)\\s*tsy_cd\\s*=(?'tsy_cd'.*?)\\s*qsy_cd\\s*=(?'qsy_cd'.*?)\\s*commentaire_externe\\s*=(?'commentaire_externe'.*(?:\\n" + //
                        ".*)*?)\\s*apt_localisation\\s*=(?'apt_localisation'.*?)\\s*urg_cd";
        RegexParser parser = RegexParser.createParser(RegexParser.Language.Java, patternStr);
        RegexExpr regexExpr = parser.parse();
        System.out.println(regexExpr.genJsonExpression());
        System.out.println(regexExpr.genString());
    }
}
