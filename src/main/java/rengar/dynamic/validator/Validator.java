package rengar.dynamic.validator;

import rengar.checker.attack.*;
import rengar.dynamic.exception.EarlyExitException;
import rengar.dynamic.jdk8.regex.*;

public class Validator {
    private final Pattern pattern;
    private String type;

    public Validator(String patternStr, String type) {
        this.type = type;
        pattern = Pattern.compile(patternStr);
    }

    public boolean validate(String inputString, int upperBound) {
        Matcher matcher = pattern.matcher(inputString, upperBound);
        matcher.setEarlyExit();
        try {
            if (type.contains("SLQ")) { //実装ミスで、本来はPOLSとしなければならない
                matcher.find();
            } else {
                matcher.matches();
                //System.out.println(matcher.getProfile().getMatchingStep());
            }
        } catch (EarlyExitException ignored) {
            return true;
        } catch (StackOverflowError ignored) {
            return false;
        }
        return false;
    }
}
