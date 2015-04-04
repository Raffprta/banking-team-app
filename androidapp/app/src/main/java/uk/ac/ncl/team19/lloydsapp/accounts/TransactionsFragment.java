package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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

        // Retrieve transactions
        APIConnector ac = new APIConnector(getActivity());
        ac.getTransactions(currentAccount.getId(), null, null, new Callback<TransactionsResponse>() {
            @Override
            public void success(TransactionsResponse transactionsResponse, Response response) {
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

    public class TransactionYearSpinnerAdapter extends ArrayAdapter<String> {
        public TransactionYearSpinnerAdapter(List<Transaction> transactions) {
            super(getActivity(), android.R.layout.simple_spinner_item);

            SimpleDateFormat df = new SimpleDateFormat("yyyy");

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

    public class TransactionListAdapter extends BaseExpandableListAdapter {
        // LinkedHashMap retains order
        private LinkedHashMap<String, List<Transaction>> transactionMap = new LinkedHashMap<>();

        public TransactionListAdapter(List<Transaction> transactions, String forYear) {
            SimpleDateFormat yearFormatter = new SimpleDateFormat("yyyy");
            SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMM");

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
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(TransactionsFragment.this.getActivity());
            textView.setText(getGroup(i).toString());

            return textView;
        }

        @Override
        public View getChildView(int i, int j, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(TransactionsFragment.this.getActivity());
            Transaction t = (Transaction) getChild(i, j);
            // If the from account ID matches the ID of the account we're inspecting, it's a withdrawal
            boolean isWithdrawal = t.getFromAccountId() == currentAccount.getId();

            // TODO: MAKE RED ARROW STUFF HAPPEN HERE BOOM

            String dateString = new SimpleDateFormat("E d @ k:m").format(t.getDate());

            String transactionString = String.format("%s - %s%s %s", dateString, isWithdrawal ? "-" : "", CurrencyMangler.integerToSterlingString(t.getAmount()), t.getReference());
            textView.setText(transactionString);

            return textView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

    }


    @Override
    public String toString(){
        return getString(R.string.transactions_page);
    }

}