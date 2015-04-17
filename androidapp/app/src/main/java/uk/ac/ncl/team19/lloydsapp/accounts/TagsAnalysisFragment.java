package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.adapters.TransactionMonthSpinnerAdapter;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.Transaction;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.CurrencyMangler;
import uk.ac.ncl.team19.lloydsapp.utils.general.PieChartView;

/**
 * @author Dale Whinham
 * This fragment allows the user to view a pie chart showing the proportion of their expenditure
 * based on the tag they have given each transaction.
 */
public class TagsAnalysisFragment extends Fragment {

    private Bundle args;
    private String year;
    private List<Transaction> allTransactions;

    private TextView title;
    private CheckBox wholeYear;
    private CheckBox showValues;
    private Spinner monthSpinner;
    private PieChartView pieChartView;

    private TextView tagUntagged;
    private TextView tagFoodDrink;
    private TextView tagClothes;
    private TextView tagWithrawal;
    private TextView tagEntertainment;
    private TextView tagOther;
    private TextView tagUtility;
    private TextView tagTransport;
    private TextView tagDonation;

    // Map to hold totals for tags
    private Map<Transaction.Tag, Long> tagMap;

    private static final SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM", Locale.ROOT);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View tagsAnalysisView = inflater.inflate(R.layout.tags_analysis, container, false);

        // References to views
        title = (TextView) tagsAnalysisView.findViewById(R.id.title);
        pieChartView = (PieChartView) tagsAnalysisView.findViewById(R.id.pieChart);
        wholeYear = (CheckBox) tagsAnalysisView.findViewById(R.id.wholeYear);
        showValues = (CheckBox) tagsAnalysisView.findViewById(R.id.showValues);
        monthSpinner = (Spinner) tagsAnalysisView.findViewById(R.id.monthSpinner);

        tagUntagged = (TextView) tagsAnalysisView.findViewById(R.id.tagUntagged);
        tagFoodDrink = (TextView) tagsAnalysisView.findViewById(R.id.tagFoodDrink);
        tagClothes = (TextView) tagsAnalysisView.findViewById(R.id.tagClothes);
        tagWithrawal = (TextView) tagsAnalysisView.findViewById(R.id.tagWithdrawal);
        tagEntertainment = (TextView) tagsAnalysisView.findViewById(R.id.tagEntertainment);
        tagOther = (TextView) tagsAnalysisView.findViewById(R.id.tagOther);
        tagUtility = (TextView) tagsAnalysisView.findViewById(R.id.tagUtility);
        tagTransport = (TextView) tagsAnalysisView.findViewById(R.id.tagTransport);
        tagDonation = (TextView) tagsAnalysisView.findViewById(R.id.tagDonation);

        // Retrieve transactions from arguments
        args = getArguments();
        year = args.getString(Constants.BUNDLE_KEY_YEAR);
        allTransactions = (List<Transaction>) args.getSerializable(Constants.BUNDLE_KEY_TRANSACTIONS_LIST);

        // Setup initial view state
        title.setText(getString(R.string.analysis_withdrawals_in) + " " + year);
        wholeYear.setChecked(true);
        showValues.setChecked(false);
        monthSpinner.setEnabled(false);
        updateTagMap(allTransactions);
        updatePieChart();
        updateTagKey(false);

        // Setup spinner adaptor
        monthSpinner.setAdapter(new TransactionMonthSpinnerAdapter(getActivity(), allTransactions));

        // Listener for when the whole year checkbox is changed
        wholeYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Month spinner only enabled if unchecked
                monthSpinner.setEnabled(!isChecked);

                if (isChecked) {
                    updateTagMap(allTransactions);
                    updatePieChart();
                    updateTagKey(showValues.isChecked());
                } else {
                    String selectedMonth = (String) monthSpinner.getSelectedItem();
                    updateTagMap(getTransactionsForMonth(selectedMonth));
                    updatePieChart();
                    updateTagKey(showValues.isChecked());
                }
            }
        });

        // Listener for when the show values checkbox is changed
        showValues.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateTagKey(isChecked);
            }
        });

        // Listener for when a month is selected
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = (String) monthSpinner.getSelectedItem();
                updateTagMap(getTransactionsForMonth(selectedMonth));
                updatePieChart();
                updateTagKey(showValues.isChecked());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return tagsAnalysisView;
    }

    // Updates map that holds totals for each tag
    private void updateTagMap(List<Transaction> transactions) {
        // Add up totals for tags
        tagMap = new EnumMap<>(Transaction.Tag.class);
        for (Transaction t : transactions) {
            Transaction.Tag tag = t.getTag();
            if (tagMap.containsKey(tag)) {
                tagMap.put(tag, tagMap.get(tag) + t.getAmount());
            } else {
                tagMap.put(tag, t.getAmount());
            }
        }
    }

    // Refreshes pie chart
    private void updatePieChart() {
        // Add wedges to pie chart
        pieChartView.removeAllSlices();
        for (Transaction.Tag t : tagMap.keySet()) {
            pieChartView.addSlice(tagMap.get(t), getResources().getColor(Transaction.Tag.getColorIdForTag(t)));
        }
        pieChartView.generatePath();
    }

    // Updates tag key with values/descriptions
    private void updateTagKey(boolean showValues) {
        if (showValues) {
            tagUntagged.setText(CurrencyMangler.integerToSterlingString(tagMap.containsKey(Transaction.Tag.UNTAGGED) ? tagMap.get(Transaction.Tag.UNTAGGED) : 0));
            tagFoodDrink.setText(CurrencyMangler.integerToSterlingString(tagMap.containsKey(Transaction.Tag.FOODDRINK) ? tagMap.get(Transaction.Tag.FOODDRINK) : 0));
            tagClothes.setText(CurrencyMangler.integerToSterlingString(tagMap.containsKey(Transaction.Tag.CLOTHES) ? tagMap.get(Transaction.Tag.CLOTHES) : 0));
            tagWithrawal.setText(CurrencyMangler.integerToSterlingString(tagMap.containsKey(Transaction.Tag.WITHDRAWAL) ? tagMap.get(Transaction.Tag.WITHDRAWAL) : 0));
            tagEntertainment.setText(CurrencyMangler.integerToSterlingString(tagMap.containsKey(Transaction.Tag.ENTERTAINMENT) ? tagMap.get(Transaction.Tag.ENTERTAINMENT) : 0));
            tagOther.setText(CurrencyMangler.integerToSterlingString(tagMap.containsKey(Transaction.Tag.OTHER) ? tagMap.get(Transaction.Tag.OTHER) : 0));
            tagUtility.setText(CurrencyMangler.integerToSterlingString(tagMap.containsKey(Transaction.Tag.UTILITY) ? tagMap.get(Transaction.Tag.UTILITY) : 0));
            tagTransport.setText(CurrencyMangler.integerToSterlingString(tagMap.containsKey(Transaction.Tag.TRANSPORT) ? tagMap.get(Transaction.Tag.TRANSPORT) : 0));
            tagDonation.setText(CurrencyMangler.integerToSterlingString(tagMap.containsKey(Transaction.Tag.DONATION) ? tagMap.get(Transaction.Tag.DONATION) : 0));
        } else {
            tagUntagged.setText(getString(R.string.tag_untagged));
            tagFoodDrink.setText(getString(R.string.tag_food_drink));
            tagClothes.setText(getString(R.string.tag_clothes));
            tagWithrawal.setText(getString(R.string.tag_withdrawal));
            tagEntertainment.setText(getString(R.string.tag_entertainment));
            tagOther.setText(getString(R.string.tag_other));
            tagUtility.setText(getString(R.string.tag_utility));
            tagTransport.setText(getString(R.string.tag_transport));
            tagDonation.setText(getString(R.string.tag_donation));
        }
    }

    // Filters a list of transactions by a given year
    private List<Transaction> getTransactionsForMonth(String month) {
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction t: allTransactions) {
            String transactionYear = monthFormatter.format(t.getDate());
            if (transactionYear.equals(month)) {
                filteredTransactions.add(t);
            }
        }

        return filteredTransactions;
    }

    @Override
    public String toString(){
        return getString(R.string.tag_analysis_page);
    }
}
