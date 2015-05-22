package com.sinapsi.android.web;

import android.util.Base64;

import com.bgp.codec.DecodingMethod;

/**
 * Created by Giuseppe on 20/05/15.
 */
public class AndroidBase64DecodingMethod implements DecodingMethod {
    @Override
    public byte[] decode(String s) {
        return Base64.decode(s, Base64.DEFAULT);
    }
}
