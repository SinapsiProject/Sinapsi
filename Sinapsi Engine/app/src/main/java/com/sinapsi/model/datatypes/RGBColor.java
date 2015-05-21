package com.sinapsi.model.datatypes;

/**
 * RGBColor class, used
 */
public class RGBColor {

    private int red = 255;
    private int green = 255;
    private int blue = 255;

    public RGBColor(int r, int g, int b){
        red = r;
        green = g;
        blue = b;
        checkValues();
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public void set(int r, int g, int b){
        red = r;
        green = g;
        blue = b;
        checkValues();
    }

    private void checkValues() {
        if(red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255 )
            throw new RuntimeException("Invalid rgb values");
    }
}
