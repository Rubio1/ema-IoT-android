package com.ecc.ema;

import com.ecc.ema.model.ConfigData;
import com.att.webrtcsdk.model.OAuthToken;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.mime.TypedString;

public interface ApiService {

    @GET("/config")
    void getAccountIDConfig(Callback<ConfigData> callback);

    @Headers("Content-Type:application/json")
    @POST("/tokens")
    void getAccessToken(@Body TypedString jsonString, Callback<OAuthToken> callback);

}