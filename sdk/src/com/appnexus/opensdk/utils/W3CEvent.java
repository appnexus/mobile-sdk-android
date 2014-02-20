/*
 *    Copyright 2013 APPNEXUS INC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.appnexus.opensdk.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.provider.CalendarContract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

//See http://www.w3.org/TR/calendar-api/#calendarevent-interface
//Question marks denote optional parameters.
public class W3CEvent {
    private String id;
    private String description;
    private String location;//?
    private String summary;//?
    private String start;
    private String end;//?
    private String status;//?
    private String transparency;//?
    private String reminder;//?
    private W3CRepeatRule recurrence;
    public static boolean useMIME=false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransparency() {
        return transparency;
    }

    public void setTransparency(String transparency) {
        this.transparency = transparency;
    }

    public String getReminder() {
        return reminder;
    }

    public void setReminder(String reminder) {
        this.reminder = reminder;
    }

    public W3CRepeatRule getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(W3CRepeatRule recurrence) {
        this.recurrence = recurrence;
    }

    public static W3CEvent createFromJSON(String s) {
        W3CEvent out = new W3CEvent();
        try {
            JSONObject eventj = new JSONObject(s);
            if (!eventj.isNull("id")) {
                out.setId(eventj.getString("id"));
            }
            if (!eventj.isNull("description")) {
                out.setDescription(eventj.getString("description"));
            }
            if (!eventj.isNull("location")) {
                out.setLocation(eventj.getString("location"));
            }
            if (!eventj.isNull("summary")) {
                out.setSummary(eventj.getString("summary"));
            }
            if (!eventj.isNull("start")) {
                out.setStart(eventj.getString("start"));
            }
            if (!eventj.isNull("end")) {
                if (eventj.isNull(("start"))) {
                    out.setStart(eventj.getString("end"));
                }
                out.setEnd(eventj.getString("end"));
            }
            if (!eventj.isNull("status")) {
                out.setStatus(eventj.getString("status"));
            }
            if (!eventj.isNull("freebusy")) {
                out.setTransparency(eventj.getString("freebusy")); //wai, w3, wai
            }
            if (!eventj.isNull("reminder")) {
                out.setReminder(eventj.getString("reminder"));
            }

            //Parse the recurrence event
            if (!eventj.isNull("recurrence")) {
                out.setRecurrence(new W3CRepeatRule());
                try {
                    JSONObject recurrencej = eventj.getJSONObject("recurrence");
                    if (!recurrencej.isNull("frequency")) {
                        out.getRecurrence().setFrequency(recurrencej.getString("frequency"));
                    }
                    if (!recurrencej.isNull("interval")) {
                        out.getRecurrence().setInterval(recurrencej.getInt("interval"));
                    }
                    if (!recurrencej.isNull("expires")) {
                        out.getRecurrence().setExpires(recurrencej.getString("expires"));
                    }
                    if (!recurrencej.isNull("exceptionDates")) {
                        JSONArray exceptionDatesj = recurrencej.getJSONArray("exceptionDates");
                        int len = exceptionDatesj.length();
                        out.getRecurrence().setExceptionDates(new String[len]);
                        for (int i = 0; i < len; i++) {
                            out.getRecurrence().getExceptionDates()[i] = exceptionDatesj.getString(i);
                        }
                    }
                    if (!recurrencej.isNull("daysInWeek")) {
                        JSONArray daysInWeekj = recurrencej.getJSONArray("daysInWeek");
                        int len = daysInWeekj.length();
                        out.getRecurrence().setDaysInWeek(new int[len]);
                        for (int i = 0; i < len; i++) {
                            out.getRecurrence().getDaysInWeek()[i] = daysInWeekj.getInt(i);
                        }
                    }
                    if (!recurrencej.isNull("daysInMonth")) {
                        JSONArray daysInMonthj = recurrencej.getJSONArray("daysInMonth");
                        int len = daysInMonthj.length();
                        out.getRecurrence().setDaysInMonth(new int[len]);
                        for (int i = 0; i < len; i++) {
                            out.getRecurrence().getDaysInMonth()[i] = daysInMonthj.getInt(i);
                        }
                    }
                    if (!recurrencej.isNull("daysInYear")) {
                        JSONArray daysInYearj = recurrencej.getJSONArray("daysInYear");
                        int len = daysInYearj.length();
                        out.getRecurrence().setDaysInYear(new int[len]);
                        for (int i = 0; i < len; i++) {
                            out.getRecurrence().getDaysInYear()[i] = daysInYearj.getInt(i);
                        }
                    }
                    if (!recurrencej.isNull("weeksInMonth")) {
                        JSONArray weeksInMonthj = recurrencej.getJSONArray("weeksInMonth");
                        int len = weeksInMonthj.length();
                        out.getRecurrence().setWeeksInMonth(new int[len]);
                        for (int i = 0; i < len; i++) {
                            out.getRecurrence().getWeeksInMonth()[i] = weeksInMonthj.getInt(i);
                        }
                    }
                    if (!recurrencej.isNull("monthsInYear")) {
                        JSONArray monthsInYearj = recurrencej.getJSONArray("monthsInYear");
                        int len = monthsInYearj.length();
                        out.getRecurrence().setMonthsInYear(new int[len]);
                        for (int i = 0; i < len; i++) {
                            out.getRecurrence().getMonthsInYear()[i] = monthsInYearj.getInt(i);
                        }
                    }
                } catch (JSONException e) {
                    // TODO: Clogging - error because of bad json in recurrence
                }
            }

        } catch (JSONException e) {
            //TODO: Clogging - error because of bad json
        }
        return out;
    }

    private W3CEvent() {

    }
    //Constants for Event frequency
    static final String W3C_DAILY = "daily";
    static final String W3C_WEEKLY = "weekly";
    static final String W3C_MONTHLY = "monthly";
    static final String W3C_YEARLY = "yearly";
    
    @SuppressLint({ "NewApi", "InlinedApi" })
    public Intent getInsertIntent() {
        Intent i;
        boolean nativeMethod = (!useMIME && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH);
        if (nativeMethod) {
            i = new Intent(Intent.ACTION_EDIT).setData(CalendarContract.Events.CONTENT_URI);
        } else {
            i = new Intent(Intent.ACTION_EDIT).setType("vnd.android.cursor.item/event");
        }
        if (!StringUtil.isEmpty(getDescription())) {
            if (nativeMethod) {
                i.putExtra(CalendarContract.Events.TITLE, getDescription());
            } else {
                i.putExtra("title", getDescription());
            }
        }
        if (!StringUtil.isEmpty(getLocation())) {
            if (nativeMethod) {
                i.putExtra(CalendarContract.Events.EVENT_LOCATION, getLocation());
            } else {
                i.putExtra("eventLocation", getLocation());
            }
        }
        if (!StringUtil.isEmpty(getSummary())) {
            if (nativeMethod) {
                i.putExtra(CalendarContract.Events.DESCRIPTION, getSummary());
            } else {
                i.putExtra("description", getSummary());
            }
        }
        if (!StringUtil.isEmpty(getStart())) {
            long start = -1;
                start = millisFromDateString(getStart());
            if(start>0){
                if (nativeMethod) {
                    i.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start);
                } else {
                    i.putExtra("beginTime", start);
                }
            }
        }
        if (!StringUtil.isEmpty(getEnd())) {
            long end = -1;
            end = millisFromDateString(getEnd());
            if(end>0){
                if (nativeMethod) {
                    i.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
                } else {
                    i.putExtra("endTime", end);
                }
            }
        }
        if (!StringUtil.isEmpty(getStatus())) {
            if (nativeMethod) {
                i.putExtra(CalendarContract.Events.STATUS, getStatus());
            }
        }
        if (!StringUtil.isEmpty(getTransparency())) {
            if (nativeMethod) {
                i.putExtra(CalendarContract.Events.VISIBLE, !getTransparency().equals("opaque"));
            }
        }
        if (!StringUtil.isEmpty(getReminder())) {
            long time = millisFromDateString(getReminder());
            if(time<0){
                if (nativeMethod) {
                    i.putExtra(CalendarContract.Reminders.MINUTES, Math.abs(time/60000));
                }
            }else if(!StringUtil.isEmpty(getStart())){
                if (nativeMethod) {
                    long tstart = millisFromDateString(getStart());
                    if (tstart > 0) {
                        i.putExtra(CalendarContract.Reminders.MINUTES, Math.abs((tstart- time)/60000));
                    }
                }
            }
        }

        StringBuilder repeatRuleBuilder = new StringBuilder("");
        if (getRecurrence() != null) {
           
            String freq = getRecurrence().getFrequency();
            if (!StringUtil.isEmpty(freq)) {
                if (W3C_DAILY.equals(freq)) {
                    repeatRuleBuilder.append("FREQ=DAILY;");
                } else if (W3C_WEEKLY.equals(freq)) {
                    repeatRuleBuilder.append("FREQ=WEEKLY;");   
                } else if (W3C_MONTHLY.equals(freq)) {
                    repeatRuleBuilder.append("FREQ=MONTHLY;");                    
                } else if (W3C_YEARLY.equals(freq)) {
                    repeatRuleBuilder.append("FREQ=YEARLY;");
                } else {
                    freq = "";
                }
            } else {
                freq = "";
            }
            if (getRecurrence().getInterval() > 0) {
                repeatRuleBuilder.append("INTERVAL=");
                repeatRuleBuilder.append(getRecurrence().getInterval());
                repeatRuleBuilder.append(";");
            }
            if (W3C_WEEKLY.equals(freq) && getRecurrence().getDaysInWeek()!=null && 
                    getRecurrence().getDaysInWeek().length > 0) {
                repeatRuleBuilder.append("BYDAY=");
                for (int j : getRecurrence().getDaysInWeek()) {
                    switch (j) {
                        case 0:
                            repeatRuleBuilder.append("SU,");
                            break;
                        case 1:
                            repeatRuleBuilder.append("MO,");
                            break;
                        case 2:
                            repeatRuleBuilder.append("TU,");
                            break;
                        case 3:
                            repeatRuleBuilder.append("WE,");
                            break;
                        case 4:
                            repeatRuleBuilder.append("TH,");
                            break;
                        case 5:
                            repeatRuleBuilder.append("FR,");
                            break;
                        case 6:
                            repeatRuleBuilder.append("SA,");
                            break;
                    }
                }
                repeatRuleBuilder.setCharAt(repeatRuleBuilder.length()-1, ';');
            }
            if (W3C_MONTHLY.equals(freq) && getRecurrence().getDaysInMonth()!=null&&getRecurrence().getDaysInMonth().length > 0) {
                repeatRuleBuilder.append("BYMONTHDAY=");
                for (int j : getRecurrence().getDaysInMonth()) {
                    repeatRuleBuilder.append(j);
                    repeatRuleBuilder.append(",");
                }
                repeatRuleBuilder.setCharAt(repeatRuleBuilder.length()-1, ';');
            }
            if (W3C_YEARLY.equals(freq) && getRecurrence().getDaysInYear()!=null&&getRecurrence().getDaysInYear().length > 0) {
                repeatRuleBuilder.append("BYYEARDAY=");
                for (int j : getRecurrence().getDaysInYear()) {
                    repeatRuleBuilder.append(j);
                    repeatRuleBuilder.append(",");
                }
                repeatRuleBuilder.setCharAt(repeatRuleBuilder.length()-1, ';');
            }
            if (W3C_YEARLY.equals(freq) && getRecurrence().getMonthsInYear()!=null&&getRecurrence().getMonthsInYear().length > 0) {
                repeatRuleBuilder.append("BYMONTH=");
                for (int j : getRecurrence().getMonthsInYear()) {
                    repeatRuleBuilder.append(j);
                    repeatRuleBuilder.append(",");
                }
                repeatRuleBuilder.setCharAt(repeatRuleBuilder.length()-1, ';');
            }
            if (W3C_MONTHLY.equals(freq) && getRecurrence().getWeeksInMonth()!=null&&getRecurrence().getWeeksInMonth().length > 0) {
                repeatRuleBuilder.append("BYWEEKNO=");
                for (int j : getRecurrence().getWeeksInMonth()) {
                    repeatRuleBuilder.append(j);
                    repeatRuleBuilder.append(",");
                }
                repeatRuleBuilder.setCharAt(repeatRuleBuilder.length()-1, ';');
            }
            if (!StringUtil.isEmpty(getRecurrence().getExpires())) {
                repeatRuleBuilder.append("UNTIL=");
                repeatRuleBuilder.append(getRecurrence().getExpires());
                repeatRuleBuilder.append(";");
            }
            if (getRecurrence().getExceptionDates()!=null&&getRecurrence().getExceptionDates().length > 0) {
                repeatRuleBuilder.append("EXDATE=");
                for (String s : getRecurrence().getExceptionDates()) {
                    repeatRuleBuilder.append(s);
                    repeatRuleBuilder.append(",");
                }
                repeatRuleBuilder.setCharAt(repeatRuleBuilder.length()-1, ';');
            }
            if (nativeMethod) {
                i.putExtra(CalendarContract.Events.RRULE, repeatRuleBuilder.toString());
            } else {
                i.putExtra("rrule", repeatRuleBuilder.toString());
            }
        }

        return i;

    }

    private static SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ",Locale.US);
    private static SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZZZZZ",Locale.US);
    private long millisFromDateString(String date){
        try {
            return format1.parse(date).getTime();
        } catch (ParseException e) {
            try {
                return format2.parse(date).getTime();
            } catch (ParseException e1) {
                try{
                    return Long.parseLong(date);
                }catch (NumberFormatException e2){
                    return -1;
                }
            }
        }
    }
}