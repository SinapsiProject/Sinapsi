package com.sinapsi.android.utils.swipeaction;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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
    public void addAction(SwipeActionButton action) {
        Button b = new Button(context);
        switch (drawableLocation) {

            case TOP:
                b.setCompoundDrawablesWithIntrinsicBounds(null, action.getIcon(), null, null);
                break;
            case BOTTOM:
                b.setCompoundDrawablesWithIntrinsicBounds(null, null, null, action.getIcon());
                break;
            case LEFT:
                b.setCompoundDrawablesWithIntrinsicBounds(action.getIcon(), null, null, null);
                break;
            case RIGHT:
                b.setCompoundDrawablesWithIntrinsicBounds(null, null, action.getIcon(), null);
                break;
        }
        b.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        //since background color is transparent, this will give back touch feedback
        b.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((Button) view).setBackgroundColor(Color.argb(100, 255, 255, 255));
                        break;
                    case MotionEvent.ACTION_UP:
                        ((Button) view).setBackgroundColor(Color.TRANSPARENT);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        ((Button) view).setBackgroundColor(Color.TRANSPARENT);
                }
                return false;

            }
        });
        b.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        b.setText(action.getName());
        b.setMaxLines(1);
        b.setOnClickListener(action);
        b.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        b.setHorizontallyScrolling(true);
        b.setSelected(true);
        linearLayout.addView(b);
    }
}
