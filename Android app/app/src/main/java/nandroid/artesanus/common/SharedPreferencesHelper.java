package nandroid.artesanus.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.SharedPreferencesCompat;

import nandroid.artesanus.gui.MenuActivity;

/**
 * This class will deals with SharedPreferences class to add, update or delete preferences
 */

public class SharedPreferencesHelper
{
    private static final String languagePreference = "language";
    private static final String ipAddressPreference = "ip_address";

    public static void updatePreference(Context ctx, String preference, String value)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(preference, value);
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public static String getPreference(Context ctx, String preference)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(preference, "");
    }

    public static void setDefaultPreferences(Context ctx)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        if (preferences.getString(languagePreference, "").equals(""))
        {
            editor.putString(languagePreference, "es");
        }
        if (preferences.getString(ipAddressPreference, "").equals(""))
        {
            editor.putString(ipAddressPreference, "192.168.1.40");
        }

        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public static String getLanguagePreference(Context ctx)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(languagePreference, "");
    }

    public static String getIPAddressPreference(Context ctx)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getString(ipAddressPreference, "");
    }
}
