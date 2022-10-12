package com.martinkondor.mooncalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;

public class Utils {

    /**
     * Get the TextView children of an object
     * @param parent
     * @return
     */
    public static ArrayList getTextViewChildren(ViewGroup parent) {
        ArrayList<TextView> children = new ArrayList<TextView>();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View v = parent.getChildAt(i);
            if (v instanceof TextView) {
                children.add((TextView) v);
            }
        }
        return children;
    }

    public static String getMonthNameInHu(int monthIndex) {
        switch (monthIndex - 1) {
            case 0: return "január";
            case 1: return "február";
            case 2: return "március";
            case 3: return "április";
            case 4: return "május";
            case 5: return "június";
            case 6: return "július";
            case 7: return "augusztus";
            case 8: return "szeptember";
            case 9: return "október";
            case 10: return "november";
        }
        return "december";
    }

    public static int getDayInWeekIndex(String dayName) {
        if (dayName.equals("MONDAY") || dayName.equals("HÉTFŐ")) return 0;
        else if (dayName.equals("TUESDAY") || dayName.equals("KEDD")) return 1;
        else if (dayName.equals("WEDNESDAY") || dayName.equals("SZERDA")) return 2;
        else if (dayName.equals("THURSDAY") || dayName.equals("CSÜTÖRTÖK")) return 3;
        else if (dayName.equals("FRIDAY") || dayName.equals("PÉNTEK")) return 4;
        else if (dayName.equals("SATURDAY") || dayName.equals("SZOMBAT")) return 5;
        return 6;
    }

    @SuppressLint("NewApi")
    public static int getStartDayOfMonth(LocalDate now) {
        int dimi = now.getDayOfMonth();
        int diwi = Utils.getDayInWeekIndex(now.getDayOfWeek().toString());
        while (dimi != 1) {
            dimi--;
            if (--diwi == -1) {
                diwi = 6;
            }
        }
        return diwi;
    }

    public static boolean isLeapYear(int year) {
        boolean isLeap = false;

        if (year % 4 == 0) {
            if (year % 100 == 0) {
                if (year % 400 == 0) {
                    isLeap = true;
                }
                else {
                    isLeap = false;
                }
            }
            else {
                isLeap = true;
            }
        }
        else {
            isLeap = false;
        }
        return isLeap;
    }

    public static boolean isIn(int[] arr, int n) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == n) {
                return true;
            }
        }
        return false;
    }

    public static int getNumberOfDaysForMonth(int year, int month) {
        if (month == 2) {
            return Utils.isLeapYear(year) ? 29 : 28;
        }
        int[] monthsWith31Days = {1, 3, 5, 7, 8, 10, 12};
        return Utils.isIn(monthsWith31Days, month) ? 31 : 30;
    }

    /**
     *
     * @param time in format: HH:mm
     * @return int[] {Hour, Minute}
     */
    public static int[] getTime(String time) {
        String[] parts = time.split(":");
        parts[0] = parts[0].trim();
        parts[1] = parts[1].trim();
        return new int[] {
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1])
        };
    }

    /**
     * Creates a string from the input elements
     * that looks like this: start_time - end_time
     *
     * Example:
     * start_time = 10:00
     * end_time = 12:00
     * then this returns = "10:00 - 12:00"
     *
     * @return formatted string
     */
    public static String createTimeStringFromInput(
            TimePicker startTimeInput, TimePicker endTimeInput) {

        String sh = String.valueOf(startTimeInput.getHour());
        String sm = String.valueOf(startTimeInput.getMinute());
        String eh = String.valueOf(endTimeInput.getHour());
        String em = String.valueOf(endTimeInput.getMinute());
        sh = Utils.addZeroInCase(sh);
        sm = Utils.addZeroInCase(sm);
        eh = Utils.addZeroInCase(eh);
        em = Utils.addZeroInCase(em);
        return String.format("%s:%s - %s:%s", sh, sm, eh, em);
    }

    public static String addZeroInCase(int time) {
        return addZeroInCase(String.valueOf(time));
    }

    /**
     * Adds a zero before the time to make it look nicer
     * @param time (like 1, 9, 10, 34, 45)
     * @return (like 01, 09, 10, 34, 45)
     */
    public static String addZeroInCase(String time) {
        return time.length() == 1 ? ("0" + time) : time;
    }

    /**
     * Sends toast message to the view
     * @param ctx
     * @param msg
     */
    public static void sendToastMessage(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

}
