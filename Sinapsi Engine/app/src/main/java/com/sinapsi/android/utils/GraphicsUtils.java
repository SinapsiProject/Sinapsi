package com.sinapsi.android.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

import com.sinapsi.android.R;
import com.sinapsi.model.MacroInterface;

/**
 * Collections of methods to elaborate drawables
 */
public class GraphicsUtils {

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

    /**
     * Generates a new Drawable with the macro chosen icon upon
     * a gradient variation of the macro's color.
     *
     * @param macro the macro
     * @param context the context, used to resolve the icon resource
     * @return the icon drawable
     */
    public static Drawable generateMacroIconWhiteOnColor(MacroInterface macro, Context context){
        int macroColor = Color.parseColor(macro.getMacroColor());

        Drawable[] layers = new Drawable[2];

        int resourceId = context.getResources().getIdentifier(macro.getIconName(), "drawable", context.getPackageName());
        Drawable d = context.getResources().getDrawable(resourceId);

        float scaleSize = 0.75f;
        if(d == null) return null;

        //d.setBounds(0, 0, (int) (d.getIntrinsicWidth() * scaleSize), (int) (d.getIntrinsicHeight() * scaleSize));
        //TODO: resize image (shrink)

        if(macro.isValid()){
            ColorDrawable cd = new ColorDrawable(macroColor); //the new material style
            /*GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{
                            scaleColorProperty(macroColor, ColorProperties.BRIGHTNESS, 2.0f),
                            scaleColorProperty(macroColor, ColorProperties.SATURATION, 0.5f)});*/ //the old gradient style
            layers[0] = cd;
            layers[1] = d;
        }else{

            ColorDrawable cd = new ColorDrawable(context.getResources().getColor(R.color.cardview_light_background));
            /*GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{
                            scaleColorProperty(errorBGColor, ColorProperties.BRIGHTNESS, 2.0f),
                            scaleColorProperty(errorBGColor, ColorProperties.SATURATION, 0.5f)});*/
            layers[0] = cd;
            Drawable error = getColoredDrawable(d, context.getResources().getColor(R.color.error_red));
            layers[1] = error;
        }

        return new LayerDrawable(layers);
    }


    public static Drawable generateMacroIconColoredOnTransparent(MacroInterface macro, Context context){
        Drawable[] layers = new Drawable[2];
        int resourceId = context.getResources().getIdentifier(macro.getIconName(), "drawable", context.getPackageName());
        Drawable d = context.getResources().getDrawable(resourceId);

        if(d == null) return null;

        int macroColor = Color.parseColor(macro.getMacroColor());

        layers[0] = new ColorDrawable(context.getResources().getColor(R.color.cardview_light_background));
        layers[1] = getColoredDrawable(d, macroColor);
        return  new LayerDrawable(layers);
    }



    public enum ColorProperties{
        HUE,
        SATURATION,
        BRIGHTNESS,
        ALPHA,
        RED,
        GREEN,
        BLUE
    }

    public static int scaleColorProperty(int original, ColorProperties colorProperty, float scale){

        float[] hsv = new float[3];
        Color.colorToHSV(original, hsv);
        int a = Color.alpha(original);
        int r = Color.red(original);
        int g = Color.green(original);
        int b = Color.blue(original);
        switch (colorProperty){

            case HUE:{
                hsv[0] *= scale;
                return Color.HSVToColor(hsv);
            }
            case SATURATION:{
                hsv[1] *= scale;
                return Color.HSVToColor(hsv);
            }
            case BRIGHTNESS:{
                hsv[2] *= scale;
                return Color.HSVToColor(hsv);
            }
            case ALPHA:{
                a *= scale;
                break;
            }
            case RED:{
                r *= scale;
                break;
            }
            case GREEN:{
                g *= scale;
                break;
            }
            case BLUE:{
                b *= scale;
                break;
            }
        }

        return Color.argb(a, r, g, b);
    }

    public static String getStringHexOfColor(int color){
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return String.format("#%02X%02X%02X", r, g, b);
    }
}