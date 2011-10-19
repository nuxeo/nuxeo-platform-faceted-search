package org.nuxeo.ecm.platform.faceted.search.utils;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.derby.tools.sysinfo;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.chrono.GregorianChronology;
import org.junit.Test;

public class TestDateMatcherUtil {

    @Test
    public void test() {
        DateMatcherUtil someThingElseThanDate = DateMatcherUtil.fromInput("");
        assertNull(someThingElseThanDate);
        DateMatcherUtil dateMonthsOnlyMoreThan12 = DateMatcherUtil.fromInput("13");
        assertNull(dateMonthsOnlyMoreThan12);
        DateMatcherUtil dateMonthsOnlylessThan1 = DateMatcherUtil.fromInput("0");
        assertNull(dateMonthsOnlylessThan1);   

        DateMatcherUtil dateYearWithMonthOnlylessThan1 = DateMatcherUtil.fromInput("1654-00");
        assertNull(dateYearWithMonthOnlylessThan1);   
        DateMatcherUtil dateYearWithMonthMoreThan12 = DateMatcherUtil.fromInput("1486_44");
        assertNull(dateYearWithMonthMoreThan12);
        
        DateMatcherUtil dateMonthsOnlylessThan1WithYear = DateMatcherUtil.fromInput("0 2111");
        assertNull(dateMonthsOnlylessThan1WithYear);   
        DateMatcherUtil dateMonthsOnlyMoreThan12WithYear = DateMatcherUtil.fromInput("23 2111");
        assertNull(dateMonthsOnlyMoreThan12WithYear);

        DateMatcherUtil dateMonthAndDayMoreToMuchAndYear = DateMatcherUtil.fromInput("12:32-2111");
        assertNull(dateMonthAndDayMoreToMuchAndYear);
        DateMatcherUtil dateMonthMoreToMuchDayAndYear = DateMatcherUtil.fromInput("13 31-2111");
        assertNull(dateMonthMoreToMuchDayAndYear);
        
        DateMatcherUtil  dateMonthDayMoreToMuchYear  = DateMatcherUtil.fromInput("12 32_2111");
        assertNull(dateMonthDayMoreToMuchYear);
        
        DateMatcherUtil  dateMonthAndDaynotEnoughYear  = DateMatcherUtil.fromInput("00 00_2111");
        assertNull(dateMonthAndDaynotEnoughYear);
        
        DateMatcherUtil  dateMonthDaynotEnoughYear  = DateMatcherUtil.fromInput("01 00_2111");
        assertNull(dateMonthDaynotEnoughYear);
        
        DateMatcherUtil  dateMonthNotEnoughDayYear  = DateMatcherUtil.fromInput("00 01_2111");
        assertNull(dateMonthNotEnoughDayYear);
        

        DateMatcherUtil dateYearMonthAndDayMoreToMuch = DateMatcherUtil.fromInput("2111 12:32");
        assertNull(dateYearMonthAndDayMoreToMuch);
        DateMatcherUtil dateYearMonthMoreToMuchDay = DateMatcherUtil.fromInput("2111 13 31");
        assertNull(dateYearMonthMoreToMuchDay);
        
        DateMatcherUtil  dateYearMonthDayMoreToMuch  = DateMatcherUtil.fromInput("2111 12 32");
        assertNull(dateYearMonthDayMoreToMuch);
        
        DateMatcherUtil  dateYearMonthAndDaynotEnough  = DateMatcherUtil.fromInput("2111 00 00");
        assertNull(dateYearMonthAndDaynotEnough);
        
        DateMatcherUtil  dateYearMonthDaynotEnough= DateMatcherUtil.fromInput("2111 01 00");
        assertNull(dateYearMonthDaynotEnough);
        
        DateMatcherUtil  dateYearMonthNotEnoughDay  = DateMatcherUtil.fromInput("2111 00 01");
        assertNull(dateYearMonthNotEnoughDay);

        DateMatcherUtil dateOnlyYear = DateMatcherUtil.fromInput("1980");
        assertNotNull(dateOnlyYear);
        assertTrue(dateOnlyYear.isWithYears());
        assertFalse(dateOnlyYear.isWithMonth());
        assertFalse(dateOnlyYear.isWitDay());
        assertNotNull(dateOnlyYear.getDateSuggestion());
        assertEquals(1980, dateOnlyYear.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(1, dateOnlyYear.getDateSuggestion().get(GregorianCalendar.MONTH));
        assertEquals(1, dateOnlyYear.getDateSuggestion().get(GregorianCalendar.DAY_OF_MONTH));
        
        DateMatcherUtil dateOnlyMonth = DateMatcherUtil.fromInput("10");
        assertNotNull(dateOnlyMonth);
        assertFalse(dateOnlyMonth.isWithYears());
        assertTrue(dateOnlyMonth.isWithMonth());
        assertFalse(dateOnlyMonth.isWitDay());
        assertNotNull(dateOnlyMonth.getDateSuggestion());
        assertEquals(2011, dateOnlyMonth.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(10, dateOnlyMonth.getDateSuggestion().get(GregorianCalendar.MONTH));
        assertEquals(1, dateOnlyMonth.getDateSuggestion().get(GregorianCalendar.DAY_OF_MONTH));

        DateMatcherUtil dateMonthDayYear = DateMatcherUtil.fromInput("02 28 2011");
        assertNotNull(dateMonthDayYear);
        assertTrue(dateMonthDayYear.isWithYears());
        assertTrue(dateMonthDayYear.isWithMonth());
        assertTrue(dateMonthDayYear.isWitDay());
        assertNotNull(dateMonthDayYear.getDateSuggestion());
        assertEquals(2011, dateMonthDayYear.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(2, dateMonthDayYear.getDateSuggestion().get(GregorianCalendar.MONTH));
        assertEquals(28, dateMonthDayYear.getDateSuggestion().get(GregorianCalendar.DAY_OF_MONTH));

        DateMatcherUtil dateMonthDayUnder12Year = DateMatcherUtil.fromInput("02 12 2011");
        assertNotNull(dateMonthDayUnder12Year);
        assertTrue(dateMonthDayUnder12Year.isWithYears());
        assertTrue(dateMonthDayUnder12Year.isWithMonth());
        assertTrue(dateMonthDayUnder12Year.isWitDay());
        assertNotNull(dateMonthDayUnder12Year.getDateSuggestion());
        assertEquals(2011, dateMonthDayUnder12Year.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(2, dateMonthDayUnder12Year.getDateSuggestion().get(GregorianCalendar.MONTH));
        assertEquals(12, dateMonthDayUnder12Year.getDateSuggestion().get(GregorianCalendar.DAY_OF_MONTH));
        
        DateMatcherUtil dateYearMonthDay = DateMatcherUtil.fromInput("2009 03 30");
        assertNotNull(dateYearMonthDay);
        assertTrue(dateYearMonthDay.isWithYears());
        assertTrue(dateYearMonthDay.isWithMonth());
        assertTrue(dateYearMonthDay.isWitDay());
        assertNotNull(dateYearMonthDay.getDateSuggestion());
        assertEquals(2009, dateYearMonthDay.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(3, dateYearMonthDay.getDateSuggestion().get(GregorianCalendar.MONTH));
        assertEquals(30, dateYearMonthDay.getDateSuggestion().get(GregorianCalendar.DAY_OF_MONTH));
    
    DateFormat datef = DateFormat.getDateInstance(DateFormat.FULL
            , Locale.GERMAN);
    System.out.println(datef.format(new Date()));
    Chronology chrono = GregorianChronology.getInstance();
    DateTime dt = new DateTime(2009, 02, 29, 12, 0, 0, 0, chrono);
    }

}
