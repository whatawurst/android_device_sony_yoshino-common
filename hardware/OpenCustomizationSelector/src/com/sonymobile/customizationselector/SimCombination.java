package com.sonymobile.customizationselector;

public final class SimCombination {

    private String mGid1 = null;
    private String mGid2 = null;
    private String mImsi = null;
    private String mMCC = null;
    private String mMNC = null;
    private String mSP = null;
    private String mSimConfigId = null;

    public SimCombination() {
    }

    public String getGid1() {
        return this.mGid1;
    }

    public String getGid2() {
        return this.mGid2;
    }

    public String getIMSI() {
        return this.mImsi;
    }

    public String getMCC() {
        return this.mMCC;
    }

    public String getMNC() {
        return this.mMNC;
    }

    public String getServiceProvider() {
        return this.mSP;
    }

    public String getSimConfigId() {
        return this.mSimConfigId;
    }

    public void setGid1(String str) {
        this.mGid1 = str;
    }

    public void setGid2(String str) {
        this.mGid2 = str;
    }

    public void setIMSI(String str) {
        this.mImsi = str;
    }

    public void setMCC(String str) {
        this.mMCC = str;
    }

    public void setMNC(String str) {
        this.mMNC = str;
    }

    public void setServiceProvider(String str) {
        this.mSP = str;
    }

    public void setSimConfigId(String str) {
        this.mSimConfigId = str;
    }
}
