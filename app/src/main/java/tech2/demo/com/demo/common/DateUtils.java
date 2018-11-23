package tech2.demo.com.demo.common;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Locale;

import tech2.demo.com.demo.model.Meals;

/**
 * Created by Joel on 01-Mar-16.
 */
public class DateUtils {

    private static final Locale LOCAL = Locale.UK;
    private static final String FORMAT_TYPE = "%02d";

    private static final String fullPattern = "dd/MM/yyyy HH:mm";
    private static final String datePattern = "dd/MM/yyyy";
    private static final String timePattern = "HH:mm";

    public static String getCurrentDate() {
        Calendar currentTime = Calendar.getInstance();
        return formatDate(currentTime.get(Calendar.DAY_OF_MONTH), currentTime.get(Calendar.MONTH), currentTime.get(Calendar.YEAR));
    }

    public static String getCurrentHour() {
        Calendar currentTime = Calendar.getInstance();
        return formatTime(currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE));
    }

    public static String formatDate(int dayOfMonth, int monthOfYear, int year) {

        return String.format(LOCAL, FORMAT_TYPE, dayOfMonth)
                + "/"
                + String.format(LOCAL, FORMAT_TYPE, (monthOfYear + 1))
                + "/"
                + year;
    }

    public static String formatTime(int hourOfDay, int minute) {

        return String.format(LOCAL, FORMAT_TYPE, hourOfDay)
                + ":"
                + String.format(LOCAL, FORMAT_TYPE, minute);
    }

    public static boolean isWithinDateAndTime(Meals meal, String fromDate, String toDate, String fromTime, String toTime) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(fullPattern);
        DateTime dt = formatter.parseDateTime(meal.getDate() + " " + meal.getTime());
        DateTime fromdt = formatter.parseDateTime(fromDate + " " + fromTime);
        DateTime todt = formatter.parseDateTime(toDate + " " + toTime);

        if (checkDateTimePeriod(dt, fromdt, todt)) {
            return true;
        }
        return false;
    }

    public static boolean isWithinDate(Meals meal, String fromDate, String toDate) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(datePattern);
        DateTime dt = formatter.parseDateTime(meal.getDate());
        DateTime fromdt = formatter.parseDateTime(fromDate);
        DateTime todt = formatter.parseDateTime(toDate);

        if (checkDateTimePeriod(dt, fromdt, todt)) {
            return true;
        }
        return false;
    }

    public static boolean isWithinTime(Meals meal, String fromTime, String toTime) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(timePattern);
        DateTime dt = formatter.parseDateTime(meal.getTime());
        DateTime fromdt = formatter.parseDateTime(fromTime);
        DateTime todt = formatter.parseDateTime(toTime);

        if (checkDateTimePeriod(dt, fromdt, todt)) {
            return true;
        }
        return false;
    }

    private static boolean checkDateTimePeriod(DateTime dt, DateTime fromdt, DateTime todt) {
        if (fromdt.isBefore(dt) || fromdt.isEqual(dt)) {
            if (todt.isAfter(dt) || todt.isEqual(dt)) {
                return true;
            }
        }
        return false;
    }
}
