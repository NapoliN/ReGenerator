package rengar.cli;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import rengar.dynamic.jdk8.regex.Pattern;

public class CanCompie {
    public static void main(String[] args){
        if(args.length != 1){
            return;
        }
        String patternStr = b64decode(args[0]);
        try{
            Pattern.compile(patternStr);
            System.out.println("true");
        }
        catch (Exception e){
            System.out.println("false");
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
}
