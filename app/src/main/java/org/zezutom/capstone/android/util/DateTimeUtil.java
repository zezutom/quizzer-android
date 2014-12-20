package org.zezutom.capstone.android.util;

import android.text.format.DateUtils;
import android.util.Log;

import com.google.api.client.util.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateTimeUtil {

    public static final String TAG = DateTimeUtil.class.getSimpleName();

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.S'Z'";

    private DateTimeUtil() {}

    public static final DateTime toDateTime(String timeStamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        GregorianCalendar calendar = new GregorianCalendar();

        try {
            Date date = simpleDateFormat.parse(timeStamp);
            calendar.setTime(date);
            TimeZone timeZone = calendar.getTimeZone();
            calendar.add(GregorianCalendar.MILLISECOND, timeZone.getDSTSavings());
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            return new DateTime(calendar.getTimeInMillis());
        }
    }

    public static final String toString(DateTime dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTime.getValue());
        // return new SimpleDateFormat(DATE_TIME_FORMAT).format(dateTime.getValue());
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(calendar.getTime());
    }
}
