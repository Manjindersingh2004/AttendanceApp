package com.example.attendanceapp;

public class StudentDataModel {
    String ROLL_NO;
    String NAME;
    String GROUP;
    String ATEND_LEC;
    String TOTAL_LEC;
    String PERCENTAGE;
    StudentDataModel(String rollno,String name,String group,String atendlec,String totallec,String percentage){
        ROLL_NO=rollno;
        NAME=name;
        GROUP=group;
        ATEND_LEC=atendlec;
        TOTAL_LEC=totallec;
        PERCENTAGE=percentage;
    }
}
