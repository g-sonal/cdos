package com.cdos.corescheduler.projection;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AreaCab {
    private final int DaysPerWeek = 5;

    @Id
    private int areaCode;
    private int cabCapacity;
    private String freeCabSeats;

    protected AreaCab() {}

    public AreaCab(int areaCode, int cabCapacity) {
        this.areaCode = areaCode;
        this.cabCapacity = cabCapacity;

        StringBuilder s = new StringBuilder(DaysPerWeek);
        for (int i = 0; i < DaysPerWeek; ++i) {
            s.append(cabCapacity);
        }
        this.freeCabSeats = s.toString();
    }
    
    public int getAreaCode() {
        return areaCode;
    }

    public int getCabCapacity() {
        return cabCapacity;
    }

    public String getFreeCabSeats() {
        return freeCabSeats;
    }
}