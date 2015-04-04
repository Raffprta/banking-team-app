package uk.ac.ncl.team19.lloydsapp.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.datatypes.SecureChar;
import uk.ac.ncl.team19.lloydsapp.api.request.AuthRequest;
import uk.ac.ncl.team19.lloydsapp.api.request.TransferRequest;
import uk.ac.ncl.team19.lloydsapp.api.request.UpdateGcmIdRequest;
import uk.ac.ncl.team19.lloydsapp.api.request.UpdatePlayIdRequest;
import uk.ac.ncl.team19.lloydsapp.api.request.UpdateSettingsRequest;
import uk.ac.ncl.team19.lloydsapp.api.response.APIResponse;
import uk.ac.ncl.team19.lloydsapp.api.response.AccountDetailsResponse;
import uk.ac.ncl.team19.lloydsapp.api.response.AuthResponse;
import uk.ac.ncl.team19.lloydsapp.api.response.TransactionsResponse;

/**
 * Created by Dale Whinham on 17/03/15. Modified by Raffaello Perrotta.
 */
public class APIConnector {
    private interface BackendService {
        @POST("/authenticate")
        void authenticate(
                @Body AuthRequest authRequest,
                Callback<AuthResponse> cb
        );

        @POST("/updatesettings")
        void updateSettings(
                @Header("Device-Token") String deviceToken,
                @Body UpdateSettingsRequest updateSettingsRequest,
                Callback<APIResponse> cb
        );

        @POST("/updategcmid")
        void updateGcmId(
                @Header("Device-Token") String deviceToken,
                @Body UpdateGcmIdRequest updateGcmIdRequest,
                Callback<APIResponse> cb
        );

        @POST("/updateplayid")
        void updatePlayId(
                @Header("Device-Token") String deviceToken,
                @Body UpdatePlayIdRequest updatePlayIdRequest,
                Callback<APIResponse> cb
        );

        @GET("/accountdetails")
        void getAccountDetails(
                @Header("Device-Token") String deviceToken,
                Callback<AccountDetailsResponse> cb
        );

        @GET("/transactions/{id}")
        void getTransactions(
                @Header("Device-Token") String deviceToken,
                @Path("id") Long accId,
                @Query("periodFrom") Long periodFrom,
                @Query("periodTo") Long periodTo,
                Callback<TransactionsResponse> cb
        );

        @POST("/transfer")
        void transferMoney(
                @Header("Device-Token") String deviceToken,
                @Body TransferRequest transferMoneyRequest,
                Callback<APIResponse> cb
        );
    }

    private final String deviceToken;
    private RestAdapter restAdapter;
    private BackendService service;

    public APIConnector(Context ctx) {
        final String baseURL = ctx.getString(R.string.api_base_url);
        final String apiKey = ctx.getString(R.string.api_key);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        deviceToken = sp.getString(ctx.getString(R.string.sp_device_token), null);

        // This request interceptor adds the API key to every request
        RequestInterceptor interceptor = new RequestInterceptor()
        {
            @Override
            public void intercept(RequestFacade request) {
                request.addHeader("API-Key", apiKey);
            }
        };

        restAdapter = new RestAdapter.Builder()
                .setEndpoint(baseURL)
                .setRequestInterceptor(interceptor)
                .build();

        service = restAdapter.create(BackendService.class);
    }

    public void authenticate(String username, String password, List<SecureChar> secureChars, Callback<AuthResponse> callback) {
        AuthRequest authRequest = new AuthRequest(username, password, secureChars);
        service.authenticate(authRequest, callback);
    }

    public void updateSettings(boolean emailNotifications, boolean pushNotifications, Callback<APIResponse> callback) {
        UpdateSettingsRequest updateSettingsRequest = new UpdateSettingsRequest(emailNotifications, pushNotifications);
        service.updateSettings(deviceToken, updateSettingsRequest, callback);
    }

    public void updateGcmId(String gcmId, Callback<APIResponse> callback) {
        UpdateGcmIdRequest updateGcmIdRequest = new UpdateGcmIdRequest(gcmId);
        service.updateGcmId(deviceToken, updateGcmIdRequest, callback);
    }

    public void updatePlayId(String playId, Callback<APIResponse> callback) {
        UpdatePlayIdRequest updatePlayIdRequest = new UpdatePlayIdRequest(playId);
        service.updatePlayId(deviceToken, updatePlayIdRequest, callback);
    }

    public void getAccountDetails(Callback<AccountDetailsResponse> callback) {
        service.getAccountDetails(deviceToken, callback);
    }

    public void getTransactions(long accountId, Date periodFrom, Date periodTo, Callback<TransactionsResponse> callback) {
        service.getTransactions(deviceToken, accountId, periodFrom != null ? periodFrom.getTime() : null, periodTo != null ? periodTo.getTime() : null, callback);
    }

    public void transfer(long fromAccId, String toAccNo, String toSortCode, int amount, String reference, Callback<APIResponse> callback) {
        TransferRequest transferMoneyRequest = new TransferRequest(fromAccId, toAccNo, toSortCode, amount, reference);
        service.transferMoney(deviceToken, transferMoneyRequest, callback);
    }
}