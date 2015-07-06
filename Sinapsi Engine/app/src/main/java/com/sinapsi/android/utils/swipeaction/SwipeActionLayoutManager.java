package com.sinapsi.android.utils.swipeaction;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * An utility class to initialize and show SwipeActionButtons on a LinearLayout
 */
public class SwipeActionLayoutManager {
    LinearLayout linearLayout;
    private Context context;
    private DrawableLocation drawableLocation;


    public enum DrawableLocation {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public SwipeActionLayoutManager(Context c, LinearLayout ll, DrawableLocation dl) {
        linearLayout = ll;
        context = c;
        drawableLocation = dl;
    }


    public SwipeActionLayoutManager(Context c, LinearLayout ll) {
        linearLayout = ll;
        context = c;
        drawableLocation = DrawableLocation.LEFT;
    }

    /**
     * Removes all the actions from the linear layout
     */
    public void clear() {
        linearLayout.removeAllViews();
    }

    /**
     * Adds an action to the linear layout as a clickable Button
     *
     * @param action the action
     */
    public void addSwipeAction(SwipeActionButton action) {
        FloatingActionButton b = new FloatingActionButton(context);

        b.setIconDrawable(action.getIcon());

        b.setColorNormal(action.getColorNormal());
        b.setColorPressed(action.getColorPressed());

        b.setSize(FloatingActionButton.SIZE_MINI);

        b.setLayoutParams(new ViewGroup.LayoutParams(50, 50));
        b.setOnClickListener(action);
        linearLayout.addView(b);
    }


    /**
     * adds a custom view to the linear layout
     *
     * @param v the view
     */
    public void addCustomView(View v) {
        linearLayout.addView(v);
    }


    /**
     * sets the alpha of the linearLayout instance containing all the action buttons
     *
     * @param alpha a value from 0 to 1
     */
    public void setAlpha(float alpha) {
        linearLayout.setAlpha(alpha);
        for (int i = 0; i < linearLayout.getChildCount(); ++i) {
            linearLayout.getChildAt(i).setAlpha(alpha);
        }
    }
}
