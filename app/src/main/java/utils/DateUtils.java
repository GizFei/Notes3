package utils;

import android.text.format.DateFormat;

import java.util.Date;

public class DateUtils {

    public static String getDateString(Date date){
        return DateFormat.format("yyyyMMEE", date).toString();
    }

}
