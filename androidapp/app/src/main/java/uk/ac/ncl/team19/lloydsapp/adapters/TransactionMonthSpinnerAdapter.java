package uk.ac.ncl.team19.lloydsapp.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import uk.ac.ncl.team19.lloydsapp.api.datatypes.Transaction;

/**
 * @author Dale Whinham
 *
 * This class provides a spinner adapter to display transactions by individual years.
 */
public class TransactionMonthSpinnerAdapter extends ArrayAdapter<String> {
    public TransactionMonthSpinnerAdapter(Context ctx, List<Transaction> transactions) {
        super(ctx, android.R.layout.simple_spinner_dropdown_item);

        SimpleDateFormat df = new SimpleDateFormat("MMMM", Locale.ROOT);

        // Get all the unique years from the transactions
        List<String> years = new ArrayList<>();
        for (Transaction t: transactions) {
            String yearString = df.format(t.getDate());
            if (!years.contains(yearString)) {
                years.add(yearString);
            }
        }

        // Add them to the adapter
        Collections.sort(years);
        addAll(years);
    }
}