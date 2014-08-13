package com.appnexus.opensdk.transitionanimation;

public enum TransitionType {
    NONE,
    RANDOM,
    FADE,
    PUSH,
    MOVEIN,
    REVEAL;

    public static TransitionType getTypeForInt(int i){
        for (TransitionType type: TransitionType.values()){
            if (type.ordinal() == i)
                return type;
        }
        return TransitionType.NONE;
    }
}
