package com.example.demo;

import com.example.demo.models.Attendance;

import java.util.ArrayList;
import java.util.List;

public class AttendanceWrapper {


    private List<Attendance> stringList = new ArrayList<>();

    public List<Attendance> getStringList() {
        return stringList;
    }

    public void setStringList(List<Attendance> stringList) {
        this.stringList = stringList;
    }
}
