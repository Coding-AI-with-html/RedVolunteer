package com.redvolunteer.utils.calendar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarFormatter {


    /**
     * Locate
     */
    private static final Locale  DATA_LOCALE = Locale.ENGLISH;
    private static final String MONTH_DATE_FORMAT = "MMM";


    /**
     * It formats a Data in order to retrieve a String containing
     */

    public static String getMonthDay(long dateMillis){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(dateMillis));
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month = new SimpleDateFormat(MONTH_DATE_FORMAT).format(cal.getInstance().getTime());
        return month.concat(" ").concat(day);

    }

    /**
     * it formats a Date in order to retrieve a String containing the date in English format
     */

    public static String getDate(long dateMilis){
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.LONG, DATA_LOCALE).format(new Date(dateMilis));

    }

    /**
     * it formats a date in order to retrieve a String containing the time expressed in hour and minutes
     */

    public static String getTime(long dateMillis){

        return DateFormat.getTimeInstance(DateFormat.SHORT, DATA_LOCALE).format(new Date(dateMillis));

    }

    private CalendarFormatter(){

    }
}
