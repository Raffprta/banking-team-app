package uk.ac.ncl.team19.lloydsapp.utils.general;

/**
 * @author Dale Whinham
 *
 * A class to provide string "prettying" methods to be used when handling currency on the application.
 */
public class CurrencyMangler {
    private static final String STERLING_FORMATTING_STRING = "£%.2f";

    public static String integerToSterlingString(Long i) {
        return String.format(STERLING_FORMATTING_STRING, i / 100.0);
    }

    public static String floatToSterlingString(Float f) {
        return String.format(STERLING_FORMATTING_STRING, f);
    }

    public static Long sterlingStringToInteger(String s) {
        return (long) (Double.parseDouble(s) * 100);
    }
}