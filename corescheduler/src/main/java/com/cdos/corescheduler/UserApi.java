package com.cdos.corescheduler;

public class UserApi {

    private String userId;
    private int areaCode;
    private String commuteMode;

    protected UserApi() {}

    public UserApi(String userId, int areaCode, String commuteMode) {
        this.userId = userId;
        this.areaCode = areaCode;
        this.commuteMode = commuteMode;
    }

    public String getUserId() {
        return userId;
    }

    public int getAreaCode() {
        return areaCode;
    }

    public String getCommuteMode() {
        return commuteMode;
    }
}