package rengar.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlobalConfig {
    // you can change these values to adjust the performance and result of the tool
    public static final int MatchingStepUpperBound = 100000000; 
    public static final int MaxYStringSum = 100000;

    public static final int MaxYStringLengthForNQ = 45;
    public static final int MaxYStringLengthForEOD = 40;
    public static final int MaxYStringLengthForEOA = 120;
    public static final int MaxYStringLengthForSLQ = 10000;
    public static final int MatchingStepUpperBoundForPartialAnalysis = 100000; // Do not Change
    public static final int MaxYStringLengthForPOA = 10000; // Do not Change
    
    
    public static Option option = new Option();
    public static ExecutorService executor = Executors.newCachedThreadPool();
}
