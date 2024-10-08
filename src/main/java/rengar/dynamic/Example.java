package rengar.dynamic;

import rengar.dynamic.exception.EarlyExitException;
import rengar.dynamic.jdk8.regex.*;

public class Example {
    public static void main(String[] args) throws EarlyExitException {
        Pattern pattern = Pattern.compile("^(ab*b*)+c");
        Matcher matcher = pattern.matcher("a".repeat(20) + "1");
        //matcher.setEarlyExit();
        matcher.matches();
    
        System.out.println(matcher.getProfile().getSuggestions());
        System.out.println(matcher.getProfile().getMatchingStep());
        System.out.println(matcher.getProfile().getNodeNumber());
        System.out.println(matcher.getProfile().coverage());
    }
}
