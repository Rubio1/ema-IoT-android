package com.ecc.ema;

import android.util.Log;

import com.ecc.ema.model.ConfigData;
import com.att.webrtcsdk.apicall.Constants;
import com.att.webrtcsdk.model.OAuthToken;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.mime.TypedString;

import static com.att.webrtcsdk.apicall.SdkCallbacks.ErrorCallback;
import static com.att.webrtcsdk.apicall.SdkCallbacks.SuccessCallback;


/**
 * Class to make POST/PUT/GET request to the API gateway.
 *
 * @author ag155w
 */
public class ApiRequest {

    private final static String TAG = ApiRequest.class.getSimpleName();
    private static final String BF_URL = "https://api.att.com/RTC/v1/";

    private static ApiRequest singleton = null;

    private final RestClient rc;
    private final RestClient dhsRc;

    public ApiRequest() {
        rc = new RestClient(BF_URL);
        dhsRc = new RestClient(Constants.getBaseURL());
    }

    public static ApiRequest getInstance() {
        if (singleton == null) {
            singleton = new ApiRequest();
        }
        return singleton;
    }

    private String getHeaderValue(final String name, final Response response) {
        for (final Header header : response.getHeaders()) {
            if (header.getName() != null && header.getName().equalsIgnoreCase(name)) {
                return header.getValue();
            }
        }
        return null;
    }

    public void getConfig(final SuccessCallback successCallback,
                          final ErrorCallback errorCallback) {
        dhsRc.getApiService().getAccountIDConfig(new Callback<ConfigData>() {
            @Override
            public void success(ConfigData configData, Response response) {
                successCallback.onSuccess(configData);
            }

            @Override
            public void failure(RetrofitError error) {
                errorCallback.onError(error.toString());
            }
        });
    }

    public void getOAuthToken(final SuccessCallback successCallback,
                              final ErrorCallback errorCallback) {
        try {
            final JSONObject obj = new JSONObject()
                    .put("app_scope", "ACCOUNT_ID")
                    .put("auth_code", "");
            dhsRc.getApiService().getAccessToken(new TypedString(obj.toString()), new Callback<OAuthToken>() {
                @Override
                public void success(OAuthToken token, Response response) {
                    successCallback.onSuccess(token);
                }

                @Override
                public void failure(RetrofitError error) {
                    errorCallback.onError(error.toString());
                }
            });
        } catch (JSONException e) {
            Log.wtf(TAG, e.getMessage(), e);
        }
    }

}
