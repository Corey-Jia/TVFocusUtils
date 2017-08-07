/**
 * Copyright 2012 LeTV Technology Co. Ltd., Inc. All rights reserved.
 *
 * @Author : qingxia
 * @Description :
 */

package com.corey.tvfocuslib;

import android.content.Context;

/**
 * This class provide a global application context.
 *
 * @author liyang
 */
public final class ContextProvider {
    private static Context sContext = null;

    public static void initIfNotInited(Context context) {
        if (sContext == null) {
            init(context);
        }
    }

    /**
     * NOTE(liyang): This function should be invoked in Application while the
     * application is been created.
     *
     * @param context
     */
    public static void init(Context context) {
        if (context == null) {
            throw new NullPointerException("Can not use null initlialized application context");
        }
        sContext = context;
    }

    /**
     * Get application context.
     *
     * @return
     */
    public static Context getApplicationContext() {
        if (sContext == null) {
            throw new NullPointerException("Global application uninitialized");
        }
        return sContext;
    }

    private ContextProvider() {
    }
}
