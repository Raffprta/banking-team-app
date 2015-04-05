package uk.ac.ncl.team19.lloydsapp.utils.general;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Raffaello Perrotta - Class to hold general numeric constants which will be used throughout the application.
 */
public class Constants {

    // health thresholds
    public final static int HEALTH_PERFECT = 100;
    public final static int HEALTH_GOOD = 75;
    public final static int HEALTH_AVG = 50;
    public final static int HEALTH_POOR = 25;

    // health minimums
    public final static double MIN_SPEND = 0;
    public final static double MIN_SAVE = 5;
    public final static double MIN_OVERDRAFT = 0;

    // health enum
    public final static int WEEKLY = 2;
    public final static int MONTHLY = 1;

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

    // Auto-log off in 5 minutes
    public static final int TIME_MILLIS_LOGOFF = 5 * 1000 * 60;

    // Minimum security code length
    public static final int MINIMUM_SECURITY_CODE_LENGTH = 6;

    // Bundle keys for inter-fragment data transfer
    public static final String BUNDLE_KEY_HELP = "help";
    public static final String BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE = "message";
    public static final String BUNDLE_KEY_CUSTOM_DIALOG_IS_ERROR = "isError";

    public static final String BUNDLE_KEY_USERNAME = "username";
    public static final String BUNDLE_KEY_PASSWORD = "password";

    public static final String BUNDLE_KEY_CURRENT_ACCOUNT = "currentAccount";
    public static final String BUNDLE_KEY_ACCOUNT_LIST = "accountList";

    public static final String BUNDLE_KEY_FROM_ACC = "fromAcc";
    public static final String BUNDLE_KEY_TO_REF = "toRef";
    public static final String BUNDLE_KEY_TO_ACC_NO = "toAccNo";
    public static final String BUNDLE_KEY_TO_SORT_CODE = "toSortCode";
    public static final String BUNDLE_KEY_AMOUNT = "amount";

    public static final String BUNDLE_KEY_GOALS_EXPIRY = "expiry";

    // Shared preferenes keys.
    public static final String SP_USERNAME = "spUsername";
    public static final String SP_STATUS = "STATUS";
    public static final String SP_BIO = "BIO";
    public static final String SP_LOGINS = "LOGINS";
    public static final String SP_FIRST_LOGIN = "FIRST_LOGIN";
    public static final String SP_ACH_BRANCH_EXPLORER = "ACH_BRANCH_EXP";
    public static final String SP_ACH_BRANCH_EXPLORER_MUTEX = "ACH_BRANCH_EXP_MUTEX";
    public static final String SP_ACH_FIRST_LOGIN = "ACH_FIRST_LOGIN";
    public static final String SP_ACH_GOLD_LOGIN = "ACH_GOLD_LOGIN";
    public static final String SP_ACH_SILVER_LOGIN = "ACH_SILVER_LOGIN";
    public static final String SP_ACH_BRONZE_LOGIN = "ACH_BRONZE_LOGIN";
    public static final String SP_ACH_OLD_TIMER = "ACH_OLD_TIMER";
    public static final String SP_LAST_DATE_INC = "LAST_INC";
    public static final String SP_MAGIC_NO = "MAGIC_NO";
    public static final String SP_MAGIC_NO_MUTEX = "MAGIC_NO_MUTEX";
    public static final String SP_GOALS_SET = "GOALS_SET";
    public static final String SP_GOALS_SET_FOR = "GOALS_SET_FOR";
    public static final String SP_GOALS_START = "GOALS_START";
    public static final String SP_GOALS_SPEND = "GOALS_SPEND";
    public static final String SP_GOALS_SAVE = "GOALS_SAVE";
    public static final String SP_GOALS_OVERDRAFT = "GOALS_OVERDRAFT";
    public static final String SP_GOALS_DONATE = "GOALS_DONATE";
    public static final String SP_PLAYID = "PLAYID";

    public static final String SP_DEVICE_TOKEN = "DEVICE_TOKEN";

    public static final String SP_ACCOUNTS_HP = "HP";

}
