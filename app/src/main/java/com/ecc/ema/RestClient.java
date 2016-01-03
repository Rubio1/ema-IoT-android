package com.ecc.ema;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.squareup.okhttp.OkHttpClient;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class RestClient {
    public static final String LOG_TAG = RestClient.class.getSimpleName();
    private static long READ_TIMEOUT = 10000;
    private ApiService apiService;

    public RestClient(String baseURL) {

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .create();
        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);

        // .setLogLevel(RestAdapter.LogLevel.FULL) to get the library http logs
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(baseURL)
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new OkClient(okHttpClient))
                .build();

        apiService = restAdapter.create(ApiService.class);
    }

    protected ApiService getApiService() {
        return apiService;
    }
}