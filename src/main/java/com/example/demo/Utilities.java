package com.example.demo;

import java.util.Calendar;
import java.util.Date;

public class Utilities {

    // adds numDays to date
    // numDays can be negative to subtract days
    public static Date addDays(Date date, int numDays)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, numDays); //minus number would decrement the days
        return cal.getTime();
    }

    public static int getDiffInDays(Date startDate, Date endDate) {
        int dayInSeconds = 1000 * 60 * 60 * 24;
        return (int) ((Math.abs(startDate.getTime() - endDate.getTime()) / dayInSeconds) + 1);
    }

}
