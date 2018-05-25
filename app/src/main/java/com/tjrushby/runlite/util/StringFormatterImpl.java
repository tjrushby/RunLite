package com.tjrushby.runlite.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tjrushby.runlite.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StringFormatterImpl implements StringFormatter {
    private double distanceUnits;

    private Context context;
    private DecimalFormat dfDistance;
    private SimpleDateFormat dateFormat;
    private SharedPreferences sharedPrefs;

    private String distanceUnitsString;

    public StringFormatterImpl(Context context) {
        this.context = context;
        dfDistance = new DecimalFormat("#0.00");
        dateFormat = new SimpleDateFormat("dd/MM/yy, kk:mm");

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        getDistanceUnitsFromSharedPreferences();
    }

    @Override
    public long minutesSecondsToLong(String minutesSeconds) {
        String[] split = minutesSeconds.split(":");
        long minutes = Long.parseLong(split[0]);
        long seconds = Long.parseLong(split[1]);

        return minutes * 60 + seconds;
    }

    @Override
    public String dateToString(Date date) {
        return dateFormat.format(date);
    }

    @Override
    public String doubleToDistanceString(double distanceDouble) {
        return dfDistance.format(distanceDouble / distanceUnits);
    }

    @Override
    public String doubleToDistanceStringWithUnits(double distanceDouble) {
        return dfDistance.format(distanceDouble / distanceUnits) + " " + distanceUnitsString;
    }

    @Override
    public String longToAveragePaceAudioCueString(long timeInSeconds) {
        if(timeInSeconds == 0) {
            return "Unavailable";
        } else {
            return longToMinutesSecondsAudioCueString(timeInSeconds) + " / " + distanceUnitsString;
        }
    }

    @Override
    public String longToAveragePaceString(long timeInSeconds) {
        return " Mins/" + distanceUnitsString + " " + longToMinutesSeconds(timeInSeconds);
    }

    @Override
    public String longToMinutesSeconds(long timeInSeconds) {
        long seconds = timeInSeconds % 60;
        long minutes = timeInSeconds / 60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    public String longToMinutesSecondsAudioCueString(long timeInSeconds) {
        long seconds = timeInSeconds % 60;
        long minutes = timeInSeconds / 60;

        if(minutes < 1) {
            return String.format(Locale.getDefault(), "%d seconds", seconds);
        } else {
            return String.format(
                    Locale.getDefault(), "%d minutes, %d seconds", minutes, seconds
            );
        }
    }

    @Override
    public void getDistanceUnitsFromSharedPreferences() {
        distanceUnits = Double.parseDouble(
                sharedPrefs.getString(
                        context.getString(R.string.pref_distance_units_key),
                        context.getResources().getStringArray(R.array.distance_units_values)[0])
        );

        if(distanceUnits == 1) {
            // kilometers
            distanceUnitsString = context.getResources()
                    .getStringArray(R.array.distance_units_options_short)[0];
        } else {
            distanceUnitsString = context.getResources()
                    .getStringArray(R.array.distance_units_options_short)[1];
        }
    }

    @Override
    public double getDistanceUnits() {
        return distanceUnits;
    }

    @Override
    public String getDistanceUnitsString() {
        return distanceUnitsString;
    }
}
