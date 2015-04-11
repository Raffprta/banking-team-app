package uk.ac.ncl.team19.lloydsapp.utils.general;

/**
 * Created by Dale Whinham on 03/04/15.
 */
public class CurrencyMangler {
    public static String integerToSterlingString(Long i) {
        return String.format("£%.2f", i / 100.0);
    }

    public static Long sterlingStringToInteger(String s) {
        return Long.valueOf((long) (Double.parseDouble(s) * 100));
    }
}