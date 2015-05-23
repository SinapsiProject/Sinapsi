package com.sinapsi.android.utils;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

/**
 * Collections of methods to elaborate drawables
 */
public class DrawableUtils {

    /**
     * Return a coloured clone instance of source
     * @param source a drawable, to be coloured
     * @param color the color
     * @return the colored clone
     */
    public static Drawable getColoredDrawable(Drawable source, int color){
        Drawable clone = source.getConstantState().newDrawable();
        clone.mutate().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return clone;
    }
}