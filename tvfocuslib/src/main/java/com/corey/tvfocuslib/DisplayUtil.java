package com.corey.tvfocuslib;

import android.content.Context;

/**
 * dp、sp 转换为 px 的工具类
 *
 * @author liyang 2015.03.16
 */
public class DisplayUtil {
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dp(Context context, float pxValue) {
        if (context == null) {
            context = ContextProvider.getApplicationContext();
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        if (context == null) {
            context = ContextProvider.getApplicationContext();
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        if (context == null) {
            context = ContextProvider.getApplicationContext();
        }
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        if (context == null) {
            context = ContextProvider.getApplicationContext();
        }
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
