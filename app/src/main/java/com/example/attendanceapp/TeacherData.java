package com.example.attendanceapp;

public class TeacherData {
    static String Admin;
    static String CollageId;

    TeacherData(){

    }

    public TeacherData(String admin, String collageId) {
        Admin = admin;
        CollageId = collageId;
    }

    public String getAdmin() {
        return Admin;
    }

    public void setAdmin(String admin) {
        Admin = admin;
    }

    public String getCollageId() {
        return CollageId;
    }

    public void setCollageId(String collageId) {
        CollageId = collageId;
    }
}
