package com.example.demo;

import com.example.demo.models.Attendance;

import java.util.ArrayList;
import java.util.List;

// this class is necessary to get a list of objects through a form
// there is probably a better way, but this works just fine
public class AttendanceWrapper {

    private List<Attendance> attendanceList = new ArrayList<>();

    public List<Attendance> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<Attendance> attendanceList) {
        this.attendanceList = attendanceList;
    }
}
