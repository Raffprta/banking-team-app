package uk.ac.ncl.team19.lloydsapp.accounts;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.adapters.TransactionYearSpinnerAdapter;
import uk.ac.ncl.team19.lloydsapp.api.APIConnector;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.Transaction;
import uk.ac.ncl.team19.lloydsapp.api.response.APIResponse;
import uk.ac.ncl.team19.lloydsapp.api.response.TransactionsResponse;
import uk.ac.ncl.team19.lloydsapp.api.utility.ErrorHandler;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.CurrencyMangler;
import uk.ac.ncl.team19.lloydsapp.utils.general.FragmentChecker;

/**
 * @author Yessengerey Bolatov
 * XML design and structure. Collaboration in setting up adapter classes. Icons.
 * @author Raffaello Perrotta
 * Minor set up of the class.
 * @author Dale Whinham
 * Major XML integration, plus marquee effects. Spinner/List adapters, full backend integration and respective viewing methods.
 *
 * This fragment allows the user to view the transaction history pertaining to one acount. The transactions
 * are automatically categorised by date, and the user may intuitively navigate the dates. Transactions are iconised depending
 * if they are outbound or inbound.
 */
public class TransactionsFragment extends Fragment {

    // The user's current account that they are viewing the history from.
    private BankAccount currentAccount;
    // Where the transaction history is loaded into.
    private List<Transaction> transactions;

    // Various views used by this class
    private Spinner yearSpinner;
    private ExpandableListView elv;
    private ProgressBar progressBar;
    private TextView noTransactions;

    // Adapters
    private TransactionYearSpinnerAdapter spinnerAdapter;
    private TransactionListAdapter listAdapter;

    // Date formatters
    private static final SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy", Locale.ROOT);
    private static final SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM", Locale.ROOT);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View transactionsView = inflater.inflate(R.layout.transaction_history_page, container, false);

        // Get references to views
        yearSpinner = (Spinner) transactionsView.findViewById(R.id.yearSpinner);
        elv = (ExpandableListView) transactionsView.findViewById(R.id.expandableListView);
        progressBar = (ProgressBar) transactionsView.findViewById(R.id.progressBar);
        noTransactions = (TextView) transactionsView.findViewById(R.id.noTransactions);

        // Hide spinner/elv, show progress
        yearSpinner.setVisibility(View.GONE);
        elv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // Unbundle account
        currentAccount = (BankAccount) getArguments().getSerializable(Constants.BUNDLE_KEY_CURRENT_ACCOUNT);

        // Get the fragment manager
        final FragmentManager fragmentManager = getFragmentManager();

        // Setup tags button
        final Button tagsAnalysisButton = (Button) transactionsView.findViewById(R.id.tags);
        tagsAnalysisButton.setEnabled(false);
        tagsAnalysisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();

                // Pass withdrawals for the selected year to the tags analysis fragment
                String year = (String) yearSpinner.getSelectedItem();
                List<Transaction> transactionsForSelectedYear = getOutgoingTransactionsForYear(year);
                b.putString(Constants.BUNDLE_KEY_YEAR, year);
                b.putSerializable(Constants.BUNDLE_KEY_TRANSACTIONS_LIST, (Serializable) transactionsForSelectedYear);

                // Pass arguments from this fragment to the next
                TagsAnalysisFragment tagsAnalysisFragment = new TagsAnalysisFragment();
                tagsAnalysisFragment.setArguments(b);

                fragmentManager.beginTransaction().replace(R.id.container, tagsAnalysisFragment).addToBackStack(getString(R.string.accounts_dashboard_page)).commit();
            }
        });

        // Retrieve transactions
        APIConnector ac = new APIConnector(getActivity());
        ac.getTransactions(currentAccount.getId(), null, null, new Callback<TransactionsResponse>() {
            @Override
            public void success(TransactionsResponse transactionsResponse, Response response) {

                // Fail silently if not on the same class.
                if(!FragmentChecker.checkFragment(fragmentManager, TransactionsFragment.this))
                    return;

                // Hide progress
                progressBar.setVisibility(View.GONE);


                if (transactionsResponse.getStatus() == APIResponse.Status.SUCCESS) {
                    // Store transactions
                    transactions = transactionsResponse.getTransactions();

                    // Show notification if there are no transactions to show
                    if (transactions == null || transactions.size() == 0) {
                        noTransactions.setVisibility(View.VISIBLE);
                        return;
                    }

                    // Enable Tags button
                    tagsAnalysisButton.setEnabled(true);

                    // Show spinner/elv, hide progress
                    yearSpinner.setVisibility(View.VISIBLE);
                    elv.setVisibility(View.VISIBLE);

                    // Setup adapters
                    spinnerAdapter = new TransactionYearSpinnerAdapter(getActivity(), transactions);
                    listAdapter = new TransactionListAdapter(transactions, spinnerAdapter.getItem(0));

                    yearSpinner.setAdapter(spinnerAdapter);
                    elv.setAdapter(listAdapter);

                    yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            listAdapter = new TransactionListAdapter(transactions, (String) yearSpinner.getSelectedItem());
                            elv.setAdapter(listAdapter);

                            // Pop the first group open by default
                            elv.expandGroup(0);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                } else {
                    ErrorHandler.fail(getFragmentManager(), transactionsResponse.getErrorMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                // Hide progress
                progressBar.setVisibility(View.GONE);

                // Handle error
                ErrorHandler.fail(getActivity(), getFragmentManager(), error);
            }
        });

        return transactionsView;
    }

    private class TransactionListAdapter extends BaseExpandableListAdapter {
        // LinkedHashMap retains order
        private final LinkedHashMap<String, List<Transaction>> transactionMap = new LinkedHashMap<>();

        public TransactionListAdapter(List<Transaction> transactions, String forYear) {
            // Filter out transactions for this year
            for (Transaction t: transactions) {
                String yearString = yearFormatter.format(t.getDate());
                if (yearString.equals(forYear)) {
                    // Add transaction to map
                    String monthString = monthFormatter.format(t.getDate());
                    if (transactionMap.containsKey(monthString)) {
                        transactionMap.get(monthString).add(t);
                    } else {
                        List<Transaction> tl = new ArrayList<>();
                        tl.add(t);
                        transactionMap.put(monthString, tl);
                    }
                }
            }

        }

        @Override
        public int getGroupCount() {
            return transactionMap.keySet().size();
        }

        @Override
        public int getChildrenCount(int i) {
            String key = (String) transactionMap.keySet().toArray()[i];
            return transactionMap.get(key).size();
        }

        @Override
        public Object getGroup(int i) {
            return transactionMap.keySet().toArray()[i];
        }

        @Override
        public Object getChild(int i, int j) {
            String key = (String) transactionMap.keySet().toArray()[i];
            return transactionMap.get(key).get(j);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int j) {
            return j;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expandlist_group_item, null);
            }

            TextView lblListHeader = (TextView) convertView.findViewById(R.id.groupName);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Transaction t = (Transaction) getChild(groupPosition, childPosition);

            // If the from account ID matches the ID of the account we're inspecting, it's a withdrawal
            boolean isWithdrawal = t.getFromAccountId() == currentAccount.getId();

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.expandlist_child_item, null);
            }

            // References to subviews
            TextView date = (TextView) convertView.findViewById(R.id.date);
            TextView reference = (TextView) convertView.findViewById(R.id.reference);
            TextView amount = (TextView) convertView.findViewById(R.id.amount);
            ImageButton inOutIndicator = (ImageButton) convertView.findViewById(R.id.inOutIndicator);

            String dateString = new SimpleDateFormat("E d, kk:mm", Locale.ROOT).format(t.getDate());
            String transactionAmountString = (isWithdrawal ? "-" : "") + CurrencyMangler.integerToSterlingString(t.getAmount());

            // Set the transaction strings
            date.setText(dateString);
            reference.setText(t.getReference());
            amount.setText(transactionAmountString);

            // Must be selected in order for marquee to work
            reference.setSelected(true);

            // Tagged withdrawals get a coloured icon
            // Untagged withdrawals get the red outgoing arrow, deposits get the green incoming arrow
            Transaction.Tag tag = t.getTag();
            if (isWithdrawal && tag != null && tag != Transaction.Tag.UNTAGGED) {
                int drawableId = Transaction.Tag.getDrawableIdForTag(t.getTag());
                int colorId = Transaction.Tag.getColorIdForTag(t.getTag());
                inOutIndicator.setImageDrawable(getResources().getDrawable(drawableId));
                inOutIndicator.setBackgroundColor(getResources().getColor(colorId));
            } else {
                inOutIndicator.setImageDrawable(getResources().getDrawable(isWithdrawal ? R.drawable.out : R.drawable.in));
                inOutIndicator.setBackgroundColor(Color.TRANSPARENT);
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    // Filters a list of transactions by a given year
    private List<Transaction> getOutgoingTransactionsForYear(String year) {
        List<Transaction> filteredTransactions = new ArrayList<>();
        for (Transaction t: transactions) {
            String transactionYear = yearFormatter.format(t.getDate());
            boolean isWithdrawal = t.getFromAccountId() == currentAccount.getId();
            if (transactionYear.equals(year) && isWithdrawal) {
                filteredTransactions.add(t);
            }
        }

        return filteredTransactions;
    }

    @Override
    public String toString(){
        return getString(R.string.transactions_page);
    }

}