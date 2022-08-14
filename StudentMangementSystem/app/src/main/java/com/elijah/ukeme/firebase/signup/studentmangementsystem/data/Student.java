package com.elijah.ukeme.firebase.signup.studentmangementsystem.data;

public class Student {
    private String name;
    private String regNum;
    private String attendance;
    private String date;
    private int mark;

    public Student() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegNum() {
        return regNum;
    }

    public void setRegNum(String regNum) {
        this.regNum = regNum;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getMark() {
        return mark;
    }

    public void setMark(int mark) {
        this.mark = mark;
    }

    public Student(String name, String regNum, String attendance, String date, int mark) {
        this.name = name;
        this.regNum = regNum;
        this.attendance = attendance;
        this.date = date;
        this.mark = mark;
    }
}
