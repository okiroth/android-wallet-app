package com.palebluepagos.androidwallet.utilities;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeUtils {

    public final static long ONE_SECOND = 1000;
    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long ONE_DAY = ONE_HOUR * 24;
    public final static long SECONDS    = 60;
    public final static long MINUTES    = 60;
    public final static long HOURS    = 24;

    private TimeUtils() {
    }

    /**
     * Return date in specified format.
     *
     * @param date Date in milliseconds
     * @return String representing date in specified format
     */
    public static String getSimpleDate(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return formatter.format(calendar.getTime());
    }

    /**
     * Return date in specified format.
     *
     * @param date Date in milliseconds
     * @return String representing date in specified format
     */
    public static String getFullDate(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM / kk:mm");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return formatter.format(calendar.getTime()) + " hs";
    }
}