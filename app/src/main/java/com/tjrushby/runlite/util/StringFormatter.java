package com.tjrushby.runlite.util;

import java.util.Date;

public interface StringFormatter {
    long minutesSecondsToLong(String minutesSeconds);

    String dateToString(Date date);

    String doubleToDistanceString(double distanceDouble);

    String doubleToDistanceStringWithUnits(double distanceDouble);

    String longToAveragePaceString(long timeInSeconds);

    String longToMinutesSeconds(long timeInSeconds);

    void getDistanceUnitsFromSharedPreferences();

    double getDistanceUnits();

    String getDistanceUnitsString();

}
