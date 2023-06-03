package main.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {
    public static String HOUR_MIN = "HH:mm";

    public static String changeDateToHourMin(String dateStr) {
        // 2022-11-15 12:21 -> 12:21
        String res;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = simpleDateFormat.parse(dateStr);
            SimpleDateFormat formatHourMin = new SimpleDateFormat("HH:mm");
            res = formatHourMin.format(date);
        } catch (ParseException exception) {
            return dateStr;
        }
        return res;
    }

    public static String getHourMinNow() {
        SimpleDateFormat df = new SimpleDateFormat(HOUR_MIN);
        return df.format(Calendar.getInstance().getTime());
    }

    public static boolean isTimeOrdered(String before, String after) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dateBefore = simpleDateFormat.parse(before);
            Date dateAfter = simpleDateFormat.parse(after);
            return dateAfter.after(dateBefore);
        } catch (ParseException ex) {
            return false;
        }
    }

    public static boolean isValidTimeStr(String timeStr) {
        if (timeStr == null) {
            return false;
        }
        if (timeStr.contains("/")) {
            return false;
        }
        return !TextUtils.isEmpty(timeStr);
    }
}
