package com.tjrushby.runlite.util;

import java.util.Date;

public interface StringFormatter {
    int hoursMinutesSecondsToInt(String hoursMinutesSeconds);

    String dateToString(Date date);

    String averagePaceToMinutesSecondsString(double averagePace);

    String doubleToAveragePaceAudioCueString(double averagePace);

    String doubleToAveragePaceString(double averagePace);

    String doubleToDistanceString(double distanceDouble);

    String doubleToDistanceStringWithUnits(double distanceDouble);

    String intToHoursMinutesSeconds(int timeInSeconds);

    String intToMinutesSecondsAudioCueString(int timeInSeconds);

    void getDistanceUnitsFromSharedPreferences();

    double getDistanceUnits();

    String getDistanceUnitsString();

}
