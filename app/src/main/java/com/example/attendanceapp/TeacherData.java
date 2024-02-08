package com.example.attendanceapp;

public class TeacherData {
    String Admin;
    String CollageId;
    String Name;
    String Mobile;


    public TeacherData() {
    }
    public TeacherData(String admin, String collageId, String name, String mobile) {
        Admin = admin;
        CollageId = collageId;
        Name = name;
        Mobile = mobile;
    }

    public  String getAdmin() {
        return Admin;
    }

    public void setAdmin(String admin) {
        Admin = admin;
    }

    public  String getCollageId() {
        return CollageId;
    }

    public  void setCollageId(String collageId) {
        CollageId = collageId;
    }

    public  String getName() {
        return Name;
    }

    public  void setName(String name) {
        Name = name;
    }

    public  String getMobile() {
        return Mobile;
    }

    public  void setMobile(String mobile) {
        Mobile = mobile;
    }
}
