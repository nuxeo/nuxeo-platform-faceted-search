package org.nuxeo.ecm.platform.faceted.search.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.chrono.GregorianChronology;

public class DateMatcher {

    private boolean withYears = false;

    private boolean withMonth = false;

    private boolean withDay = false;

    private Calendar dateSuggestion;

    private final static Pattern YEAR_ONLY_MATCHER = Pattern.compile("^\\d{4}$");

    private final static Pattern MONTH_DIGIT_ONLY_MATCHER = Pattern.compile("^\\d{2}$");

    private final static Pattern YEAR_MONTHS_MATCHER = Pattern.compile("^\\d{4}[_ -:]\\d{2}$");

    private final static Pattern MONTHS_YEAR_MATCHER = Pattern.compile("^\\d{2}[_ -:]\\d{4}$");

    private final static Pattern MONTHS_DAY_YEAR_MATCHER = Pattern.compile("^\\d{2}[_ -:]\\d{2,}[_ -:]\\d{4}$");

    private final static Pattern YEAR_MONTHS_DAY_MATCHER = Pattern.compile("^\\d{4}[_ -:]\\d{2,}[_ -:]\\d{2}$");

    private DateMatcher(boolean withYears, boolean withMonth,
            boolean witDay, Calendar dateSuggestion) {
        super();
        this.withYears = withYears;
        this.withMonth = withMonth;
        this.withDay = witDay;
        this.dateSuggestion = dateSuggestion;
    }

    public boolean isWithYears() {
        return withYears;
    }

    public void setWithYears(boolean withYears) {
        this.withYears = withYears;
    }

    public boolean isWithMonth() {
        return withMonth;
    }

    public void setWithMonth(boolean withMonth) {
        this.withMonth = withMonth;
    }

    public boolean isWitDay() {
        return withDay;
    }

    public void setWitDay(boolean witDay) {
        this.withDay = witDay;
    }

    public Calendar getDateSuggestion() {
        return dateSuggestion;
    }

    public boolean hasMatch() {
        return getDateSuggestion() != null;
    }

    public static Matcher parsingDate(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input.trim());
        return matcher;
    }

    public static DateMatcher fromInput(String input) {
        Matcher matcher = parsingDate(YEAR_ONLY_MATCHER, input);

        if (matcher.find()) {

            return new DateMatcher(true, false, false, dateToInstance(
                    Integer.parseInt(matcher.group()), 1, 1));
        }
        matcher = parsingDate(MONTH_DIGIT_ONLY_MATCHER, input);
        if (matcher.find()) {
            int month = Integer.parseInt(matcher.group());
            if (month > 12) {
                return new DateMatcher(false, true, false, null);
            }
            return new DateMatcher(false, true, false,
                    dateToInstance(
                            GregorianCalendar.getInstance().get(
                                    GregorianCalendar.YEAR), month, 1));
        }
        matcher = parsingDate(YEAR_MONTHS_MATCHER, input);
        if (matcher.find()) {
            int month = Integer.parseInt(matcher.group().substring(5));
            if (month > 12 || month < 1) {
                return new DateMatcher(true, true, false, null);
            }
            int year = Integer.parseInt(matcher.group().substring(0, 4));

            return new DateMatcher(true, true, false, dateToInstance(year,
                    month, 1));
        }
        matcher = parsingDate(MONTHS_YEAR_MATCHER, input);
        if (matcher.find()) {
            int month = Integer.parseInt(matcher.group().substring(0, 2));
            if (month > 12 || month < 1) {
                return new DateMatcher(true, true, false, null);
            }
            int year = Integer.parseInt(matcher.group().substring(3));

            return new DateMatcher(true, true, false, dateToInstance(year,
                    month, 1));

        }
        matcher = parsingDate(MONTHS_DAY_YEAR_MATCHER, input);
        if (matcher.find()) {
            int first = Integer.parseInt(matcher.group().substring(0, 2));
            int second = Integer.parseInt(matcher.group().substring(3, 5));
            int year = Integer.parseInt(matcher.group().substring(6));
            int control = first + second;
            if (control < 2 || control > 12 + 31 || first < 1 || second < 1) {
                return new DateMatcher(true, true, true, null);
            } else if (control < 12 + 12 + 1) {
                new DateMatcher(true, true, true, dateToInstance(year,
                        first, second));
            }
            int month = first;
            int day = second;
            if (first > second) {
                month = second;
                day = first;
            }
            Calendar dateToInstance = null; 
            try {
                dateToInstance=    dateToInstance(year,
                        month, day);
            } catch (Exception e) {
            }
            return new DateMatcher(true, true, true, dateToInstance);
        }
        matcher = parsingDate(YEAR_MONTHS_DAY_MATCHER, input);
        if (matcher.find()) {
            int year = Integer.parseInt(matcher.group().substring(0, 4));
            int first = Integer.parseInt(matcher.group().substring(5, 7));
            int second = Integer.parseInt(matcher.group().substring(8));
            int control = first + second;
            if (control < 2 || control > 12 + 31 || first < 1 || second < 1) {
                return new DateMatcher(true, true, true, null);
            } else if (control < 12 + 12 + 1) {
                new DateMatcher(true, true, true, dateToInstance(year,
                        first, second));
            }
            int month = first;
            int day = second;
            if (first > second) {
                month = second;
                day = first;
            }
            Calendar dateToInstance = null; 
            try {
                dateToInstance=    dateToInstance(year,
                        month, day);
            } catch (Exception e) {
            }
            return new DateMatcher(true, true, true,dateToInstance);
        }

        return new DateMatcher(false, false, false, null);
    }

    protected static Calendar dateToInstance(int year, int month, int day) {

        Chronology chrono = GregorianChronology.getInstance();
        DateTime dt = new DateTime(year, month, day, 12, 0, 0, 0, chrono);
        Calendar gregorianCalendar = dt.toGregorianCalendar();
        return gregorianCalendar;
    }

}
