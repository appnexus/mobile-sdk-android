/*
 *    Copyright 2015 APPNEXUS INC
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

/**
 * A convenience class which holds a width and height in integers.
 */
public class AdSize {
    private final int w;
    private final int h;

    public AdSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    /**
     * @return The width, in pixels.
     */
    public int width() {
        return w;
    }

    /**
     * @return The height, in pixels.
     */
    public int height() {
        return h;
    }

    /**
     * Determines whether this size object fits inside a rectangle of the
     * given width and height
     *
     * @param width  The width to check against.
     * @param height The height to check against.
     * @return True, if the size fits inside the described rectangle,
     * otherwise, false.
     */
    public boolean fitsIn(int width, int height) {
        return h < height && w < width;
    }

}
