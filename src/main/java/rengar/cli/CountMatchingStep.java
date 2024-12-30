package rengar.cli;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import rengar.dynamic.jdk8.regex.Pattern;
import rengar.checker.vulnerability.AttackStringFormat;
import rengar.dynamic.jdk8.regex.Matcher;

public class CountMatchingStep {
    public static void main(String[] args){
        if(args.length != 2){
            return;
        }
        String patternStr = b64decode(args[0]);
        String attackStr = b64decode(args[1]);
        Pattern pattern = Pattern.compile(patternStr);
        String input = decodeAttackString(attackStr);
        Matcher matcher = pattern.matcher(input);
        //System.out.printf("pattern: %s\nattack: %s\n", patternStr,input);
        try{
            matcher.matches();
            int step = matcher.getProfile().getMatchingStep();
            System.out.println(step);
        }
        catch (Exception e){
            System.out.println("error");
        }

    }
    private static String b64encode(String str) {
        return new String(
                Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
    }

    private static String b64decode(String str) {
        return new String(
                Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
    }

    private static String decodeAttackString(String attack){
        return AttackStringFormat.fromJSONString(attack).genString();
    }
}
