package com.sinapsi.android.web;

import android.util.Base64;

import com.bgp.codec.EncodingMethod;

/**
 * Method to encode in Base64 using Android implementations
 */
public class AndroidBase64EncodingMethod implements EncodingMethod {
    @Override
    public String encodeAsString(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
