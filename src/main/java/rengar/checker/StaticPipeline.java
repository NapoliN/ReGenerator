package rengar.checker;

import rengar.checker.attack.AttackString;
import rengar.checker.attack.StringProvider;
import rengar.checker.pattern.*;
import rengar.checker.vulnerability.Vulnerability;
import rengar.config.GlobalConfig;
import rengar.dynamic.validator.Validator;
import rengar.parser.ReDosHunterPreProcess;
import rengar.parser.RegexParser;
import rengar.parser.ast.RegexExpr;
import rengar.parser.exception.PatternSyntaxException;
import rengar.util.Pair;
import java.text.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import rengar.checker.vulnerability.*;

public class StaticPipeline {
    public static class Result {
        public enum State {
            SyntaxError, Vulnerable, InternalBug, Normal
        }

        public DisturbType disturbType;
        public long runningTime;
        public State state;
        public List<Pair<DisturbFreePattern, StringProvider>> attacks = new LinkedList<>();
        public List<Vulnerability> vulnerabilities = new ArrayList<>();

        public void add(DisturbFreePattern newPattern, StringProvider newAttackString) {
            boolean isOK = true;
            for (Pair<DisturbFreePattern, StringProvider> pair : attacks) {
                DisturbFreePattern pattern = pair.getLeft();
                StringProvider attackString = pair.getRight();
                if (attackString.equals(newAttackString)) {
                    isOK = false;
                    break;
                }
                if (pattern.isDuplicate(newPattern)) {
                    isOK = false;
                    break;
                }
            }
            if (isOK) {
                attacks.add(new Pair<>(newPattern, newAttackString));
            }
        }

        public void addVuln(Vulnerability vuln) {
            vulnerabilities.add(vuln);
        }

        public void printVulnerabilitiesAST() {
            StringBuilder sb = new StringBuilder();
            // [を出力
            sb.append("[");
            for (int i = 0; i < vulnerabilities.size(); i++) {
                Vulnerability vuln = vulnerabilities.get(i);
                sb.append(vuln.toJSONString());
                if (i != vulnerabilities.size() - 1) {
                    sb.append(",");
                }
            }
            // ]を出力
            sb.append("]");
            System.out.println(sb.toString());
        }
    }

    public static Result runWithTimeOut(String patternStr, RegexParser.Language language, boolean findAll) {
        Future<Result> future = GlobalConfig.executor.submit(
                () -> StaticPipeline.run(patternStr, language, findAll));
        try {
            return future.get(GlobalConfig.option.getTotalTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException ignored) {
            // スタックトレースの出力
            ignored.printStackTrace();
            future.cancel(true);
            return null;
        }
    }

    public static Result run(String patternStr, RegexParser.Language language, boolean findAll) {
        if (!GlobalConfig.option.isQuiet())
            System.out.printf("%s pattern string %s\n", getCurrentDate(), patternStr);
        long startTime = System.currentTimeMillis();
        Result result = new Result();
        result.state = Result.State.Normal;

        // preprocess: standardize the pattern string
        if (!GlobalConfig.option.isDisablePreprocess()) {
            patternStr = ReDosHunterPreProcess.process(patternStr);
            if (patternStr == null) {
                result.state = Result.State.SyntaxError;
                result.runningTime = System.currentTimeMillis() - startTime;
                return result;
            }
            if (!GlobalConfig.option.isQuiet())
                System.out.printf("ReDosHunter rengar.preprocess result %s\n", patternStr);
        }

        // step 1. parse the pattern string
        RegexExpr targetExpr;
        try {
            RegexParser parser = RegexParser.createParser(language, patternStr);
            targetExpr = parser.parse();
            System.out.println(String.format("regex AST: %s", targetExpr.genJsonExpression()));
        } catch (PatternSyntaxException e) {
            if (!GlobalConfig.option.isQuiet())
                System.out.println(e);
            result.state = Result.State.SyntaxError;
            result.runningTime = System.currentTimeMillis() - startTime;
            return result;
        }

        // step 2. detect ReDoS pattern
        DisturbFreeChecker checker;
        try {
            checker = new DisturbFreeChecker(targetExpr);
        } catch (Exception | StackOverflowError | OutOfMemoryError e) {
            if (!GlobalConfig.option.isQuiet())
                System.out.println(e);
            result.state = Result.State.InternalBug;
            result.runningTime = System.currentTimeMillis() - startTime;
            return result;
        }

        // step 3. generate attack strings and validate them
        List<DisturbFreePattern> patternList = checker.getFreePatterns();
        var count = 0;
        var flagEOLS = false;
        // validate each part of vulnerability pattern
        for (DisturbFreePattern pattern : patternList) {
            if (Thread.currentThread().isInterrupted())
                return null;
            try {
                // EOLSなら本検証
                // PTLS, POLSなら部分脆弱性としての検証
                AttackString attackStr = handleReDoSPattern(patternStr, pattern, result);
                if (attackStr != null) {
                    result.state = Result.State.Vulnerable;
                    result.add(pattern, attackStr);
                    ReDoSPattern redosPattern = pattern.getPattern();
                    switch (redosPattern) {
                        case POAPattern poa:
                            count += 1;
                            result.addVuln(new Vulnerability(poa, attackStr, count));
                            break;
                        case EOAPattern eoa:
                            flagEOLS = true;
                            break;
                        case EODPattern eod:
                            flagEOLS = true;
                            break;
                        default:
                            break;
                    }
                    if (!findAll)
                        break;
                }
            } catch (PatternSyntaxException ignored) {
            }
        }

        // if there is no exponential vulnerability and are more than one polynomial
        // vulnerabilities, require addtional analysis
        // step 4. generate multi-vulnerability attack string
        if (!flagEOLS && count > 1) {
            result.printVulnerabilitiesAST();

            // construct DAG for vulnerabilities
            DAG dag = new DAG();
            for (int i = 0; i < result.vulnerabilities.size(); i++) {
                Vulnerability vuln1 = result.vulnerabilities.get(i);
                dag.addNode(i);
                for (int j = i + 1; j < result.vulnerabilities.size(); j++) {
                    Vulnerability vuln2 = result.vulnerabilities.get(j);
                    var compare = Vulnerability.compare(vuln1, vuln2);
                    if (compare == Vulnerability.Comparator.PRECEED) {
                        dag.addEdge(i, j);
                    } else if (compare == Vulnerability.Comparator.FOLLOW) {
                        dag.addEdge(j, i);
                    }
                }
            }

            // search longest increasing sequence
            DAGDFS dfs = new DAGDFS(dag);
            List<Integer> longestPath = dfs.findLongestIncreasingSequence();
            // longestPathを出力する
            System.out.print("Longest Path:");
            for (int i = 0; i < longestPath.size(); i++) {
                System.out.printf("%d ", longestPath.get(i));
            }
            System.out.println();

            List<Vulnerability> longestVuln = new ArrayList<>();
            for (int i = 0; i < longestPath.size(); i++) {
                longestVuln.add(result.vulnerabilities.get(longestPath.get(i)));
            }

            // generate multi-vulnerability attack string
            MultiVulnPattern multiVulnPattern = new MultiVulnPattern(checker.getRegexExpr(), longestVuln);
            MultiVulnAttackString multiVulnAttackString = multiVulnPattern.getMultiVulnAttackString();
            System.out.println(multiVulnAttackString.genReadableString());
            System.out.println("entire length: " + multiVulnAttackString.genStr().length());
            System.out.println("estimated step: " + multiVulnAttackString.estimatedMatchingStep().toString());

            // validate multi-vulnerability attack string
            Validator validator = new Validator(patternStr, "MPV");
            if (validator.validate(multiVulnAttackString.genStr(), GlobalConfig.MatchingStepUpperBound)) {
                result.state = Result.State.Vulnerable;
                System.out.println("Multiple Vulnerability Found");
            }

        }

        long endTime = System.currentTimeMillis();
        result.runningTime = endTime - startTime;
        if (!GlobalConfig.option.isQuiet())
            System.out.printf(
                    "%s. It takes %f seconds\n",
                    getCurrentDate(), (double) result.runningTime / 1000);
        DisturbType type = new DisturbType();
        /**
        for (Pair<DisturbFreePattern, StringProvider> pair : result.attacks) {
            StringProvider as = pair.getRight();
            type.setType(as.getDisturbType());
        }
        */
        if (!checker.hasBranch()) {
            type.getTypes().remove(DisturbType.Type.Case1);
            type.getTypes().remove(DisturbType.Type.Case2);
            type.getTypes().remove(DisturbType.Type.Case3);
            if (type.getTypes().isEmpty()) {
                type.getTypes().add(DisturbType.Type.None);
            }
        }
        result.disturbType = type;
        return result;
    }

    private static AttackString handleReDoSPattern(String patternStr, DisturbFreePattern pattern, Result result)
            throws PatternSyntaxException {
        if (!GlobalConfig.option.isQuiet())
            System.out.println(pattern);
        List<AttackString> attackStrList;
        // if pattern is exponential pattern, use user designated upperbound, otherwise use partial analysis upperbound
        int upperBound = pattern.getPattern() instanceof EOAPattern || pattern.getPattern() instanceof EODPattern ? GlobalConfig.MatchingStepUpperBound : GlobalConfig.MatchingStepUpperBoundForPartialAnalysis;
        try {
            attackStrList = pattern.generate();
        } catch (Exception | Error ignored) {
            if (!GlobalConfig.option.isQuiet())
                System.out.println("ERROR");
            return null;
        }

        for (AttackString attackStr : attackStrList) {
            if (Thread.currentThread().isInterrupted())
                break;
            if (!GlobalConfig.option.isQuiet())
                System.out.printf("try %s ", attackStr.genReadableStr());
            try {
                Validator validator = new Validator(patternStr, pattern.getType());
                if (validator.validate(attackStr.genStr(), upperBound)) {
                    if (!GlobalConfig.option.isQuiet())
                        System.out.println("SUCCESS");
                    return attackStr;
                } else {
                    if (!GlobalConfig.option.isQuiet())
                        System.out.println("FAILED");
                }
            } catch (rengar.dynamic.jdk8.regex.PatternSyntaxException e) {
                if (!GlobalConfig.option.isQuiet())
                    System.out.println("SYNTAX ERROR");
            }
        }
        return null;
    }

    private static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
