package com.example.attendanceapp;

public class VerificationDataModel {
    String Name;
    String Mobile;
    String Email;
    String Verified;

    public VerificationDataModel(){

    }

    public VerificationDataModel(String name, String mobile, String email, String verified) {
        Name = name;
        Mobile = mobile;
        Email = email;
        Verified = verified;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getVerified() {
        return Verified;
    }

    public void setVerified(String verified) {
        Verified = verified;
    }
}
