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
import uk.ac.ncl.team19.lloydsapp.api.datatypes.Transaction;
import uk.ac.ncl.team19.lloydsapp.api.response.APIResponse;
import uk.ac.ncl.team19.lloydsapp.api.utility.ErrorHandler;
import uk.ac.ncl.team19.lloydsapp.dialogs.ProgressDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;
import uk.ac.ncl.team19.lloydsapp.utils.general.CurrencyMangler;
import uk.ac.ncl.team19.lloydsapp.utils.general.FragmentChecker;
import uk.ac.ncl.team19.lloydsapp.utils.general.GraphicsUtils;

/**
 * @author Ivy Tong
 * XML design and structure.
 * @author Raffaello Perrotta
 * Passing information between fragments and setting up all fragment transactions. Fragment instance error handling.
 * @author Dale Whinham
 * All Backend integration and respective error/success handling from responses.
 *
 * This class provides the user a fragment where they may confirm whether they want to submit their payment.
 * An option to cancel and fully decouple the payment from being executed is also provided, should the user
 * want to cancel their payment at this stage or review it.
 *
 */
public class PaymentConfirmFragment extends Fragment{

    private Bundle args;
    private BankAccount fromAccount;

    private Button confirmPaymentButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View paymentConfirmView = inflater.inflate(R.layout.payment_confirm, container, false);

        TextView fromAccType = (TextView) paymentConfirmView.findViewById(R.id.fromAccType);
        TextView fromAccNo = (TextView) paymentConfirmView.findViewById(R.id.fromAccNo);
        TextView fromSortCode = (TextView) paymentConfirmView.findViewById(R.id.fromSortCode);
        TextView toAccNo = (TextView) paymentConfirmView.findViewById(R.id.toAccNo);
        TextView toSortCode = (TextView) paymentConfirmView.findViewById(R.id.toSortCode);
        TextView reference = (TextView) paymentConfirmView.findViewById(R.id.reference);
        TextView amount = (TextView) paymentConfirmView.findViewById(R.id.amount);
        TextView tag = (TextView) paymentConfirmView.findViewById(R.id.tag);

        // Set the values from the bundle, i.e. what the user entered in the previous fragment.
        args = getArguments();
        fromAccount = (BankAccount) args.getSerializable(Constants.BUNDLE_KEY_FROM_ACC);
        fromAccType.setText(fromAccount.getAccountTypeString(getActivity()));
        fromAccNo.setText(fromAccount.getAccountNumber());
        fromSortCode.setText(fromAccount.getFormattedSortCode());
        toAccNo.setText(args.getString(Constants.BUNDLE_KEY_TO_ACC_NO));
        toSortCode.setText(args.getString(Constants.BUNDLE_KEY_TO_SORT_CODE));
        reference.setText(args.getString(Constants.BUNDLE_KEY_REF));
        amount.setText(CurrencyMangler.integerToSterlingString(args.getLong(Constants.BUNDLE_KEY_AMOUNT)));
        tag.setText(args.getString(Constants.BUNDLE_KEY_TAG_STRING));

        // Get the fragment manager
        final FragmentManager fragmentManager = getFragmentManager();

        // On Clicking the confirm button
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
                        args.getString(Constants.BUNDLE_KEY_REF),
                        (Transaction.Tag) args.getSerializable(Constants.BUNDLE_KEY_TAG),
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
                                    PaymentSuccessfulFragment paymentSuccess = new PaymentSuccessfulFragment();
                                    // Pass the bundle arguments to the next fragment.
                                    paymentSuccess.setArguments(args);
                                    fragmentManager.beginTransaction().replace(R.id.container, paymentSuccess).commit();
                                } else {
                                    ErrorHandler.fail(fragmentManager, apiResponse.getErrorMessage());
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                // Hide the effects on the button
                                GraphicsUtils.buttonClickEffectHide(confirmPaymentButton);

                                // Dismiss progress dialog
                                ProgressDialog.removeLoading(getActivity());

                                // Handle error
                                ErrorHandler.fail(getActivity(), fragmentManager, error);
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
