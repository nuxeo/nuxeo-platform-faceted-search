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
        assertNull(someThingElseThanDate.getDateSuggestion());
        DateMatcherUtil dateMonthsOnlyMoreThan12 = DateMatcherUtil.fromInput("13");
        assertNull(dateMonthsOnlyMoreThan12.getDateSuggestion());
        DateMatcherUtil dateMonthsOnlylessThan1 = DateMatcherUtil.fromInput("0");
        assertNull(dateMonthsOnlylessThan1.getDateSuggestion());   

        DateMatcherUtil dateYearWithMonthOnlylessThan1 = DateMatcherUtil.fromInput("1654-00");
        assertNull(dateYearWithMonthOnlylessThan1.getDateSuggestion());   
        DateMatcherUtil dateYearWithMonthMoreThan12 = DateMatcherUtil.fromInput("1486_44");
        assertNull(dateYearWithMonthMoreThan12.getDateSuggestion());
        
        DateMatcherUtil dateMonthsOnlylessThan1WithYear = DateMatcherUtil.fromInput("0 2111");
        assertNull(dateMonthsOnlylessThan1WithYear.getDateSuggestion());   
        DateMatcherUtil dateMonthsOnlyMoreThan12WithYear = DateMatcherUtil.fromInput("23 2111");
        assertNull(dateMonthsOnlyMoreThan12WithYear.getDateSuggestion());

        DateMatcherUtil dateMonthAndDayMoreToMuchAndYear = DateMatcherUtil.fromInput("12:32-2111");
        assertNull(dateMonthAndDayMoreToMuchAndYear.getDateSuggestion());
        DateMatcherUtil dateMonthMoreToMuchDayAndYear = DateMatcherUtil.fromInput("13 31-2111");
        assertNull(dateMonthMoreToMuchDayAndYear.getDateSuggestion());
        
        DateMatcherUtil  dateMonthDayMoreToMuchYear  = DateMatcherUtil.fromInput("12 32_2111");
        assertNull(dateMonthDayMoreToMuchYear.getDateSuggestion());
        
        DateMatcherUtil  dateMonthAndDaynotEnoughYear  = DateMatcherUtil.fromInput("00 00_2111");
        assertNull(dateMonthAndDaynotEnoughYear.getDateSuggestion());
        
        DateMatcherUtil  dateMonthDaynotEnoughYear  = DateMatcherUtil.fromInput("01 00_2111");
        assertNull(dateMonthDaynotEnoughYear.getDateSuggestion());
        
        DateMatcherUtil  dateMonthNotEnoughDayYear  = DateMatcherUtil.fromInput("00 01_2111");
        assertNull(dateMonthNotEnoughDayYear.getDateSuggestion());
        

        DateMatcherUtil dateYearMonthAndDayMoreToMuch = DateMatcherUtil.fromInput("2111 12:32");
        assertNull(dateYearMonthAndDayMoreToMuch.getDateSuggestion());
        DateMatcherUtil dateYearMonthMoreToMuchDay = DateMatcherUtil.fromInput("2111 13 31");
        assertNull(dateYearMonthMoreToMuchDay.getDateSuggestion());
        
        DateMatcherUtil  dateYearMonthDayMoreToMuch  = DateMatcherUtil.fromInput("2111 12 32");
        assertNull(dateYearMonthDayMoreToMuch.getDateSuggestion());
        
        DateMatcherUtil  dateYearMonthAndDaynotEnough  = DateMatcherUtil.fromInput("2111 00 00");
        assertNull(dateYearMonthAndDaynotEnough.getDateSuggestion());
        
        DateMatcherUtil  dateYearMonthDaynotEnough= DateMatcherUtil.fromInput("2111 01 00");
        assertNull(dateYearMonthDaynotEnough.getDateSuggestion());
        
        DateMatcherUtil  dateYearMonthNotEnoughDay  = DateMatcherUtil.fromInput("2111 00 01");
        assertNull(dateYearMonthNotEnoughDay.getDateSuggestion());

        DateMatcherUtil dateOnlyYear = DateMatcherUtil.fromInput("1980");
        assertNotNull(dateOnlyYear);
        assertTrue(dateOnlyYear.isWithYears());
        assertFalse(dateOnlyYear.isWithMonth());
        assertFalse(dateOnlyYear.isWitDay());
        assertNotNull(dateOnlyYear.getDateSuggestion());
        assertEquals(1980, dateOnlyYear.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(0, dateOnlyYear.getDateSuggestion().get(GregorianCalendar.MONTH));
        assertEquals(1, dateOnlyYear.getDateSuggestion().get(GregorianCalendar.DAY_OF_MONTH));
        
        DateMatcherUtil dateOnlyMonth = DateMatcherUtil.fromInput("10");
        assertNotNull(dateOnlyMonth);
        assertFalse(dateOnlyMonth.isWithYears());
        assertTrue(dateOnlyMonth.isWithMonth());
        assertFalse(dateOnlyMonth.isWitDay());
        assertNotNull(dateOnlyMonth.getDateSuggestion());
        assertEquals(2011, dateOnlyMonth.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(9, dateOnlyMonth.getDateSuggestion().get(GregorianCalendar.MONTH));
        assertEquals(1, dateOnlyMonth.getDateSuggestion().get(GregorianCalendar.DAY_OF_MONTH));
        
        DateMatcherUtil impossibleDate = DateMatcherUtil.fromInput("02 29 2011");
        assertNotNull(impossibleDate);
        assertTrue(impossibleDate.isWithYears());
        assertTrue(impossibleDate.isWithMonth());
        assertTrue(impossibleDate.isWitDay());
        assertNull(impossibleDate.getDateSuggestion());
        
        DateMatcherUtil dateMonthDayYear = DateMatcherUtil.fromInput("02 28 2011");
        assertNotNull(dateMonthDayYear);
        assertTrue(dateMonthDayYear.isWithYears());
        assertTrue(dateMonthDayYear.isWithMonth());
        assertTrue(dateMonthDayYear.isWitDay());
        assertNotNull(dateMonthDayYear.getDateSuggestion());
        assertEquals(2011, dateMonthDayYear.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(1, dateMonthDayYear.getDateSuggestion().get(GregorianCalendar.MONTH));
        assertEquals(28, dateMonthDayYear.getDateSuggestion().get(GregorianCalendar.DAY_OF_MONTH));

        DateMatcherUtil dateMonthDayUnder12Year = DateMatcherUtil.fromInput("02 12 2011");
        assertNotNull(dateMonthDayUnder12Year);
        assertTrue(dateMonthDayUnder12Year.isWithYears());
        assertTrue(dateMonthDayUnder12Year.isWithMonth());
        assertTrue(dateMonthDayUnder12Year.isWitDay());
        assertNotNull(dateMonthDayUnder12Year.getDateSuggestion());
        assertEquals(2011, dateMonthDayUnder12Year.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(1, dateMonthDayUnder12Year.getDateSuggestion().get(GregorianCalendar.MONTH));
        assertEquals(12, dateMonthDayUnder12Year.getDateSuggestion().get(GregorianCalendar.DAY_OF_MONTH));
        
        DateMatcherUtil dateYearMonthDay = DateMatcherUtil.fromInput("2009 03 30");
        assertNotNull(dateYearMonthDay);
        assertTrue(dateYearMonthDay.isWithYears());
        assertTrue(dateYearMonthDay.isWithMonth());
        assertTrue(dateYearMonthDay.isWitDay());
        assertNotNull(dateYearMonthDay.getDateSuggestion());
        assertEquals(2009, dateYearMonthDay.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(2, dateYearMonthDay.getDateSuggestion().get(GregorianCalendar.MONTH));
        assertEquals(30, dateYearMonthDay.getDateSuggestion().get(GregorianCalendar.DAY_OF_MONTH));
    
    }

}
