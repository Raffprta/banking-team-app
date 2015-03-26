package uk.ac.ncl.team19.lloydsapp.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;
import uk.ac.ncl.team19.lloydsapp.R;
import uk.ac.ncl.team19.lloydsapp.api.request.AuthRequest;
import uk.ac.ncl.team19.lloydsapp.api.request.SecureChar;
import uk.ac.ncl.team19.lloydsapp.api.request.UpdateGcmIdRequest;
import uk.ac.ncl.team19.lloydsapp.api.request.UpdateSettingsRequest;
import uk.ac.ncl.team19.lloydsapp.api.response.APIResponse;
import uk.ac.ncl.team19.lloydsapp.api.response.AuthResponse;

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
}