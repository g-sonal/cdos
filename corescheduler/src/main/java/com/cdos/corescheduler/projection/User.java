package com.cdos.corescheduler.projection;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    private String userId;
    private int areaCode;
    private String commuteMode;
    private String slots = "000000000000000000000000000000";

    protected User() {}

    public User(String userId, int areaCode, String commuteMode) {
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

    public String getSlots() {
        return slots;
    }
}