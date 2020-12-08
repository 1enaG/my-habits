package com.example.myhabits;

public class MyDate {
    private int id;
    private String dateStr;
    private String day;

    public MyDate(){}

    public MyDate(int id, String dateStr, String day) {
        this.id = id;
        this.dateStr = dateStr;
        this.day = day;
    }

    public int getId() {
        return id;
    }

    public String getDateStr() {
        return dateStr;
    }

    public String getDay() {
        return day;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return  id +
                " " + dateStr +
                " " + day;
    }
}
