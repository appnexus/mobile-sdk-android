package com.appnexus.opensdk.transitionanimation;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

import com.appnexus.opensdk.BannerAdView.TransitionDirection;

public class Reveal implements Transition{
    private Animation inAnimation;
    private Animation outAnimation;

    private static final float up_coordinates[] = new float[]{0.0f, 0.0f, 0.0f, -1.0f};
    private static final float down_coordinates[] = new float[]{0.0f, 0.0f, 0.0f, +1.0f};
    private static final float right_coordinates[] = new float[]{0.0f, +1.0f, 0.0f, 0.0f};
    private static final float left_coordinates[] = new float[]{0.0f, -1.0f, 0.0f, 0.0f};

    public Reveal(long duration, TransitionDirection direction){
        Interpolator interpolator = new AccelerateInterpolator();
        setInAnimation(interpolator, duration);
        setOutAnimation(getDirection(direction), interpolator, duration);
    }

    private void setInAnimation(Interpolator interpolator, long duration){
        inAnimation = new AlphaAnimation(0, 1);
        inAnimation.setDuration(duration);
        inAnimation.setInterpolator(interpolator);
    }

    private void setOutAnimation(float[] direction, Interpolator interpolator, long duration) {
        Animation push = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, direction[0], Animation.RELATIVE_TO_PARENT, direction[1],
                Animation.RELATIVE_TO_PARENT, direction[2], Animation.RELATIVE_TO_PARENT, direction[3]
        );
        push.setInterpolator(interpolator);
        push.setDuration(duration);

        Animation fade = new AlphaAnimation(1, 0);
        fade.setDuration(duration);
        fade.setInterpolator(interpolator);

        AnimationSet set = new AnimationSet(false);
        set.addAnimation(push);
        set.addAnimation(fade);

        outAnimation = set;
    }

    private float[] getDirection(TransitionDirection direction) {
        switch (direction) {
            case Up:
                return Reveal.up_coordinates;
            case Down:
                return Reveal.down_coordinates;
            case Right:
                return Reveal.right_coordinates;
            case Left:
                return Reveal.left_coordinates;
        }
        return Reveal.up_coordinates;
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
