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

import java.util.ArrayList;
import java.util.Collections;

public class AnimationFactory {
    public static Transition create(TransitionType type, long duration, TransitionDirection direction) {
        if (type == TransitionType.RANDOM) {
            ArrayList<TransitionType> randomType = new ArrayList<TransitionType>();
            Collections.addAll(randomType, TransitionType.values());
            randomType.remove(TransitionType.NONE);
            randomType.remove(TransitionType.RANDOM);
            Collections.shuffle(randomType);
            type = randomType.get(0);
        }

        switch (type) {
            case FADE:
                return new Fade(duration);
            case PUSH:
                return new Push(duration, direction);
            case MOVEIN:
                return new MoveIn(duration, direction);
            case REVEAL:
                return new Reveal(duration, direction);
        }

        return null;
    }
}
