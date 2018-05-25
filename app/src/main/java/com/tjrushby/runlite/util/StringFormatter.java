package com.tjrushby.runlite.util;

import java.util.Date;

public interface StringFormatter {
    long minutesSecondsToLong(String minutesSeconds);

    String dateToString(Date date);

    String doubleToDistanceString(double distanceDouble);

    String doubleToDistanceStringWithUnits(double distanceDouble);

    String longToAveragePaceAudioCueString(long timeInSeconds);

    String longToAveragePaceString(long timeInSeconds);

    String longToMinutesSeconds(long timeInSeconds);

    String longToMinutesSecondsAudioCueString(long timeInSeconds);

    void getDistanceUnitsFromSharedPreferences();

    double getDistanceUnits();

    String getDistanceUnitsString();

}
