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
    String id;
    String decription;
    String location;//?
    String summary;//?
    String start;
    String end;//?
    String status;//?
    String transparency;//?
    String reminder;//?
    RepeatRule recurrence;

    static class RepeatRule{
        //Repeat rule?
        String frequency;//?
        int interval;//?
        String expires;//?
        String[] exceptionDates;
        int[] daysInWeek;
        int[] daysInMonth;
        int[] daysInYear;
        int[] weeksInMonth;
        int[] monthsInYear;

        private void RepeatRule(){

        }
    }

    public static W3CEvent createFromJSON(String s){
        W3CEvent out = new W3CEvent();
        try {
            JSONObject eventj = new JSONObject(s);
            if(!eventj.isNull("id")){
                out.id = eventj.getString("id");
            }
            if(!eventj.isNull("description")){
                out.decription = eventj.getString("description");
            }
            if(!eventj.isNull("location")){
                out.location = eventj.getString("location");
            }
            if(!eventj.isNull("summary")){
                out.summary = eventj.getString("summary");
            }
            if(!eventj.isNull("start")){
                out.start = eventj.getString("start");
            }
            if(!eventj.isNull("end")){
                out.end = eventj.getString("end");
            }
            if(!eventj.isNull("status")){
                out.status = eventj.getString("status");
            }
            if(!eventj.isNull("freebusy")){
                out.transparency = eventj.getString("freebusy"); //wai, w3, wai
            }
            if(!eventj.isNull("reminder")){
                out.reminder = eventj.getString("reminder");
            }

            //Parse the recurrence event
            if(!eventj.isNull("recurrence")){
                out.recurrence = new RepeatRule();
                try{
                    JSONObject recurrencej = eventj.getJSONObject("recurrence");
                    if(!recurrencej.isNull("frequency")){
                        out.recurrence.frequency = recurrencej.getString("frequency");
                    }
                    if(!recurrencej.isNull("interval")){
                        out.recurrence.interval = recurrencej.getInt("interval");
                    }
                    if(!recurrencej.isNull("expires")){
                        out.recurrence.expires = recurrencej.getString("expires");
                    }
                    if(!recurrencej.isNull("exceptionDates")){
                        JSONArray exceptionDatesj = recurrencej.getJSONArray("exceptionDates");
                        int len = exceptionDatesj.length();
                        out.recurrence.exceptionDates = new String[len];
                        for(int i = 0; i<len; i++){
                            out.recurrence.exceptionDates[i]=exceptionDatesj.getString(i);
                        }
                    }
                    if(!recurrencej.isNull("daysInWeek")){
                        JSONArray daysInWeekj = recurrencej.getJSONArray("daysInWeek");
                        int len = daysInWeekj.length();
                        out.recurrence.daysInWeek = new int[len];
                        for(int i = 0; i<len; i++){
                            out.recurrence.daysInWeek[i]=daysInWeekj.getInt(i);
                        }
                    }
                    if(!recurrencej.isNull("daysInMonth")){
                        JSONArray daysInMonthj = recurrencej.getJSONArray("daysInMonth");
                        int len = daysInMonthj.length();
                        out.recurrence.daysInMonth = new int[len];
                        for(int i = 0; i<len; i++){
                            out.recurrence.daysInMonth[i]=daysInMonthj.getInt(i);
                        }
                    }
                    if(!recurrencej.isNull("daysInYear")){
                        JSONArray daysInYearj = recurrencej.getJSONArray("daysInYear");
                        int len = daysInYearj.length();
                        out.recurrence.daysInYear = new int[len];
                        for(int i = 0; i<len; i++){
                            out.recurrence.daysInYear[i]=daysInYearj.getInt(i);
                        }
                    }
                    if(!recurrencej.isNull("weeksInMonth")){
                        JSONArray weeksInMonthj = recurrencej.getJSONArray("weeksInMonth");
                        int len = weeksInMonthj.length();
                        out.recurrence.weeksInMonth = new int[len];
                        for(int i = 0; i<len; i++){
                            out.recurrence.weeksInMonth[i]=weeksInMonthj.getInt(i);
                        }
                    }
                    if(!recurrencej.isNull("monthsInYear")){
                        JSONArray monthsInYearj = recurrencej.getJSONArray("monthsInYear");
                        int len = monthsInYearj.length();
                        out.recurrence.monthsInYear = new int[len];
                        for(int i = 0; i<len; i++){
                            out.recurrence.monthsInYear[i]=monthsInYearj.getInt(i);
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