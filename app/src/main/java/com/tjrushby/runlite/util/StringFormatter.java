package com.tjrushby.runlite.util;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringFormatter {
    private DecimalFormat dfDistance;
    private SimpleDateFormat dateFormat;

    public StringFormatter() {
        dfDistance = new DecimalFormat("#0.00");
        dateFormat = new SimpleDateFormat("dd/MM/yy, kk:mm");
    }

    public String dateToString(Date date) {
        return dateFormat.format(date);
    }

    public String doubleToDistanceString(double distanceDouble) {
        return dfDistance.format(distanceDouble);
    }

    public String longToMinutesSeconds(long timeInSeconds) {
        long seconds = timeInSeconds % 60;
        long minutes = timeInSeconds / 60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
