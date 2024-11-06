package rengar.checker;

import rengar.checker.pattern.*;
import rengar.checker.util.RegexUtil;
import rengar.config.GlobalConfig;
import rengar.parser.exception.PatternSyntaxException;
import rengar.parser.RegexParser;
import rengar.parser.ast.*;
import rengar.preprocess.PreprocessPipeline;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ReDoSChecker {
    private final RegexExpr regexExpr;
    private final Set<ReDoSPattern> patterns = new HashSet<>();

    public ReDoSChecker(RegexExpr regexExpr) {
        this.regexExpr = regexExpr;
    }

    public String getPatternStr() {
        return regexExpr.genString();
    }

    public RegexExpr getRegexExpr() {
        return regexExpr;
    }

    public List<ReDoSPattern> getPatterns() {
        return patterns.stream().toList();
    }

    public void analyse() {
        NQPatternReDoSChecker nqChecker = new NQPatternReDoSChecker();
        EODPatternReDoSChecker eodChecker = new EODPatternReDoSChecker();
        EOAPatternReDoSChecker eoaChecker = new EOAPatternReDoSChecker();
        POAPatternReDoSChecker poaChecker = new POAPatternReDoSChecker();
        SLQPatternReDoSChecker slqChecker = new SLQPatternReDoSChecker();

        Future<Void> future = GlobalConfig.executor.submit(() -> {
                try {
                    nqChecker.analyse(regexExpr);
                    eodChecker.analyse(regexExpr);
                    eoaChecker.analyse(regexExpr);
                    RegexExpr preprocess = RegexUtil.preprocessForPoaSlq(regexExpr);
                    poaChecker.analyse(preprocess);
                    slqChecker.analyse(preprocess);
                } catch (InterruptedException ignored) {}
                return null;
            }
        );
        try {
            future.get(GlobalConfig.option.getStaticTimeout(), TimeUnit.SECONDS);
        } catch (Exception ignored) {
            future.cancel(true);
        }

        patterns.addAll(nqChecker.getPatterns());
        patterns.addAll(slqChecker.getPatterns());
        patterns.addAll(eodChecker.getPatterns());
        patterns.addAll(eoaChecker.getPatterns());
        patterns.addAll(poaChecker.getPatterns());
    }

    public void print() {
        System.out.printf("check string: %s\n", getPatternStr());
        for (ReDoSPattern pattern : patterns) {
            System.out.println(pattern);
        }
    }
}
