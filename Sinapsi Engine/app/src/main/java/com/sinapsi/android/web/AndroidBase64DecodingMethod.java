package com.sinapsi.android.web;

import android.util.Base64;

import com.bgp.codec.DecodingMethod;

/**
 * Method to decode from Base64 using Android implementations
 */
public class AndroidBase64DecodingMethod implements DecodingMethod {
    @Override
    public byte[] decode(String s) {
        return Base64.decode(s, Base64.DEFAULT);
    }
}
