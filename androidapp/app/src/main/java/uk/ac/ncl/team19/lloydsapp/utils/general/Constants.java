package uk.ac.ncl.team19.lloydsapp.utils.general;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Raffaello Perrotta - Class to hold general numeric constants which will be used throughout the application.
 */
public class Constants {

    // health thresholds
    public final static int HEALTH_GOOD = 75;
    public final static int HEALTH_AVG = 50;
    public final static int HEALTH_POOR = 25;

    // health minimums
    public final static double MIN_SPEND = 0;
    public final static double MIN_SAVE = 5;
    public final static double MIN_OVERDRAFT = 0;

    // validation for bank numbers
    public final static int ACC_NO_SIZE = 8;
    public final static int SORT_CODE_NO_SIZE = 6;

    // achievement related
    public final static int GOLD_LOGIN = 100;
    public final static int SILVER_LOGIN = 50;
    public final static int BRONZE_LOGIN = 25;
    public final static int OLD_TIMER = 30;
    public final static int LLOYDS_VICINITY = 20;
    public final static int MAGIC_NO = 666; // 1 in 10,000 chance of getting the magic number
    public final static int MAGIC_NO_SEEDER = 10000;

    // Lookup table for achievement points, maps Id (string) -> Points (int)
    // Static immutable hashmap is used.
    public static final Map<String, Integer> ACHIEVEMENTS_POINTS;
    static {
        Map<String, Integer> aMap = new HashMap<>();
        aMap.put("CgkIpMCHuIkUEAIQAQ", 25);
        aMap.put("CgkIpMCHuIkUEAIQAw", 5);
        aMap.put("CgkIpMCHuIkUEAIQBQ", 10);
        aMap.put("CgkIpMCHuIkUEAIQCg", 30);
        aMap.put("CgkIpMCHuIkUEAIQCw", 30);
        aMap.put("CgkIpMCHuIkUEAIQDA", 15);
        aMap.put("CgkIpMCHuIkUEAIQDQ", 10);
        ACHIEVEMENTS_POINTS = Collections.unmodifiableMap(aMap);
    }

}
