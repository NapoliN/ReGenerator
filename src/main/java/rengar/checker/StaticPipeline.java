package rengar.checker;

import rengar.checker.attack.AttackString;
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
        public List<Pair<DisturbFreePattern, AttackString>> attacks = new LinkedList<>();
        public List<Vulnerability> vulnerabilities = new ArrayList<>();

        public void add(DisturbFreePattern newPattern, AttackString newAttackString) {
            boolean isOK = true;
            for (Pair<DisturbFreePattern, AttackString> pair : attacks) {
                DisturbFreePattern pattern = pair.getLeft();
                AttackString attackString = pair.getRight();
                if (attackString.equals(newAttackString)
                        && attackString.getDisturbType() == newAttackString.getDisturbType()) {
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

        public void addVuln(Vulnerability vuln){
            vulnerabilities.add(vuln);
        }

        public void printVulnerabilitiesAST(){
            System.out.println("hoge");
            StringBuilder sb = new StringBuilder();
            // [を出力
            sb.append("[");
            for (int i=0; i < vulnerabilities.size(); i++){
                Vulnerability vuln = vulnerabilities.get(i);
                sb.append(vuln.toJSONString());
                if (i != vulnerabilities.size()-1){
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
                () -> StaticPipeline.run(patternStr, language, findAll)
        );
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

        // preprocess: 正規表現を解析できる形に正規化（文字列的として置き換える）
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

        // step 1. parse regex string
        RegexExpr targetExpr;
        try {
            RegexParser parser = RegexParser.createParser(language, patternStr);
            targetExpr = parser.parse();
            System.out.println(String.format("regex AST: %s", targetExpr.genJsonExpression()));
        }
        catch (PatternSyntaxException e){
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

        List<DisturbFreePattern> patternList = checker.getFreePatterns();
        var count = 0;
        for (DisturbFreePattern pattern : patternList) {
            if (Thread.currentThread().isInterrupted())
                return null;
            try {
                AttackString attackStr = handleReDoSPattern(patternStr, pattern, result);
                if (attackStr != null) {
                    result.state = Result.State.Vulnerable;
                    result.add(pattern, attackStr);
                    if (pattern.getPattern() instanceof POAPattern){
                        count += 1;
                        POAPattern poa = (POAPattern) pattern.getPattern();
                        result.addVuln(new Vulnerability(poa, attackStr, count));
                    }
                    if (!findAll)
                        break;
                }
            } catch (PatternSyntaxException ignored) {}
        }

        System.out.println("conventional analysis finished");
        result.printVulnerabilitiesAST();

        // グラフを構築
        DAG dag = new DAG();
        for (int i=0; i < result.vulnerabilities.size(); i++){
            Vulnerability vuln1 = result.vulnerabilities.get(i);
            dag.addNode(i);
            for (int j=i+1; j < result.vulnerabilities.size(); j++){
                Vulnerability vuln2 = result.vulnerabilities.get(j);
                var compare = Vulnerability.compare(vuln1, vuln2);
                if (compare == Vulnerability.Comparator.PRECEED){
                    dag.addEdge(i, j);
                }
                else if (compare == Vulnerability.Comparator.FOLLOW){
                    dag.addEdge(j, i);
                }
            }
        }

        System.out.println("DAG constructed");

        // 最長増加列を求める
        DAGDFS dfs = new DAGDFS(dag);
        List<Integer> longestPath = dfs.findLongestIncreasingSequence();
        // longestPathを出力する
        System.out.println("Longest Path:");
        for (int i=0; i < longestPath.size(); i++){
            System.out.printf("%d ",longestPath.get(i));
        }
        System.out.println();

        List<Vulnerability> longestVuln = new ArrayList<>();
        for (int i=0; i < longestPath.size(); i++){
            longestVuln.add(result.vulnerabilities.get(longestPath.get(i)));
        }
        
        MultiVulnPattern multiVulnPattern = new MultiVulnPattern(checker.getRegexExpr(), longestVuln);
        MultiVulnAttackString multiVulnAttackString = multiVulnPattern.getMultiVulnAttackString();
        System.out.println(multiVulnAttackString.genReadableString());
        
        long endTime = System.currentTimeMillis();
        result.runningTime = endTime - startTime;
        if (!GlobalConfig.option.isQuiet())
            System.out.printf(
                    "%s. It takes %f seconds\n",
                    getCurrentDate(), (double)result.runningTime / 1000);
        DisturbType type = new DisturbType();
        for (Pair<DisturbFreePattern, AttackString> pair : result.attacks) {
            AttackString as = pair.getRight();
            type.setType(as.getDisturbType());
        }
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

    private static <T> List<T> tryPopN(int n, List<T> lists) {
        List<T> results = new LinkedList<>();
        while (results.size() != n && !lists.isEmpty()) {
            T elem = lists.get(0);
            lists.remove(0);
            results.add(elem);
        }
        return results;
    }

    public static AttackString handleReDoSPattern(String patternStr,
                                                   DisturbFreePattern pattern,
                                              Result result)
            throws PatternSyntaxException {
        if (!GlobalConfig.option.isQuiet())
            System.out.println(pattern);
        List<AttackString> attackStrList;
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
                Validator validator = new Validator(patternStr, attackStr, pattern.getType());
                if (validator.isVulnerable()) {
                    if (!GlobalConfig.option.isQuiet())
                        System.out.println("SUCCESS");
                    return attackStr;
                }
                else {
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

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }
}
