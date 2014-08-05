package com.appnexus.opensdk.transitionanimation;

import android.view.animation.Animation;

public interface Transition {
    /**
     * Get the in animation for a view animator
     *
     * @return animation
     */
    public Animation getInAnimation();

    /**
     * Get the out animation for a view animator
     *
     * @return animation
     */
    public Animation getOutAnimation();
}
