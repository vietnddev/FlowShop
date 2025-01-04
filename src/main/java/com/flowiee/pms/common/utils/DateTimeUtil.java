package com.flowiee.pms.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DateTimeUtil.class);

    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE = "dd/MM/yyyy";
    public static final String START_TIME = "00:00:00";
    public static final String END_TIME = "23:59:59";
    public static final String START_TIME_MILS_UTC = "00:00:00.000Z";
    public static final String END_TIME_MILS_UTC = "23:59:00.999Z";
    public static final String FORMAT_DATE_TIME_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String FORMAT_DATE_TIMES_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String FORMAT_DATE_UTC = "yyyy-MM-dd'T'";
    public static final String START_TIME_UTC = "00:00:00.000Z";
    public static final String END_TIME_UTC = "23:59:59.999Z";
    public static final String FORMAT_DATETIME_ZONE = "yyyy/MM/dd HH:mm:ss Z";
    public static final String FORMAT_DATETIME = "dd/MM/yyyy HH:mm:ss";
    public static final String FORMAT_SPLASH_DATE = "yyyy/MM/dd";
    public static final LocalDateTime MIN_TIME = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
    public static final LocalDateTime MAX_TIME = LocalDateTime.of(2100, 12, 1, 0, 0, 0);

    public static LocalDateTime convertStringToDateTime(String datetime) {
        return convertStringToDateTime(datetime, "MM/dd/yyyy h:mm a");
    }

    public static LocalDateTime convertStringToDateTime(String datetime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        if (isDateFormat(datetime)) {
            LocalDate localDate = LocalDate.parse(datetime, formatter);
            return localDate.atStartOfDay();
        }
        return LocalDateTime.parse(datetime, formatter);
    }

    public static boolean isDateFormat(String input) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            LocalDate.parse(input, dateFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}