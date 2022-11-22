package com.martinkondor.mooncalendar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {
    /**
     * Most likely used preference names:
     * - lod: The lastly opened day in the CalendarActivity
     * - cy: The current year
     * - cm: The current month
     * - acd: The current active day
     */

    public static void saveSharedPreference(Activity activity, String name, int data) {
        SharedPreferences preferences = activity.getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(name, data);
        editor.commit();
    }

    public static void saveSharedPreference(Activity activity, String name, String data) {
        SharedPreferences preferences = activity.getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, data);
        editor.commit();
    }

    public static String getSharedPreferenceString(Activity activity, String name) {
        SharedPreferences preferences = activity.getSharedPreferences("config", Context.MODE_PRIVATE);
        return preferences.getString(name, "");
    }

    public static int getSharedPreferenceInt(Activity activity, String name) {
        SharedPreferences preferences = activity.getSharedPreferences("config", Context.MODE_PRIVATE);
        return preferences.getInt(name, 0);
    }

}
