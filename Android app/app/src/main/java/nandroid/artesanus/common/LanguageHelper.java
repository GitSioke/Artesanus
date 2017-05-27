package nandroid.artesanus.common;

import android.app.Activity;
import android.content.Context;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Locale;

/**
 * This class deals with Language issues
 */

public class LanguageHelper
{
    private static final ArrayList<String> languagesList = new ArrayList<String>();
    private static String _localeCode = "es";
    static
    {
        languagesList.add("es");
        languagesList.add("en");
    }

    public static boolean changeLanguage(Context context, String langCode)
    {
        boolean ret = false;
        Locale newLocale = new Locale(langCode);
        Locale.setDefault(newLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        if (_localeCode != langCode)
        {
            // We have a different locale, so change it and warn activity about change
            config.locale = newLocale;
            _localeCode = newLocale.toString();
            ret = true;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }

        return ret;
    }

    public static String getLanguageCode(int languagePosition)
    {
        String retVal = "es";
        if (languagePosition > -1)
        {
            retVal = languagesList.get(languagePosition);
        }
        return retVal;
    }

    public static int getLanguagePosition(String languageCode)
    {
        return languagesList.indexOf(languageCode);
    }
}
