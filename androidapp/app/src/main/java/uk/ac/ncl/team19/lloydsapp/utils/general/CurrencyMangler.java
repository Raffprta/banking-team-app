package uk.ac.ncl.team19.lloydsapp.utils.general;

/**
 * @author Dale Whinham
 *
 * A class to provide string "prettying" methods to be used when handling currency on the application.
 */
public class CurrencyMangler {
    public static String integerToSterlingString(Long i) {
        return String.format("£%.2f", i / 100.0);
    }

    public static Long sterlingStringToInteger(String s) {
        return (long) (Double.parseDouble(s) * 100);
    }
}