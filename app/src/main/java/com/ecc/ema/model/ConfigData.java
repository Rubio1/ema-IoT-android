package com.ecc.ema.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigData {

    private String apiEndpoint;
    private String authorizeUri;
    private Info info;
    private String appKey;
    private String oauthCallback;
    private String appTokenUrl;
    private String appE911idUrl;
    private List<String> virtualNumbersPool = new ArrayList<String>();
    private String ewebrtcDomain;

    /**
     * @return The apiEndpoint
     */
    public String getApiEndpoint() {
        return apiEndpoint;
    }

    /**
     * @param apiEndpoint The api_endpoint
     */
    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    /**
     * @return The authorizeUri
     */
    public String getAuthorizeUri() {
        return authorizeUri;
    }

    /**
     * @param authorizeUri The authorize_uri
     */
    public void setAuthorizeUri(String authorizeUri) {
        this.authorizeUri = authorizeUri;
    }

    /**
     * @return The info
     */
    public Info getInfo() {
        return info;
    }

    /**
     * @param info The info
     */
    public void setInfo(Info info) {
        this.info = info;
    }

    /**
     * @return The appKey
     */
    public String getAppKey() {
        return appKey;
    }

    /**
     * @param appKey The app_key
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    /**
     * @return The oauthCallback
     */
    public String getOauthCallback() {
        return oauthCallback;
    }

    /**
     * @param oauthCallback The oauth_callback
     */
    public void setOauthCallback(String oauthCallback) {
        this.oauthCallback = oauthCallback;
    }

    /**
     * @return The appTokenUrl
     */
    public String getAppTokenUrl() {
        return appTokenUrl;
    }

    /**
     * @param appTokenUrl The app_token_url
     */
    public void setAppTokenUrl(String appTokenUrl) {
        this.appTokenUrl = appTokenUrl;
    }

    /**
     * @return The appE911idUrl
     */
    public String getAppE911idUrl() {
        return appE911idUrl;
    }

    /**
     * @param appE911idUrl The app_e911id_url
     */
    public void setAppE911idUrl(String appE911idUrl) {
        this.appE911idUrl = appE911idUrl;
    }

    /**
     * @return The virtualNumbersPool
     */
    public List<String> getVirtualNumbersPool() {
        return virtualNumbersPool;
    }

    /**
     * @param virtualNumbersPool The virtual_numbers_pool
     */
    public void setVirtualNumbersPool(List<String> virtualNumbersPool) {
        this.virtualNumbersPool = virtualNumbersPool;
    }

    /**
     * @return The ewebrtcDomain
     */
    public String getEwebrtcDomain() {
        return ewebrtcDomain;
    }

    /**
     * @param ewebrtcDomain The ewebrtc_domain
     */
    public void setEwebrtcDomain(String ewebrtcDomain) {
        this.ewebrtcDomain = ewebrtcDomain;
    }

}