package com.appnexus.opensdk.transitionanimation;

import android.widget.ViewAnimator;

import java.util.ArrayList;
import java.util.Collections;

public class AnimationFactory {
    public static void create(ViewAnimator animator, TransitionType type, long duration, TransitionDirection direction) {
        if (type == TransitionType.RANDOM) {
            ArrayList<TransitionType> randomType = new ArrayList<TransitionType>();
            Collections.addAll(randomType, TransitionType.values());
            Collections.shuffle(randomType);
            while (randomType.get(0) == TransitionType.NONE || randomType.get(0) == TransitionType.RANDOM) {
                randomType.remove(0);
            }
            type = randomType.get(0);
        }

        Transition animation = null;

        switch (type) {
            case FADE:
                animation = new Fade(duration);
                break;
            case PUSH:
                animation = new Push(duration, direction);
                break;
            case MOVEIN:
                animation = new MoveIn(duration, direction);
                break;
            case REVEAL:
                animation = new Reveal(duration, direction);
                break;
        }

        animator.setInAnimation(animation.getInAnimation());
        animator.setOutAnimation(animation.getOutAnimation());
    }
}
