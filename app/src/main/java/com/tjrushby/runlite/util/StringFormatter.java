package com.tjrushby.runlite.util;

import java.util.Date;

public interface StringFormatter {
    int timeStringToSeconds(String hoursMinutesSeconds);

    String dateToString(Date date);

    String averagePaceToTimeString(double averagePace);

    String averagePaceToTimeStringAudioCue(double averagePace);

    String averagePaceToTimeStringWithLabel(double averagePace);

    String distanceToString(double distanceDouble);

    String distanceToStringWithUnits(double distanceDouble);

    String secondsToTimeString(int timeInSeconds);

    String secondsToTimeStringAudioCue(int timeInSeconds);

    void getDistanceUnitsFromSharedPreferences();

    double getDistanceUnits();

    String getDistanceUnitsString();

}
