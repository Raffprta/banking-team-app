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
 * @author Ivy Tong (XML), Raffaello Perrotta
 * @author Dale Whinham - backend integration
 */
public class PaymentConfirmFragment extends Fragment{

    private Bundle args;
    private BankAccount fromAccount;

    private Button confirmPaymentButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View paymentConfirmView = inflater.inflate(R.layout.payment_confirm, container, false);

        TextView fromAccountName = (TextView) paymentConfirmView.findViewById(R.id.fromAccount);
        TextView fromAccountNo = (TextView) paymentConfirmView.findViewById(R.id.fromAccNo);
        TextView fromSortCode = (TextView) paymentConfirmView.findViewById(R.id.fromSortCode);
        TextView toReference = (TextView) paymentConfirmView.findViewById(R.id.toRef);
        TextView toAccountNo = (TextView) paymentConfirmView.findViewById(R.id.toAccNo);
        TextView toSortCode = (TextView) paymentConfirmView.findViewById(R.id.toSortCode);
        TextView amount = (TextView) paymentConfirmView.findViewById(R.id.amount);

        // Set the values from the bundle, i.e. what the user entered in the previous fragment.
        args = getArguments();
        fromAccount = (BankAccount) args.getSerializable(Constants.BUNDLE_KEY_FROM_ACC);
        fromAccountName.setText(fromAccount.toString());
        fromAccountNo.setText(fromAccount.getAccountNumber());
        fromSortCode.setText(fromAccount.getFormattedSortCode());

        toReference.setText(args.getString(Constants.BUNDLE_KEY_TO_REF));
        toAccountNo.setText(args.getString(Constants.BUNDLE_KEY_TO_ACC_NO));
        toSortCode.setText(args.getString(Constants.BUNDLE_KEY_TO_SORT_CODE));
        amount.setText(CurrencyMangler.integerToSterlingString(args.getLong(Constants.BUNDLE_KEY_AMOUNT)));

        // Get the fragment manager
        final FragmentManager fragmentManager = getFragmentManager();

        // On Clicking the Confirm button
        confirmPaymentButton = (Button) paymentConfirmView.findViewById(R.id.confirmPayment);
        confirmPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);

                // Show progress dialog
                ProgressDialog.showLoading(PaymentConfirmFragment.this);

                APIConnector ac = new APIConnector(getActivity());
                ac.transfer(
                        fromAccount.getId(),
                        args.getString(Constants.BUNDLE_KEY_TO_ACC_NO),
                        args.getString(Constants.BUNDLE_KEY_TO_SORT_CODE),
                        args.getLong(Constants.BUNDLE_KEY_AMOUNT),
                        args.getString(Constants.BUNDLE_KEY_TO_REF),
                        new Callback<APIResponse>() {
                            @Override
                            public void success(APIResponse apiResponse, Response response) {

                                // Fail silently if not on the same class.
                                if(!FragmentChecker.checkFragment(fragmentManager, PaymentConfirmFragment.this))
                                    return;

                                GraphicsUtils.buttonClickEffectHide(confirmPaymentButton);

                                // Hide Progress dialog
                                ProgressDialog.removeLoading(PaymentConfirmFragment.this);

                                if (apiResponse.getStatus() == APIResponse.Status.SUCCESS) {
                                    PaymentSuccessfulFragment paymentsSuccess = new PaymentSuccessfulFragment();
                                    // Pass the bundle arguments to the next fragment.
                                    paymentsSuccess.setArguments(args);
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.container, paymentsSuccess).commit();
                                } else {
                                    ErrorHandler.fail(getFragmentManager(), apiResponse.getErrorMessage());
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                // Hide the effects on the button
                                GraphicsUtils.buttonClickEffectHide(confirmPaymentButton);

                                // Dismiss progress dialog
                                ProgressDialog.removeLoading(getActivity());

                                // Handle error
                                ErrorHandler.fail(getActivity(), getFragmentManager(), error);
                            }
                        }
                );
            }
        });

        // On Clicking the cancel button, return to the previous page.
        paymentConfirmView.findViewById(R.id.cancelPayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphicsUtils.buttonClickEffectShow(v);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });
        return paymentConfirmView;

    }

    @Override
    public String toString(){
        return getString(R.string.payment_confirm_page);
    }

}
