package com.sinapsi.android.web;

import android.util.Base64;

import com.bgp.codec.EncodingMethod;

/**
 * Created by Giuseppe on 20/05/15.
 */
public class AndroidBase64EncodingMethod implements EncodingMethod {
    @Override
    public String encodeAsString(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
