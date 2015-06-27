package com.sinapsi.webshared.wsproto;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Giuseppe on 11/06/15.
 */
public class ServerAdapter {




    private InvocationHandler ih = new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return null;
        }
    };



}
