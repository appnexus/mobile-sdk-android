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
package com.appnexus.opensdk;

import android.location.Location;
import android.util.Pair;

import java.util.ArrayList;

public class TargetingParameters {
    private String age = null;
    private ArrayList<Pair<String, String>> customKeywords = new ArrayList<Pair<String, String>>();
    private AdView.GENDER gender = AdView.GENDER.UNKNOWN;
    private Location location = null;

    public TargetingParameters() {
        
    }
    public TargetingParameters(String age, AdView.GENDER gender, ArrayList<Pair<String,String>> customKeywords, Location location){
        this.age = age;
        this.gender = gender;
        this.customKeywords = customKeywords;
        this.location = location;
    };

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public ArrayList<Pair<String, String>> getCustomKeywords() {
        return customKeywords;
    }

    public void setCustomKeywords(ArrayList<Pair<String, String>> customKeywords) {
        this.customKeywords = customKeywords;
    }

    public AdView.GENDER getGender() {
        return gender;
    }

    public void setGender(AdView.GENDER gender) {
        this.gender = gender;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
