package de.familiep.mobileinformationgain.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter {
    public static String getDayAsString(long millis) {
        Date d = new Date(millis);
        return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(d);
    }
}
