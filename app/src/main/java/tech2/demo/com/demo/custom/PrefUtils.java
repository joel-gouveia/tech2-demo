package tech2.demo.com.demo.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Joel on 02-Feb-16.
 */
public class PrefUtils {
    public static final String PREF_CURRENT_USER_KEY = "pref_previous_user_key";
    public static final String PREF_CURRENT_USER_TOKEN = "pref_previous_user_token";

    public static String getCurrentUserKey(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_CURRENT_USER_KEY, "");
    }

    public static void setCurrentUserKey(final Context context, String savedStatusKey) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_CURRENT_USER_KEY, savedStatusKey).apply();
    }

    public static String getCurrentUserToken(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_CURRENT_USER_TOKEN, "");
    }

    public static void setCurrentUserToken(final Context context, String savedStatusKey) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_CURRENT_USER_TOKEN, savedStatusKey).apply();
    }
}
