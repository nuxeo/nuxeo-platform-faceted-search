package org.nuxeo.ecm.platform.faceted.search.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.GregorianCalendar;

import org.junit.Test;

public class TestDateMatcherUtil {

    @Test
    public void test() {
        DateMatcher someThingElseThanDate = DateMatcher.fromInput("");
        assertNull(someThingElseThanDate.getDateSuggestion());
        DateMatcher dateMonthsOnlyMoreThan12 = DateMatcher.fromInput("13");
        assertNull(dateMonthsOnlyMoreThan12.getDateSuggestion());
        DateMatcher dateMonthsOnlylessThan1 = DateMatcher.fromInput("0");
        assertNull(dateMonthsOnlylessThan1.getDateSuggestion());

        DateMatcher dateYearWithMonthOnlylessThan1 = DateMatcher.fromInput("1654-00");
        assertNull(dateYearWithMonthOnlylessThan1.getDateSuggestion());
        DateMatcher dateYearWithMonthMoreThan12 = DateMatcher.fromInput("1486_44");
        assertNull(dateYearWithMonthMoreThan12.getDateSuggestion());

        DateMatcher dateMonthsOnlylessThan1WithYear = DateMatcher.fromInput("0 2111");
        assertNull(dateMonthsOnlylessThan1WithYear.getDateSuggestion());
        DateMatcher dateMonthsOnlyMoreThan12WithYear = DateMatcher.fromInput("23 2111");
        assertNull(dateMonthsOnlyMoreThan12WithYear.getDateSuggestion());

        DateMatcher dateMonthAndDayMoreToMuchAndYear = DateMatcher.fromInput("12:32-2111");
        assertNull(dateMonthAndDayMoreToMuchAndYear.getDateSuggestion());
        DateMatcher dateMonthMoreToMuchDayAndYear = DateMatcher.fromInput("13 31-2111");
        assertNull(dateMonthMoreToMuchDayAndYear.getDateSuggestion());

        DateMatcher dateMonthDayMoreToMuchYear = DateMatcher.fromInput("12 32_2111");
        assertNull(dateMonthDayMoreToMuchYear.getDateSuggestion());

        DateMatcher dateMonthAndDaynotEnoughYear = DateMatcher.fromInput("00 00_2111");
        assertNull(dateMonthAndDaynotEnoughYear.getDateSuggestion());

        DateMatcher dateMonthDaynotEnoughYear = DateMatcher.fromInput("01 00_2111");
        assertNull(dateMonthDaynotEnoughYear.getDateSuggestion());

        DateMatcher dateMonthNotEnoughDayYear = DateMatcher.fromInput("00 01_2111");
        assertNull(dateMonthNotEnoughDayYear.getDateSuggestion());

        DateMatcher dateYearMonthAndDayMoreToMuch = DateMatcher.fromInput("2111 12:32");
        assertNull(dateYearMonthAndDayMoreToMuch.getDateSuggestion());
        DateMatcher dateYearMonthMoreToMuchDay = DateMatcher.fromInput("2111 13 31");
        assertNull(dateYearMonthMoreToMuchDay.getDateSuggestion());

        DateMatcher dateYearMonthDayMoreToMuch = DateMatcher.fromInput("2111 12 32");
        assertNull(dateYearMonthDayMoreToMuch.getDateSuggestion());

        DateMatcher dateYearMonthAndDaynotEnough = DateMatcher.fromInput("2111 00 00");
        assertNull(dateYearMonthAndDaynotEnough.getDateSuggestion());

        DateMatcher dateYearMonthDaynotEnough = DateMatcher.fromInput("2111 01 00");
        assertNull(dateYearMonthDaynotEnough.getDateSuggestion());

        DateMatcher dateYearMonthNotEnoughDay = DateMatcher.fromInput("2111 00 01");
        assertNull(dateYearMonthNotEnoughDay.getDateSuggestion());

        DateMatcher dateOnlyYear = DateMatcher.fromInput("1980");
        assertNotNull(dateOnlyYear);
        assertTrue(dateOnlyYear.isWithYears());
        assertFalse(dateOnlyYear.isWithMonth());
        assertFalse(dateOnlyYear.isWitDay());
        assertNotNull(dateOnlyYear.getDateSuggestion());
        assertEquals(1980,
                dateOnlyYear.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(0,
                dateOnlyYear.getDateSuggestion().get(GregorianCalendar.MONTH));
        assertEquals(
                1,
                dateOnlyYear.getDateSuggestion().get(
                        GregorianCalendar.DAY_OF_MONTH));

        DateMatcher dateOnlyMonth = DateMatcher.fromInput("10");
        assertNotNull(dateOnlyMonth);
        assertFalse(dateOnlyMonth.isWithYears());
        assertTrue(dateOnlyMonth.isWithMonth());
        assertFalse(dateOnlyMonth.isWitDay());
        assertNotNull(dateOnlyMonth.getDateSuggestion());
        assertEquals(2011,
                dateOnlyMonth.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(9,
                dateOnlyMonth.getDateSuggestion().get(GregorianCalendar.MONTH));
        assertEquals(
                1,
                dateOnlyMonth.getDateSuggestion().get(
                        GregorianCalendar.DAY_OF_MONTH));

        DateMatcher impossibleDate = DateMatcher.fromInput("02 29 2011");
        assertNotNull(impossibleDate);
        assertTrue(impossibleDate.isWithYears());
        assertTrue(impossibleDate.isWithMonth());
        assertTrue(impossibleDate.isWitDay());
        assertNull(impossibleDate.getDateSuggestion());

        DateMatcher dateMonthDayYear = DateMatcher.fromInput("02 28 2011");
        assertNotNull(dateMonthDayYear);
        assertTrue(dateMonthDayYear.isWithYears());
        assertTrue(dateMonthDayYear.isWithMonth());
        assertTrue(dateMonthDayYear.isWitDay());
        assertNotNull(dateMonthDayYear.getDateSuggestion());
        assertEquals(
                2011,
                dateMonthDayYear.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(
                1,
                dateMonthDayYear.getDateSuggestion().get(
                        GregorianCalendar.MONTH));
        assertEquals(
                28,
                dateMonthDayYear.getDateSuggestion().get(
                        GregorianCalendar.DAY_OF_MONTH));

        DateMatcher dateMonthDayUnder12Year = DateMatcher.fromInput("02 12 2011");
        assertNotNull(dateMonthDayUnder12Year);
        assertTrue(dateMonthDayUnder12Year.isWithYears());
        assertTrue(dateMonthDayUnder12Year.isWithMonth());
        assertTrue(dateMonthDayUnder12Year.isWitDay());
        assertNotNull(dateMonthDayUnder12Year.getDateSuggestion());
        assertEquals(
                2011,
                dateMonthDayUnder12Year.getDateSuggestion().get(
                        GregorianCalendar.YEAR));
        assertEquals(
                1,
                dateMonthDayUnder12Year.getDateSuggestion().get(
                        GregorianCalendar.MONTH));
        assertEquals(
                12,
                dateMonthDayUnder12Year.getDateSuggestion().get(
                        GregorianCalendar.DAY_OF_MONTH));

        DateMatcher dateYearMonthDay = DateMatcher.fromInput("2009 03 30");
        assertNotNull(dateYearMonthDay);
        assertTrue(dateYearMonthDay.isWithYears());
        assertTrue(dateYearMonthDay.isWithMonth());
        assertTrue(dateYearMonthDay.isWitDay());
        assertNotNull(dateYearMonthDay.getDateSuggestion());
        assertEquals(
                2009,
                dateYearMonthDay.getDateSuggestion().get(GregorianCalendar.YEAR));
        assertEquals(
                2,
                dateYearMonthDay.getDateSuggestion().get(
                        GregorianCalendar.MONTH));
        assertEquals(
                30,
                dateYearMonthDay.getDateSuggestion().get(
                        GregorianCalendar.DAY_OF_MONTH));

    }

}
