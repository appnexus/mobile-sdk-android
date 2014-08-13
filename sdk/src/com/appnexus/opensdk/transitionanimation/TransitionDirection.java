package com.appnexus.opensdk.transitionanimation;

public enum TransitionDirection {
    UP,
    DOWN,
    RIGHT,
    LEFT;

    public static TransitionDirection getDirectionForInt(int i){
        for (TransitionDirection direction: TransitionDirection.values()){
            if (direction.ordinal() == i)
                return direction;
        }
        return TransitionDirection.UP;
    }
}
