package org.nuxeo.ecm.platform.faceted.search.utils;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;

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

    }

}
