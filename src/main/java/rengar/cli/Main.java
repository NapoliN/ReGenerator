package rengar.cli;

import rengar.checker.StaticPipeline;
import rengar.checker.attack.AttackString;
import rengar.checker.attack.StringProvider;
import rengar.checker.pattern.DisturbFreePattern;
import com.alibaba.fastjson.JSONObject;
import rengar.config.GlobalConfig;
import org.apache.commons.cli.*;
import rengar.parser.RegexParser;
import rengar.util.Pair;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class Main {
    private final static Options options = new Options();
    private final static CommandLineParser cliParser = new DefaultParser();
    private final static HelpFormatter helpFormatter = new HelpFormatter();
    
    public static void main(String[] args) throws IOException {
        CommandLine cli = initCommandArgument(args);
        if (cli.hasOption("disablePreprocess"))
            GlobalConfig.option.disablePreprocess();
        if (cli.hasOption("weakPatternCheck"))
            GlobalConfig.option.weakPatternCheck();
        if (cli.hasOption("ingoreDisturbance"))
            GlobalConfig.option.ignoreDisturbance();
        if (cli.hasOption("multiple"))
            GlobalConfig.option.multipleVulnerabilityMode();
        if (cli.hasOption("quiet"))
            GlobalConfig.option.quiet();
        if (cli.hasOption("staticTimeout")) {
            int timeout = Integer.parseInt(cli.getOptionValue("staticTimeout"));
            GlobalConfig.option.setStaticTimeout(timeout);
        }
        if (cli.hasOption("totalTimeout")) {
            int timeout = Integer.parseInt(cli.getOptionValue("totalTimeout"));
            GlobalConfig.option.setTotalTimeout(timeout);
        }
        if (cli.hasOption("threadNumber")) {
            int number = Integer.parseInt(cli.getOptionValue("threadNumber"));
            GlobalConfig.option.setThreadNumber(number);
        }
        if (cli.hasOption("single")) {
            String b64regex = cli.getOptionValue("single");
            JSONObject jsonObject = Batch.handleSingleRegex(0, b64regex, true);
            System.out.println(jsonObject);
        } else {
            helpFormatter.printHelp("Rengar", options);
        }
        GlobalConfig.executor.shutdownNow();
    }

    private static CommandLine initCommandArgument(String[] args) {
        Option opt2 = new Option(
                "d",
                "disablePreprocess",
                false,
                "disable regex preprocess"
        );
        opt2.setRequired(false);
        Option opt3 = new Option(
                "w",
                "weakPatternCheck",
                false,
                "weak ReDoS pattern check"
        );
        opt3.setRequired(false);
        Option opt4 = new Option(
                "i",
                "ingoreDisturbance",
                false,
                "ingore disturbance"
        );
        opt4.setRequired(false);
        Option opt6 = new Option(
                "q",
                "quiet",
                false,
                "less output information"
        );
        opt6.setRequired(false);
        Option opt7 = new Option(
                "s",
                "single",
                true,
                "base64ed regex"
        );
        opt7.setRequired(false);
        Option opt8 = new Option(
                "t",
                "id",
                true,
                "id"
        );
        opt8.setRequired(false);
        Option opt9 = new Option(
                "st",
                "staticTimeout",
                true,
                "static checker timeout"
        );
        opt9.setRequired(false);
        Option opt10 = new Option(
                "tt",
                "totalTimeout",
                true,
                "total timeout"
        );
        opt10.setRequired(false);
        Option opt11 = new Option(
                "tn",
                "threadNumber",
                true,
                "thread number"
        );
        opt11.setRequired(false);
        options.addOption(opt2);
        options.addOption(opt3);
        options.addOption(opt4);
        options.addOption(opt6);
        options.addOption(opt7);
        options.addOption(opt8);
        options.addOption(opt9);
        options.addOption(opt10);
        options.addOption(opt11);
        try {
            return cliParser.parse(options, args);
        } catch (ParseException e) {
            helpFormatter.printHelp("Rengar", options);
            System.exit(0);
        }
        return null;
    }

    static class Batch {
        public static JSONObject handleSingleRegex(int id, String patternStr) {
            return handleSingleRegex(id, patternStr, false);
        }

        public static JSONObject handleSingleRegex(int id, String patternStr, boolean base64) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ID", id);
            String b64Regex;
            if (base64) {
                b64Regex = patternStr;
                patternStr = b64decode(patternStr);
            } else {
                b64Regex = b64encode(patternStr);
            }
            jsonObject.put("Regex", b64Regex);
            StaticPipeline.Result result = StaticPipeline.runWithTimeOut(
                    patternStr,
                    RegexParser.Language.Java,
                    true
            );
            if (result == null) {
                jsonObject.put("Status", "Timeout");
            } else {
                switch (result.state) {
                    case InternalBug -> jsonObject.put("Status", "InternalBug");
                    case SyntaxError -> jsonObject.put("Status", "SyntaxError");
                    case Normal -> jsonObject.put("Status", "Safe");
                    case Vulnerable -> {
                        jsonObject.put("Status", "Vulnerable");
                        List<JSONObject> patternList = new LinkedList<>();
                        for (Pair<DisturbFreePattern, StringProvider> pair : result.attacks) {
                            DisturbFreePattern pattern = pair.getLeft();
                            StringProvider sp = pair.getRight();
                            JSONObject patternObj = new JSONObject();
                            patternObj.put("Type", pattern.getType());
                            patternObj.put("AttackString", b64encode(sp.genReadableStr()));
                            patternList.add(patternObj);
                        }
                        jsonObject.put("Details", patternList);
                        jsonObject.put("DisturbType", result.disturbType.getTypes());
                    }
                }
            }
            return jsonObject;
        }

        private static String b64encode(String str) {
            return new String(
                    Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8
            );
        }

        private static String b64decode(String str) {
            return new String(
                    Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8)),
                    StandardCharsets.UTF_8
            );
        }
    }
}