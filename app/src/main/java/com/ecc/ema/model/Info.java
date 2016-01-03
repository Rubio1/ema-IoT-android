package com.ecc.ema.model;

public class Info {

    private String dhsPlatform;
    private String tokenUri;
    private String e911idUri;
    private String ewebrtcUri;
    private ScopeMap scopeMap;
    private String apiEnv;
    private String dhsName;
    private String dhsVersion;

    /**
     * @return The dhsPlatform
     */
    public String getDhsPlatform() {
        return dhsPlatform;
    }

    /**
     * @param dhsPlatform The dhs_platform
     */
    public void setDhsPlatform(String dhsPlatform) {
        this.dhsPlatform = dhsPlatform;
    }

    /**
     * @return The tokenUri
     */
    public String getTokenUri() {
        return tokenUri;
    }

    /**
     * @param tokenUri The token_uri
     */
    public void setTokenUri(String tokenUri) {
        this.tokenUri = tokenUri;
    }

    /**
     * @return The e911idUri
     */
    public String getE911idUri() {
        return e911idUri;
    }

    /**
     * @param e911idUri The e911id_uri
     */
    public void setE911idUri(String e911idUri) {
        this.e911idUri = e911idUri;
    }

    /**
     * @return The ewebrtcUri
     */
    public String getEwebrtcUri() {
        return ewebrtcUri;
    }

    /**
     * @param ewebrtcUri The ewebrtc_uri
     */
    public void setEwebrtcUri(String ewebrtcUri) {
        this.ewebrtcUri = ewebrtcUri;
    }

    /**
     * @return The scopeMap
     */
    public ScopeMap getScopeMap() {
        return scopeMap;
    }

    /**
     * @param scopeMap The scope_map
     */
    public void setScopeMap(ScopeMap scopeMap) {
        this.scopeMap = scopeMap;
    }

    /**
     * @return The apiEnv
     */
    public String getApiEnv() {
        return apiEnv;
    }

    /**
     * @param apiEnv The api_env
     */
    public void setApiEnv(String apiEnv) {
        this.apiEnv = apiEnv;
    }

    /**
     * @return The dhsName
     */
    public String getDhsName() {
        return dhsName;
    }

    /**
     * @param dhsName The dhs_name
     */
    public void setDhsName(String dhsName) {
        this.dhsName = dhsName;
    }

    /**
     * @return The dhsVersion
     */
    public String getDhsVersion() {
        return dhsVersion;
    }

    /**
     * @param dhsVersion The dhs_version
     */
    public void setDhsVersion(String dhsVersion) {
        this.dhsVersion = dhsVersion;
    }
}