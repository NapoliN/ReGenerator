package rengar.dynamic.validator;

import rengar.checker.attack.*;
import rengar.dynamic.exception.EarlyExitException;
import rengar.dynamic.jdk8.regex.*;

public class Validator {
    private final AttackString attackStr;
    private final Pattern pattern;
    private String type;

    public Validator(String patternStr, AttackString attackStr, String type) {
        this.attackStr = attackStr;
        this.type = type;
        pattern = Pattern.compile(patternStr);
    }



    public boolean validate(int upperBound) {
        Matcher matcher = pattern.matcher(attackStr.genStr(), upperBound);
        matcher.setEarlyExit();
        try {
            if (type.contains("SLQ")) { //実装ミスで、本来はPOLSとしなければならない
                matcher.find();
            } else {
                matcher.matches();
            }
        } catch (EarlyExitException ignored) {
            return true;
        } catch (StackOverflowError ignored) {
            return false;
        }
        return false;
    }
}
