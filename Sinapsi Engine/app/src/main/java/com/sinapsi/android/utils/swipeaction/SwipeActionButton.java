package com.sinapsi.android.utils.swipeaction;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;


/**
 * Utility class, representing an action button on the back side of a SwipeLayout
 */
public abstract class SwipeActionButton<E> implements View.OnClickListener {

    private E element;
    protected Context context;

    /**
     * Default ctor
     *
     * @param element the element represented by the swipe layout
     * @param context the context
     */
    public SwipeActionButton(E element, Context context) {
        this.element = element;
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        onDo(v, element);
    }

    /**
     * What to do when the action button is clicked
     *
     * @param v the view that was clicked
     * @param e the element represented by the swipe layout
     */
    public abstract void onDo(View v, E e);

    /**
     * The label of this action button
     *
     * @return the label
     */
    public abstract String getName();

    /**
     * The icon for this action button
     *
     * @return the icon
     */
    public abstract Drawable getIcon();

    /**
     * The background color for this action button
     *
     * @return the color
     */
    public abstract int getColorNormal();


    /**
     * The background color for this action button when
     * it is pressed
     *
     * @return the color
     */
    public abstract int getColorPressed();


}
