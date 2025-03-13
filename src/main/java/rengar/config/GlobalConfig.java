package rengar.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalConfig {
    public static final int MaxYStringLengthForNQ = 1000;
    public static final int MaxYStringLengthForEOD = 1000;
    public static final int MaxYStringLengthForEOA = 120;
    public static final int MaxYStringLengthForSLQ = 10000; // Do not Change
    public static final int MatchingStepUpperBoundForPartialAnalysis = 100_000; // Do not Change
    public static final int MaxYStringLengthForPOA = 10000; // Do not Change
    
    
    public static Option option = new Option();
    public static ExecutorService executor = Executors.newCachedThreadPool();
}
