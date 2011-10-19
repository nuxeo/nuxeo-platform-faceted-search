package org.nuxeo.ecm.platform.faceted.search.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateMatcherUtil {
    private boolean withYears = false;

    private boolean withMonth = false;

    private boolean witDay = false;

    private Calendar dateSuggestion;

    private final static Pattern YEAR_ONLY_MATCHER = Pattern.compile("^\\d{4}$");

    private final static Pattern MONTH_DIGIT_ONLY_MATCHER = Pattern.compile("^\\d{2}$");

    private final static Pattern YEAR_MONTHS_MATCHER = Pattern.compile("^\\d{4}[_ -:]\\d{2}$");

    private final static Pattern MONTHS_YEAR_MATCHER = Pattern.compile("^\\d{2}[_ -:]\\d{4}$");

    private final static Pattern MONTHS_DAY_YEAR_MATCHER = Pattern.compile("^\\d{2}[_ -:]\\d{2,}[_ -:]\\d{4}$");

    private final static Pattern YEAR_MONTHS_DAY_MATCHER = Pattern.compile("^\\d{4}[_ -:]\\d{2,}[_ -:]\\d{2}$");

    private DateMatcherUtil(boolean withYears, boolean withMonth,
            boolean witDay, Calendar dateSuggestion) {
        super();
        this.withYears = withYears;
        this.withMonth = withMonth;
        this.witDay = witDay;
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
        return witDay;
    }



    public void setWitDay(boolean witDay) {
        this.witDay = witDay;
    }



    public Calendar getDateSuggestion() {
        return dateSuggestion;
    }



    public void setDateSuggestion(Calendar dateSuggestion) {
        this.dateSuggestion = dateSuggestion;
    }



    public static Matcher parsingDate(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input.trim());
        return matcher;
    }

    public static DateMatcherUtil fromInput(String input) {
        Matcher matcher = parsingDate(YEAR_ONLY_MATCHER, input);
        if (matcher.find()) {

            return new DateMatcherUtil(true, false, false, dateToInstance(
                    Integer.parseInt(matcher.group()), 1, 1));
        }
        matcher = parsingDate(MONTH_DIGIT_ONLY_MATCHER, input);
        if (matcher.find()) {
            int month = Integer.parseInt(matcher.group());
            if (month > 12) {
                return null;
            }
            return new DateMatcherUtil(false, true, false,
                    dateToInstance(
                            GregorianCalendar.getInstance().get(
                                    GregorianCalendar.YEAR), month, 1));
        }
        matcher = parsingDate(YEAR_MONTHS_MATCHER, input);
        if (matcher.find()) {
            int month = Integer.parseInt(matcher.group().substring(5));
            if (month > 12 || month < 1) {
                return null;
            }
            int year = Integer.parseInt(matcher.group().substring(0, 4));

            return new DateMatcherUtil(true, true, false, dateToInstance(year,
                    month, 1));
        }
        matcher = parsingDate(MONTHS_YEAR_MATCHER, input);
        if (matcher.find()) {
            int month = Integer.parseInt(matcher.group().substring(0, 2));
            if (month > 12 || month < 1) {
                return null;
            }
            int year = Integer.parseInt(matcher.group().substring(3));

            return new DateMatcherUtil(true, true, false, dateToInstance(year,
                    month, 1));

        }
        matcher = parsingDate(MONTHS_DAY_YEAR_MATCHER, input);
        if (matcher.find()) {
            int first = Integer.parseInt(matcher.group().substring(0, 2));
            int second = Integer.parseInt(matcher.group().substring(3, 5));
            int year = Integer.parseInt(matcher.group().substring(6));
            int control = first + second ;
            if(control < 2 || control > 12 + 31 ){
                return null ;
            }else if (control < 12 + 12 + 1 ){
                new DateMatcherUtil(true, true, true, dateToInstance(year,
                        first, second));
            }
            
            return new DateMatcherUtil(true, true, true, dateToInstance(year,
                    first, second));
        }
        matcher = parsingDate(YEAR_MONTHS_DAY_MATCHER, input);
        if (matcher.find()) {
            int year = Integer.parseInt(matcher.group().substring(0, 4));
            int first = Integer.parseInt(matcher.group().substring(5, 7));
            int second = Integer.parseInt(matcher.group().substring(8));
            int control = first + second ;
            if(control < 2 || control > 12 + 31 ){
                return null ;
            }else if (control < 12 + 12 + 1){
                new DateMatcherUtil(true, true, true, dateToInstance(year,
                        first, second));
            }
            return new DateMatcherUtil(true, true, true, dateToInstance(year,
                    first, second));
        }

        return null;
    }

    protected static Calendar dateToInstance(int year, int month, int day) {

        Calendar retour = new GregorianCalendar(year,month,day);
        return retour;
    }

}