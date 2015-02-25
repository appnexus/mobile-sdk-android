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

/**
 * An object of this type is sent to the third-party SDK's {@link
 * MediatedAdView} object.  The third-party SDK uses this object
 * to retrieve local targeting extras that were sent to the AppNexus SDK.
 */
public class TargetingParameters {
    private String age = null;
    private ArrayList<Pair<String, String>> customKeywords = new ArrayList<Pair<String, String>>();
    private AdView.GENDER gender = AdView.GENDER.UNKNOWN;
    private Location location = null;

    TargetingParameters() {
        
    }
    TargetingParameters(String age, AdView.GENDER gender, ArrayList<Pair<String,String>> customKeywords, Location location){
        this.age = age;
        this.gender = gender;
        this.customKeywords = customKeywords;
        this.location = location;
    }

    /**
     * The current user's age, passed by to the ad request.  Note this string
     * may come in one of several formats: age, birth year, or age range.
     * The default value is null.
     *
     * @return The current user's age.
     */
    public String getAge() {
        return age;
    }

    /**
     * Local custom keywords added to the ad request.
     *
     * @return The current array list of key-value pairs of custom
     * keywords.
     */
    public ArrayList<Pair<String, String>> getCustomKeywords() {
        return customKeywords;
    }

    /**
     * The current user's gender, passed to the ad request. The
     * default value is UNKNOWN.
     *
     * @return The user's gender.
     */
    public AdView.GENDER getGender() {
        return gender;
    }

    /**
     * The current user's location, passed to the ad request.
     * The default value is null.
     *
     * @return The current user's location.
     */
    public Location getLocation() {
        return location;
    }

}
