package com.firebase.notti.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
    public static SimpleDateFormat sdf_short = new SimpleDateFormat("dd.MM", Locale.US);
    public static final SimpleDateFormat sdf_short_hour = new SimpleDateFormat("HH:mm", Locale.US);

    public static String getDateText_old(long d_note_timestamp) {
        String textToShow = "";
        Date d_now = new Date(System.currentTimeMillis());
        Date d_note = new Date(d_note_timestamp);
        // Get calendar instances for comparison
        Calendar calNow = Calendar.getInstance();
        calNow.setTime(d_now);

        Calendar calNote = Calendar.getInstance();
        calNote.setTime(d_note);

        // Check if d_note is today
        if (calNow.get(Calendar.YEAR) == calNote.get(Calendar.YEAR) && calNow.get(Calendar.MONTH) == calNote.get(Calendar.MONTH)) {
            if (calNow.get(Calendar.DAY_OF_YEAR) == calNote.get(Calendar.DAY_OF_YEAR)) {
                textToShow = "היום";
            }
            else {
                // Format the date as "dd/MM" (or another preferred format)
                textToShow = sdf_short.format(d_note);
            }
        }
        // Check if d_note is yesterday
        else {
            calNow.add(Calendar.DAY_OF_YEAR, -1); // Move one day back
            if (calNow.get(Calendar.YEAR) == calNote.get(Calendar.YEAR) && calNow.get(Calendar.MONTH) == calNote.get(Calendar.MONTH) &&
                    calNow.get(Calendar.DAY_OF_YEAR) == calNote.get(Calendar.DAY_OF_YEAR)) {
                textToShow = "אתמול";
            }
            else {
                // Format the date as "dd/MM/yyyy" (or another preferred format)
                textToShow = sdf.format(d_note);
            }
        }

        return textToShow;
    }

    public static String getDateText(long d_note_timestamp) {
        String textToShow = "";
        Date d_now = new Date(System.currentTimeMillis());
        Date d_note = new Date(d_note_timestamp);
        // Get calendar instances for comparison
        Calendar calNow = Calendar.getInstance();
        calNow.setTime(d_now);

        Calendar calNote = Calendar.getInstance();
        calNote.setTime(d_note);

        // Check if d_note is today
        if (calNow.get(Calendar.YEAR) == calNote.get(Calendar.YEAR) && calNow.get(Calendar.MONTH) == calNote.get(Calendar.MONTH)) {
            if (calNow.get(Calendar.DAY_OF_YEAR) == calNote.get(Calendar.DAY_OF_YEAR)) {
                if (calNow.get(Calendar.HOUR) == calNote.get(Calendar.HOUR)) {
                    if (calNow.get(Calendar.MINUTE) == calNote.get(Calendar.MINUTE)) {
                        textToShow = "הרגע";
                    }
                    else if (calNow.get(Calendar.MINUTE) < calNote.get(Calendar.MINUTE) + 5) {
                        textToShow = "לאחרונה";
                    }
                    else {
                        textToShow = "בשעה האחרונה";
                    }
                }
                else {
                    textToShow = sdf_short_hour.format(d_note);
                }
            }
            else {
                // Format the date as "dd/MM" (or another preferred format)
                textToShow = sdf_short.format(d_note);
            }
        }
        // Check if d_note is yesterday
        else {
            calNow.add(Calendar.DAY_OF_YEAR, -1); // Move one day back
            if (calNow.get(Calendar.YEAR) == calNote.get(Calendar.YEAR) && calNow.get(Calendar.MONTH) == calNote.get(Calendar.MONTH) &&
                    calNow.get(Calendar.DAY_OF_YEAR) == calNote.get(Calendar.DAY_OF_YEAR)) {
                textToShow = "אתמול";
            }
            else {
                // Format the date as "dd/MM/yyyy" (or another preferred format)
                textToShow = sdf.format(d_note);
            }
        }

        return textToShow;
    }
}
