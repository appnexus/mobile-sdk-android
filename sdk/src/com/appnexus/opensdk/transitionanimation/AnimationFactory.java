package com.appnexus.opensdk.transitionanimation;

import com.appnexus.opensdk.BannerAdView.TransitionType;
import com.appnexus.opensdk.BannerAdView.TransitionDirection;

public class AnimationFactory {
    public static Transition create(TransitionType type, long duration, TransitionDirection direction){
        Transition animation = null;
        switch (type) {
            case Fade:
                animation = new Fade(duration);
                break;
            case Push:
                animation = new Push(duration, direction);
                break;
            case MoveIn:
                animation = new MoveIn(duration, direction);
                break;
            case Reveal:
                animation = new Reveal(duration, direction);
                break;
        }
        return animation;
    }
}
