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

import java.util.HashMap;

public class MediatedAd {
    private String className;
    private String param;
    private int width;
    private int height;
    private String id;
    private String resultCB;
    private HashMap<String, Object> extras = new HashMap<String, Object>();

    public MediatedAd(String className, String param, int width, int height, String id, String resultCB) {
        this.className = className;
        this.param = param;
        this.width = width;
        this.height = height;
        this.id = id;
        this.resultCB = resultCB;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultCB() {
        return resultCB;
    }

    public void setResultCB(String resultCB) {
        this.resultCB = resultCB;
    }

    public HashMap<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(HashMap<String, Object> extras) {
        this.extras = extras;
    }
}
