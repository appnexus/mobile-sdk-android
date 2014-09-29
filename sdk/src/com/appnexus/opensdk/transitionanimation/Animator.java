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

import android.content.Context;
import android.widget.ViewAnimator;


public class Animator extends ViewAnimator {
    private Transition animation = null;
    private TransitionType type;
    private TransitionDirection direction;
    private long duration;

    public Animator(Context context, TransitionType type, TransitionDirection direction, long duration) {
        super(context);
        this.type = type;
        this.direction = direction;
        this.duration = duration;
    }

    public void setAnimation() {
        if (animation != null) {
            this.setInAnimation(animation.getInAnimation());
            this.setOutAnimation(animation.getOutAnimation());
        }
    }

    public void clearAnimation() {
        this.setInAnimation(null);
        this.setOutAnimation(null);
    }

    public void setTransitionType(TransitionType type) {
        if (this.type != type) {
            this.type = type;
            animation = AnimationFactory.create(type, duration, direction);
            setAnimation();
        }
    }

    public TransitionType getTransitionType() {
        return this.type;
    }

    public void setTransitionDirection(TransitionDirection direction) {
        if (this.direction != direction) {
            this.direction = direction;
            animation = AnimationFactory.create(type, duration, direction);
            setAnimation();
        }
    }

    public TransitionDirection getTransitionDirection() {
        return this.direction;
    }

    public void setTransitionDuration(long duration) {
        if (this.duration != duration) {
            this.duration = duration;
            animation = AnimationFactory.create(type, duration, direction);
            setAnimation();
        }
    }

    public long getTransitionDuration() {
        return this.duration;
    }
}
