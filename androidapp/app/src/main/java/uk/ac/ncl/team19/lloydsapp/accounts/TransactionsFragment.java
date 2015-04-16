package uk.ac.ncl.team19.lloydsapp.accounts;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.ac.ncl.team19.lloydsapp.R;
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
 * @author Yessengerey Bolatov (XML), Raffaello Perrotta
 * @author Dale Whinham - Spinner/List adapters, backend integration
 */
public class TransactionsFragment extends Fragment {

    BankAccount currentAccount;
    List<Transaction> transactions;

    Spinner yearSpinner;
    ExpandableListView elv;
    ProgressBar progressBar;
    TextView noTransactions;

    TransactionYearSpinnerAdapter spinnerAdapter;
    TransactionListAdapter listAdapter;

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

                    // Show spinner/elv, hide progress
                    yearSpinner.setVisibility(View.VISIBLE);
                    elv.setVisibility(View.VISIBLE);

                    // Setup adapters
                    spinnerAdapter = new TransactionYearSpinnerAdapter(transactions);
                    listAdapter = new TransactionListAdapter(transactions, spinnerAdapter.getItem(0));

                    yearSpinner.setAdapter(spinnerAdapter);
                    elv.setAdapter(listAdapter);

                    yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            listAdapter = new TransactionListAdapter(transactions, (String) yearSpinner.getSelectedItem());
                            elv.setAdapter(listAdapter);
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

    private class TransactionYearSpinnerAdapter extends ArrayAdapter<String> {
        public TransactionYearSpinnerAdapter(List<Transaction> transactions) {
            super(getActivity(), android.R.layout.simple_spinner_item);

            SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.ROOT);

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

    private class TransactionListAdapter extends BaseExpandableListAdapter {
        // LinkedHashMap retains order
        private final LinkedHashMap<String, List<Transaction>> transactionMap = new LinkedHashMap<>();

        public TransactionListAdapter(List<Transaction> transactions, String forYear) {
            SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy", Locale.ROOT);
            SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM", Locale.ROOT);

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
            TextView transactionInfo = (TextView) convertView.findViewById(R.id.transactionInfo);
            TextView transactionAmount = (TextView) convertView.findViewById(R.id.transactionAmount);
            ImageView inOutIndicator = (ImageView) convertView.findViewById(R.id.inOutIndicator);

            String dateString = new SimpleDateFormat("E d, kk:mm", Locale.ROOT).format(t.getDate());
            String transactionInfoString = String.format("%s: %s", dateString, t.getReference());
            String transactionAmountString = (isWithdrawal ? "-" : "") + CurrencyMangler.integerToSterlingString(t.getAmount());

            // Set the transaction string
            transactionInfo.setText(transactionInfoString);
            transactionAmount.setText(transactionAmountString);

            // Withdrawals get the red outgoing arrow, deposits get the green
            inOutIndicator.setImageDrawable(getResources().getDrawable(isWithdrawal ? R.drawable.out : R.drawable.in));

            // Must be selected in order for marquee to work
            transactionInfo.setSelected(true);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    @Override
    public String toString(){
        return getString(R.string.transactions_page);
    }

}