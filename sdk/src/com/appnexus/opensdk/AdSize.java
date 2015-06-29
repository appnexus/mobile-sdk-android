package com.appnexus.opensdk;

/**
 * A convenience class which holds a width and height in integers.
 */
public class AdSize {
    private final int w;
    private final int h;

    public AdSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    /**
     * @return The width, in pixels.
     */
    public int width() {
        return w;
    }

    /**
     * @return The height, in pixels.
     */
    public int height() {
        return h;
    }

    /**
     * Determines whether this size object fits inside a rectangle of the
     * given width and height
     *
     * @param width  The width to check against.
     * @param height The height to check against.
     * @return True, if the size fits inside the described rectangle,
     * otherwise, false.
     */
    public boolean fitsIn(int width, int height) {
        return h < height && w < width;
    }

}
