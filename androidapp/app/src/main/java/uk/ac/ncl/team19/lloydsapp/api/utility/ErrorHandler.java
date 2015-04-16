package uk.ac.ncl.team19.lloydsapp.api.utility;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import retrofit.RetrofitError;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.dialogs.CustomDialog;
import uk.ac.ncl.team19.lloydsapp.utils.general.Constants;

/**
 * @author Dale Whinham
 *
 * A custom error handler to read error conditions in the network or in JSON deserialisation and
 * statically show an error dialog to the calling fragment or activity.
 */
public class ErrorHandler {
    public static void fail(FragmentManager f, String errorMessage) {
        showErrorDialog(f, errorMessage);
    }

    public static void fail(Context ctx, FragmentManager f, RetrofitError e) {
        String errorMessage;
        switch (e.getKind()) {
            case NETWORK:
                errorMessage = ctx.getString(R.string.error_network) + e.getMessage();
                break;
            case CONVERSION:
                errorMessage = ctx.getString(R.string.error_conversion) + e.getMessage();
                break;
            case HTTP:
                errorMessage = ctx.getString(R.string.error_http) + e.getMessage();
                break;
            case UNEXPECTED:
            default:
                errorMessage = ctx.getString(R.string.error_unexpected) + e.getMessage();
                break;
        }

        showErrorDialog(f, errorMessage);
    }

    private static void showErrorDialog(FragmentManager f, String errorMessage) {
        Bundle b = new Bundle();
        b.putString(Constants.BUNDLE_KEY_CUSTOM_DIALOG_MESSAGE, errorMessage);
        b.putBoolean(Constants.BUNDLE_KEY_CUSTOM_DIALOG_IS_ERROR, true);
        CustomDialog custom = new CustomDialog();
        custom.setArguments(b);
        custom.show(f);
    }
}
