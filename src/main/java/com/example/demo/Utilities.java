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


    // returns negative number if start date is after end date, returns 0 if both dates are the same
    public static int getDiffInDays(Date startDate, Date endDate) {
//        int dayInSeconds = 1000 * 60 * 60 * 24;
        int diffInDays = (int) ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24));

        if(diffInDays > 0) {
            return diffInDays + 1;
        }
        else if (diffInDays < 0) {
            return diffInDays - 1;
        }
        else return 0;
    }

}
