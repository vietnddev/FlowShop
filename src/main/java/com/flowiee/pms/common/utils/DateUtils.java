package com.flowiee.pms.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static Date convertStringToDate(String dateString, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            if (dateString != null) {
                date = dateFormat.parse(dateString);
            }
        } catch (ParseException e) {
            System.out.println("Error occurred while parsing date: " + e.getMessage());
        }
        return date;
    }
}