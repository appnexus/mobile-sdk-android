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
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

public class Push implements Transition {
    private Animation inAnimation;
    private Animation outAnimation;

    private static final float in_up_coordinates[] = new float[]{0.0f, 0.0f, +1.0f, 0.0f};
    private static final float in_down_coordinates[] = new float[]{0.0f, 0.0f, -1.0f, 0.0f};
    private static final float in_right_coordinates[] = new float[]{-1.0f, 0.0f, 0.0f, 0.0f};
    private static final float in_left_coordinates[] = new float[]{+1.0f, 0.0f, 0.0f, 0.0f};

    private static final float out_up_coordinates[] = new float[]{0.0f, 0.0f, 0.0f, -1.0f};
    private static final float out_down_coordinates[] = new float[]{0.0f, 0.0f, 0.0f, +1.0f};
    private static final float out_right_coordinates[] = new float[]{0.0f, +1.0f, 0.0f, 0.0f};
    private static final float out_left_coordinates[] = new float[]{0.0f, -1.0f, 0.0f, 0.0f};

    public Push(long duration, TransitionDirection direction){
        Interpolator interpolator = new AccelerateInterpolator();
        inAnimation = getAnimation(getInDirection(direction), interpolator, duration);
        outAnimation = getAnimation(getOutDirection(direction), interpolator, duration);

    }

    private float[] getInDirection (TransitionDirection direction) {
        switch (direction) {
            case UP:
                return Push.in_up_coordinates;
            case DOWN:
                return Push.in_down_coordinates;
            case RIGHT:
                return Push.in_right_coordinates;
            case LEFT:
                return Push.in_left_coordinates;
        }
        return Push.in_up_coordinates;
    }

    private float[] getOutDirection (TransitionDirection direction) {
        switch (direction) {
            case UP:
                return Push.out_up_coordinates;
            case DOWN:
                return Push.out_down_coordinates;
            case RIGHT:
                return Push.out_right_coordinates;
            case LEFT:
                return Push.out_left_coordinates;
        }
        return Push.out_up_coordinates;
    }

    private Animation getAnimation(float[] direction, Interpolator interpolator, long duration){
        Animation animation = new TranslateAnimation(
                        Animation.RELATIVE_TO_PARENT, direction[0], Animation.RELATIVE_TO_PARENT, direction[1],
                        Animation.RELATIVE_TO_PARENT, direction[2], Animation.RELATIVE_TO_PARENT, direction[3]
                );
        animation.setInterpolator(interpolator);
        animation.setDuration(duration);
        return animation;
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
