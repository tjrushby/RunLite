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
    public int timeStringToSeconds(String hoursMinutesSeconds) {
        String[] split = hoursMinutesSeconds.split(":");

        if(split.length == 3) {
            // string contains hh:mm:ss
            int hours = Integer.parseInt(split[0]);
            int minutes = Integer.parseInt(split[1]);
            int seconds = Integer.parseInt(split[2]);

            return (hours * 60 * 60) + (minutes * 60) + seconds;
        } else {
            // string only mm:ss
            int minutes = Integer.parseInt(split[0]);
            int seconds = Integer.parseInt(split[1]);

            return minutes * 60 + seconds;
        }
    }

    @Override
    public String dateToString(Date date) {
        return dateFormat.format(date);
    }

    @Override
    public String averagePaceToTimeString(double averagePace) {
        // averagePace is always in min/km, * by the distanceUnits value to convert
        // to miles if needed
        int averagePaceAdjusted = (int) (averagePace * distanceUnits);

        long seconds = averagePaceAdjusted % 60;
        long minutes = (averagePaceAdjusted / 60) % 60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    public String averagePaceToTimeStringAudioCue(double averagePace) {
        if(averagePace == 0) {
            return "Unavailable";
        } else {
            return averagePaceToTimeString(averagePace) + " / " + distanceUnitsString;
        }
    }

    @Override
    public String averagePaceToTimeStringWithLabel(double averagePace) {
        return averagePaceToTimeString(averagePace) + " mins/" + distanceUnitsString;
    }

    @Override
    public String distanceToString(double distanceDouble) {
        return dfDistance.format(distanceDouble / distanceUnits);
    }

    @Override
    public String distanceToStringWithUnits(double distanceDouble) {
        return dfDistance.format(distanceDouble / distanceUnits) + " " + distanceUnitsString;
    }

    @Override
    public String secondsToTimeString(int timeInSeconds) {
        long seconds = timeInSeconds % 60;
        long minutes = (timeInSeconds / 60) % 60;
        long hours = (timeInSeconds / 60 / 60) % 60;

        if(hours < 1) {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }
    }

    @Override
    public String secondsToTimeStringAudioCue(int timeInSeconds) {
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
