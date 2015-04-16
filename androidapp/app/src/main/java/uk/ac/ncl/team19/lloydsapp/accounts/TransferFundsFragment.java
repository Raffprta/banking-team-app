package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.adapters.TagSpinnerAdapter;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.Transaction;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.CurrencyMangler;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Yessengerey Bolatov
 * All XML Designs.
 * @author Raffaello Perrotta
 * Client side validation. Information passing between fragments and fragment transaction maangement.
 * @author Dale Whinham
 * Backend integration. Client-side logic for transferring data. Minor XML modifications.
 *
 * This class provides a fragment with an intuitive design for transferring funds between a users'
 * set up bank accounts. The funds will not be able to be transferred if the user does not have at least
 * two accounts set up. This class will proceed to a confirmation page on the user making a transfer.
 */

public class TransferFundsFragment extends Fragment {

    private Bundle args;
    private List<BankAccount> accounts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View transferFundsView = inflater.inflate(R.layout.transfer_funds_page, container, false);
        final EditText amountToPay = (EditText) transferFundsView.findViewById(R.id.amountToTransfer);
        final Spinner fromAccount = (Spinner) transferFundsView.findViewById(R.id.accountTransferFrom);
        final Spinner toAccount = (Spinner) transferFundsView.findViewById(R.id.accountTransferTo);
        final Spinner tags = (Spinner) transferFundsView.findViewById(R.id.tag);

        // Unbundle accounts
        args = getArguments();
        accounts = (List<BankAccount>) args.getSerializable(Constants.BUNDLE_KEY_ACCOUNT_LIST);

        // Only proceed if there are 2 or more accounts
        if (accounts.size() >= 2) {
            // Setup spinners
            final ArrayAdapter<BankAccount> accountSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, accounts);
            fromAccount.setAdapter(accountSpinnerAdapter);
            toAccount.setAdapter(accountSpinnerAdapter);
            tags.setAdapter(new TagSpinnerAdapter(getActivity()));

            // Force mutual exclusion for account spinners (prevents user trying to transferring to same account)
            fromAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int toSpinnerPosition = toAccount.getSelectedItemPosition();
                    if (position == toSpinnerPosition) {
                        // Bump the other spinner to another selection
                        toAccount.setSelection(++toSpinnerPosition % accountSpinnerAdapter.getCount());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            toAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int fromSpinnerPosition = fromAccount.getSelectedItemPosition();
                    if (position == fromSpinnerPosition) {
                        // Bump the other spinner to another selection
                        fromAccount.setSelection(++fromSpinnerPosition % accountSpinnerAdapter.getCount());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            // Transfer button action
            transferFundsView.findViewById(R.id.continueTransfer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GraphicsUtils.buttonClickEffectShow(v);

                    // Check if an amount was entered
                    if (amountToPay.getText().toString() == null || amountToPay.getText().toString().length() <= 0) {
                        amountToPay.setError(getString(R.string.err_payment_empty));
                        GraphicsUtils.buttonClickEffectHide(v);
                        return;
                    }

                    // Check if GBP decimal or otherwise is entered
                    if (!amountToPay.getText().toString().matches("[0-9]+(\\.[0-9][0-9]?)?")) {
                        amountToPay.setError(getString(R.string.err_payment_wrong_form));
                        GraphicsUtils.buttonClickEffectHide(v);
                        return;
                    }

                    // If all validation conditions passed, then add information to bundle and pass to the next fragment
                    args.putSerializable(Constants.BUNDLE_KEY_FROM_ACC, (BankAccount) fromAccount.getSelectedItem());
                    args.putSerializable(Constants.BUNDLE_KEY_TO_ACC, (BankAccount) toAccount.getSelectedItem());
                    args.putLong(Constants.BUNDLE_KEY_AMOUNT, CurrencyMangler.sterlingStringToInteger(amountToPay.getText().toString()));
                    args.putSerializable(Constants.BUNDLE_KEY_TAG, Transaction.Tag.getTag(tags.getSelectedItemId()));

                    TransferConfirmFragment transferConfirmFragment = new TransferConfirmFragment();
                    transferConfirmFragment.setArguments(args);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container, transferConfirmFragment).commit();
                }
            });

            transferFundsView.findViewById(R.id.cancelTransfer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GraphicsUtils.buttonClickEffectShow(v);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                }
            });
        } else {
            // Display error and go back to previous fragment
            FragmentManager f = getFragmentManager();
            Bundle b = new Bundle();
            b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, getString(R.string.err_no_other_accounts));
            b.putBoolean(Constants.BUNDLE_KEY_CUSTOM_DIALOG_IS_ERROR, true);
            CustomDialog custom = new CustomDialog();
            custom.setArguments(b);
            custom.show(f);
            f.popBackStack();
        }

        return transferFundsView;
    }

    @Override
    public String toString(){
        return getString(R.string.transfer_funds_page);
    }

}
