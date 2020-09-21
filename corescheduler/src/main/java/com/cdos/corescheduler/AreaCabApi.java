package com.cdos.corescheduler;

public class AreaCabApi {

    private int areaCode;
    private int cabCapacity;

    protected AreaCabApi() {}

    public AreaCabApi(int areaCode, int cabCapacity) {
        this.areaCode = areaCode;
        this.cabCapacity = cabCapacity;
    }
    
    public int getAreaCode() {
        return areaCode;
    }

    public int getCabCapacity() {
        return cabCapacity;
    }
}