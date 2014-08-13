package com.appnexus.opensdk.transitionanimation;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

public class MoveIn implements Transition {
    private Animation inAnimation;
    private Animation outAnimation;

    private static final float up_coordinates[] = new float[]{0.0f, 0.0f, +1.0f, 0.0f};
    private static final float down_coordinates[] = new float[]{0.0f, 0.0f, -1.0f, 0.0f};
    private static final float right_coordinates[] = new float[]{-1.0f, 0.0f, 0.0f, 0.0f};
    private static final float left_coordinates[] = new float[]{+1.0f, 0.0f, 0.0f, 0.0f};

    public MoveIn(long duration, TransitionDirection direction){
        Interpolator interpolator = new AccelerateInterpolator();
        setInAnimation(getDirection(direction), interpolator, duration);
        setOutAnimation(interpolator, duration);

    }

    private float[] getDirection (TransitionDirection direction) {
        switch (direction) {
            case UP:
                return MoveIn.up_coordinates;
            case DOWN:
                return MoveIn.down_coordinates;
            case RIGHT:
                return MoveIn.right_coordinates;
            case LEFT:
                return MoveIn.left_coordinates;
        }
        return MoveIn.up_coordinates;
    }

    private void setInAnimation(float[] direction, Interpolator interpolator, long duration){
        inAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, direction[0], Animation.RELATIVE_TO_PARENT, direction[1],
                Animation.RELATIVE_TO_PARENT, direction[2], Animation.RELATIVE_TO_PARENT, direction[3]
        );
        inAnimation.setInterpolator(interpolator);
        inAnimation.setDuration(duration);
    }

    private void setOutAnimation(Interpolator interpolator, long duration){
        outAnimation = new AlphaAnimation(1, 0);
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
