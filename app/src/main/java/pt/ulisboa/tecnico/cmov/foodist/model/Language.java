package pt.ulisboa.tecnico.cmov.foodist.model;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Locale;

public class Language {
    public static final int DEFAULT = 0;
    private static Locale currentLocale;
    private Locale locale;

    private Language(String language) {
        this.locale = new Locale(language);
    }

    public static ArrayList<Language> getInstance(final Context context) {
        currentLocale = context.getResources().getConfiguration().locale;

        ArrayList<Language> languagesList = new ArrayList<>();
        languagesList.add(new Language("en"));        // 0
        languagesList.add(new Language("fr"));        // 1
        languagesList.add(new Language("pt"));        // 2
        return languagesList;
    }

    @NonNull
    @Override
    public String toString() {
        String str = locale.getDisplayLanguage(currentLocale);
        return str.substring(0, 1).toUpperCase() + str.substring(1); // for capitalized locale
    }

    public Locale getLocale() {
        return locale;
    }
}
