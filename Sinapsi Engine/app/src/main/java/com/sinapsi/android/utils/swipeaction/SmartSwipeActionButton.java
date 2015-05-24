package com.sinapsi.android.utils.swipeaction;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by Giuseppe on 24/05/15.
 */
public abstract class SmartSwipeActionButton<E> extends SwipeActionButton<E> {
    private final String name;
    private final Drawable icon;
    private final int colorNormal;
    private final int colorPressed;

    /**
     * Default ctor
     *
     * @param element the element represented by the swipe layout
     * @param context the context
     */
    public SmartSwipeActionButton(E element, Context context, String name, Drawable icon, int colorNormal, int colorPressed) {
        super(element, context);
        this.name = name;
        this.icon = icon;
        this.colorNormal = colorNormal;
        this.colorPressed = colorPressed;
    }

    @Override
    public abstract void onDo(View v, E e);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Drawable getIcon() {
        return icon;
    }

    @Override
    public int getColorNormal() {
        return colorNormal;
    }

    @Override
    public int getColorPressed() {
        return colorPressed;
    }
}
