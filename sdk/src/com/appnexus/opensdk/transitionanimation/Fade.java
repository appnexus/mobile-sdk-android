/*
 *    Copyright 2014 APPNEXUS INC
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

package com.appnexus.opensdk.transitionanimation;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;

public class Fade implements Transition {
    private Animation inAnimation;
    private Animation outAnimation;

    public Fade(long duration) {
        Interpolator interpolator = new AccelerateInterpolator();
        setInAnimation(interpolator, duration);
        setOutAnimation(interpolator, duration);
    }

    private void setInAnimation(Interpolator interpolator, long duration) {
        inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(duration);
        inAnimation.setInterpolator(interpolator);
    }

    private void setOutAnimation(Interpolator interpolator, long duration) {
        outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(duration);
        outAnimation.setInterpolator(interpolator);
    }

    @Override
    public Animation getInAnimation() {
        return inAnimation;
    }

    @Override
    public Animation getOutAnimation() {
        return outAnimation;
    }
}
