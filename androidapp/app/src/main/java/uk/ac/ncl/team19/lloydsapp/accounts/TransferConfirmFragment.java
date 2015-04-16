package uk.ac.ncl.team19.lloydsapp.accounts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.APIConnector;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.BankAccount;
import uk.ac.ncl.team19.lloydsapp.api.response.APIResponse;
import uk.ac.ncl.team19.lloydsapp.api.utility.ErrorHandler;
import uk.ac.ncl.team19.lloydsapp.dialogs.ProgressDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.CurrencyMangler;
import uk.ac.ncl.team19.lloydsapp.utils.general.FragmentChecker;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Raffaello Perrotta
 * Fragment transaction and passing information between fragments.
 * @author Yessengerey Bolatov
 * Full XML design.
 * @author Dale Whinham
 * Full backend integration, including requests to the server and error handling. Refactor of information passing.
 *
 *  This class provides a fragment to confirm any transfers that the user wishes to make between their
 *  bank accounts. Users may cancel the transfer at any time within this fragment, and the request to
 *  perform the transfer is not sent to the backend. If the user confirms the transfer, then the information
 *  is sent to the backend.
 */
public class TransferConfirmFragment extends Fragment {

    private Bundle args;
    private BankAccount fromAccount;
    private BankAccount toAccount;

    private Button confirmTransferButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get all views that will hold data.
        View transferConfirmView = inflater.inflate(R.layout.transfer_confirm, container, false);

        TextView fromAccountName = (TextView) transferConfirmView.findViewById(R.id.fromAccount);
        TextView fromAccountNo = (TextView) transferConfirmView.findViewById(R.id.fromAccNo);
        TextView fromSortCode = (TextView) transferConfirmView.findViewById(R.id.fromSortCode);
        TextView toAccountName = (TextView) transferConfirmView.findViewById(R.id.toAccount);
        TextView toAccountNo = (TextView) transferConfirmView.findViewById(R.id.toAccNo);
        TextView toSortCode = (TextView) transferConfirmView.findViewById(R.id.toSortCode);
        TextView amount = (TextView) transferConfirmView.findViewById(R.id.amount);

        // Set values taken from bundle, i.e. what the user entered in the previous fragment.
        args = getArguments();
        fromAccount = (BankAccount) args.getSerializable(Constants.BUNDLE_KEY_FROM_ACC);
        toAccount = (BankAccount) args.getSerializable(Constants.BUNDLE_KEY_TO_ACC);

        fromAccountName.setText(fromAccount.toString());
        fromAccountNo.setText(fromAccount.getAccountNumber());
        fromSortCode.setText(fromAccount.getFormattedSortCode());
        toAccountName.setText(toAccount.toString());
        toAccountNo.setText(toAccount.getAccountNumber());
        toSortCode.setText(toAccount.getFormattedSortCode());
        amount.setText(CurrencyMangler.integerToSterlingString(args.getLong(Constants.BUNDLE_KEY_AMOUNT)));

        // Get the fragment manager
        final FragmentManager fragmentManager = getFragmentManager();

        // On Clicking the confirm button
        confirmTransferButton = (Button) transferConfirmView.findViewById(R.id.confirmTransfer);
        confirmTransferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);

                // Show progress dialog
                ProgressDialog.showLoading(TransferConfirmFragment.this);

                APIConnector ac = new APIConnector(getActivity());
                ac.transfer(
                        fromAccount.getId(),
                        toAccount.getAccountNumber(),
                        toAccount.getSortCode(),
                        args.getLong(Constants.BUNDLE_KEY_AMOUNT),
                        args.getLong(Constants.BUNDLE_KEY_TAG),
                        new Callback<APIResponse>() {
                            @Override
                            public void success(APIResponse apiResponse, Response response) {

                                // Fail silently if not on the same class.
                                if(!FragmentChecker.checkFragment(fragmentManager, TransferConfirmFragment.this))
                                    return;

                                GraphicsUtils.buttonClickEffectHide(confirmTransferButton);

                                // Hide Progress dialog
                                ProgressDialog.removeLoading(TransferConfirmFragment.this);

                                if (apiResponse.getStatus() == APIResponse.Status.SUCCESS) {
                                    TransferSuccessfulFragment transferSuccess = new TransferSuccessfulFragment();
                                    // Pass the bundle arguments to the next fragment.
                                    transferSuccess.setArguments(args);
                                    fragmentManager.beginTransaction().replace(R.id.container, transferSuccess).commit();
                                } else {
                                    ErrorHandler.fail(fragmentManager, apiResponse.getErrorMessage());
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                // Hide the effects on the button
                                GraphicsUtils.buttonClickEffectHide(confirmTransferButton);

                                // Dismiss progress dialog
                                ProgressDialog.removeLoading(getActivity());

                                // Handle error
                                ErrorHandler.fail(getActivity(), fragmentManager, error);
                            }
                        }
                );
            }
        });

        transferConfirmView.findViewById(R.id.cancelTransferInConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                fragmentManager.popBackStack();
            }
        });

        return transferConfirmView;

    }

    @Override
    public String toString(){
        return getString(R.string.confirm_transfer_page);
    }

}
