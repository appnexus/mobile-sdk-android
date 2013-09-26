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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class W3CEvent {
    private String id;
    private String decription;
    private String location;//?
    private String summary;//?
    private String start;
    private String end;//?
    private String status;//?
    private String transparency;//?
    private String reminder;//?
    private W3CRepeatRule recurrence;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
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

    public static W3CEvent createFromJSON(String s){
        W3CEvent out = new W3CEvent();
        try {
            JSONObject eventj = new JSONObject(s);
            if(!eventj.isNull("id")){
                out.setId(eventj.getString("id"));
            }
            if(!eventj.isNull("description")){
                out.setDecription(eventj.getString("description"));
            }
            if(!eventj.isNull("location")){
                out.setLocation(eventj.getString("location"));
            }
            if(!eventj.isNull("summary")){
                out.setSummary(eventj.getString("summary"));
            }
            if(!eventj.isNull("start")){
                out.setStart(eventj.getString("start"));
            }
            if(!eventj.isNull("end")){
                out.setEnd(eventj.getString("end"));
            }
            if(!eventj.isNull("status")){
                out.setStatus(eventj.getString("status"));
            }
            if(!eventj.isNull("freebusy")){
                out.setTransparency(eventj.getString("freebusy")); //wai, w3, wai
            }
            if(!eventj.isNull("reminder")){
                out.setReminder(eventj.getString("reminder"));
            }

            //Parse the recurrence event
            if(!eventj.isNull("recurrence")){
                out.setRecurrence(new W3CRepeatRule());
                try{
                    JSONObject recurrencej = eventj.getJSONObject("recurrence");
                    if(!recurrencej.isNull("frequency")){
                        out.getRecurrence().setFrequency(recurrencej.getString("frequency"));
                    }
                    if(!recurrencej.isNull("interval")){
                        out.getRecurrence().setInterval(recurrencej.getInt("interval"));
                    }
                    if(!recurrencej.isNull("expires")){
                        out.getRecurrence().setExpires(recurrencej.getString("expires"));
                    }
                    if(!recurrencej.isNull("exceptionDates")){
                        JSONArray exceptionDatesj = recurrencej.getJSONArray("exceptionDates");
                        int len = exceptionDatesj.length();
                        out.getRecurrence().setExceptionDates(new String[len]);
                        for(int i = 0; i<len; i++){
                            out.getRecurrence().getExceptionDates()[i]=exceptionDatesj.getString(i);
                        }
                    }
                    if(!recurrencej.isNull("daysInWeek")){
                        JSONArray daysInWeekj = recurrencej.getJSONArray("daysInWeek");
                        int len = daysInWeekj.length();
                        out.getRecurrence().setDaysInWeek(new int[len]);
                        for(int i = 0; i<len; i++){
                            out.getRecurrence().getDaysInWeek()[i]=daysInWeekj.getInt(i);
                        }
                    }
                    if(!recurrencej.isNull("daysInMonth")){
                        JSONArray daysInMonthj = recurrencej.getJSONArray("daysInMonth");
                        int len = daysInMonthj.length();
                        out.getRecurrence().setDaysInMonth(new int[len]);
                        for(int i = 0; i<len; i++){
                            out.getRecurrence().getDaysInMonth()[i]=daysInMonthj.getInt(i);
                        }
                    }
                    if(!recurrencej.isNull("daysInYear")){
                        JSONArray daysInYearj = recurrencej.getJSONArray("daysInYear");
                        int len = daysInYearj.length();
                        out.getRecurrence().setDaysInYear(new int[len]);
                        for(int i = 0; i<len; i++){
                            out.getRecurrence().getDaysInYear()[i]=daysInYearj.getInt(i);
                        }
                    }
                    if(!recurrencej.isNull("weeksInMonth")){
                        JSONArray weeksInMonthj = recurrencej.getJSONArray("weeksInMonth");
                        int len = weeksInMonthj.length();
                        out.getRecurrence().weeksInMonth = new int[len];
                        for(int i = 0; i<len; i++){
                            out.getRecurrence().weeksInMonth[i]=weeksInMonthj.getInt(i);
                        }
                    }
                    if(!recurrencej.isNull("monthsInYear")){
                        JSONArray monthsInYearj = recurrencej.getJSONArray("monthsInYear");
                        int len = monthsInYearj.length();
                        out.getRecurrence().setMonthsInYear(new int[len]);
                        for(int i = 0; i<len; i++){
                            out.getRecurrence().getMonthsInYear()[i]=monthsInYearj.getInt(i);
                        }
                    }
                }catch(JSONException e){
                    // TODO: Clogging - error because of bad json in recurrence
                }
            }

        } catch (JSONException e) {
            //TODO: Clogging - error because of bad json
        }
        return out;
    }
    private W3CEvent(){

    }
}