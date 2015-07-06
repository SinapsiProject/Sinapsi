package com.sinapsi.android.utils.swipeaction;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import com.sinapsi.android.R;
import com.sinapsi.model.MacroInterface;

/**
 * Example implementation of SwipeActionButton with macros
 */
public class SwipeActionMacroExampleButton extends SwipeActionButton<MacroInterface>{

    /**
     * Default ctor
     *
     * @param element the element represented by the swipe layout
     * @param context
     */
    public SwipeActionMacroExampleButton(MacroInterface element, Context context) {
        super(element, context);
    }


    @Override
    public void onDo(View v, MacroInterface e) {
        Toast.makeText(context,"Test button on Macro '" + e.getName() + "'", Toast.LENGTH_SHORT).show();
        //Do something with macro object...
    }

    @Override
    public String getName() {
        return "TestButton";
    }

    @Override
    public Drawable getIcon() {
        return context.getResources().getDrawable(R.drawable.ic_action_help);
    }

    @Override
    public int getColorNormal() {
        return context.getResources().getColor(R.color.sinapsi_blue);
    }

    @Override
    public int getColorPressed() {
        return context.getResources().getColor(R.color.sinapsi_blue_dark);
    }
}
