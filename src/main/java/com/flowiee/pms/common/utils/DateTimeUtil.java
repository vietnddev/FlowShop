package com.flowiee.pms.common.utils;

import com.flowiee.pms.common.enumeration.FilterDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
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

    public static LocalDateTime[] getFromDateToDate(FilterDate pFilterDate) {
        LocalDateTime lvFromDate = null;
        LocalDateTime lvEndDate = null;

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToDay = today.atTime(LocalTime.MIN);
        LocalDateTime endOfToDay = today.atTime(LocalTime.MAX);

        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonthValue());
        LocalDateTime startDayOfMonth = yearMonth.atDay(1).atTime(LocalTime.MIN);
        LocalDateTime endDayOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        switch (pFilterDate) {
            case ToDay:
                lvFromDate = startOfToDay;
                lvEndDate = endOfToDay;
                break;
            case PreviousDay:
                lvFromDate = startOfToDay.minusDays(1);
                lvEndDate = endOfToDay.minusDays(1);
                break;
            case SevenDaysAgo:
                lvFromDate = startOfToDay.minusDays(7);
                lvEndDate = endOfToDay;
                break;
            case ThisMonth:
                lvFromDate = startDayOfMonth;
                lvEndDate = endDayOfMonth;
                break;
            case PreviousMonth:
                lvFromDate = startDayOfMonth.minusMonths(1);
                lvEndDate = endDayOfMonth.minusMonths(1);
        }

        return new LocalDateTime[] {lvFromDate, lvEndDate};
    }

    public static LocalDateTime[] getFromDateToDate(LocalDateTime pFromDate, LocalDateTime pToDate, String pFilterDate) {
        LocalDateTime lvStartTime = null;
        LocalDateTime lvEndTime = null;

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToDay = today.atTime(LocalTime.MIN);
        LocalDateTime endOfToDay = today.atTime(LocalTime.MAX);

        YearMonth yearMonth = YearMonth.of(today.getYear(), today.getMonthValue());
        LocalDateTime startDayOfMonth = yearMonth.atDay(1).atTime(LocalTime.MIN);
        LocalDateTime endDayOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        switch (pFilterDate) {
            case "T0": //Today
                pFromDate = startOfToDay;
                pToDate = endOfToDay;
                break;
            case "T-1": //Previous day
                pFromDate = startOfToDay.minusDays(1);
                pToDate = endOfToDay.minusDays(1);
                break;
            case "T-7": //7 days ago
                pFromDate = startOfToDay.minusDays(7);
                pToDate = endOfToDay;
                break;
            case "M0": //This month
                pFromDate = startDayOfMonth;
                pToDate = endDayOfMonth;
                break;
            case "M-1": //Previous month
                pFromDate = startDayOfMonth.minusMonths(1);
                pToDate = endDayOfMonth.minusMonths(1);
        }

        lvStartTime = pFromDate;
        lvEndTime = pToDate;

        return new LocalDateTime[] {lvStartTime, lvEndTime};
    }

    public static LocalDateTime getFilterStartTime(LocalDateTime pTime) {
        if (pTime != null) {
            return pTime;
        }
        return DateTimeUtil.MIN_TIME;
    }

    public static LocalDateTime getFilterEndTime(LocalDateTime pTime) {
        if (pTime != null) {
            return pTime;
        }
        return DateTimeUtil.MAX_TIME;
    }

    public static LocalDate parseToLocalDate(String input, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(input, formatter);
    }
}