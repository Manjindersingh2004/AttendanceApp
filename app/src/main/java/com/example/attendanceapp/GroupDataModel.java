package com.example.attendanceapp;

public class GroupDataModel {

    String TOTAL_LEC;

    public GroupDataModel() {
    }

    public GroupDataModel(String TOTAL_LEC) {
        this.TOTAL_LEC = TOTAL_LEC;
    }


    public String getTOTAL_LEC() {
        return TOTAL_LEC;
    }

    public void setTOTAL_LEC(String TOTAL_LEC) {
        this.TOTAL_LEC = TOTAL_LEC;
    }
}
