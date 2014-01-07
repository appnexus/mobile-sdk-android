package com.appnexus.opensdk.utils;

//See http://www.w3.org/TR/calendar-api/#idl-def-CalendarRepeatRule
//Question marks denote optional parameters.
public class W3CRepeatRule {
    //Repeat rule?
    private String frequency;//?
    private int interval;//?
    private String expires;//?
    private String[] exceptionDates;
    private int[] daysInWeek;
    private int[] daysInMonth;
    private int[] daysInYear;
    private int[] weeksInMonth;
    private int[] monthsInYear;

    private void W3CRepeatRule() {

    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String[] getExceptionDates() {
        return exceptionDates;
    }

    public void setExceptionDates(String[] exceptionDates) {
        this.exceptionDates = exceptionDates;
    }

    public int[] getDaysInWeek() {
        return daysInWeek;
    }

    public void setDaysInWeek(int[] daysInWeek) {
        this.daysInWeek = daysInWeek;
    }

    public int[] getDaysInMonth() {
        return daysInMonth;
    }

    public void setDaysInMonth(int[] daysInMonth) {
        this.daysInMonth = daysInMonth;
    }

    public int[] getDaysInYear() {
        return daysInYear;
    }

    public void setDaysInYear(int[] daysInYear) {
        this.daysInYear = daysInYear;
    }

    public int[] getMonthsInYear() {
        return monthsInYear;
    }

    public void setMonthsInYear(int[] monthsInYear) {
        this.monthsInYear = monthsInYear;
    }

    public int[] getWeeksInMonth() {
        return weeksInMonth;
    }

    public void setWeeksInMonth(int[] weeksInMonth) {
        this.weeksInMonth = weeksInMonth;
    }
}