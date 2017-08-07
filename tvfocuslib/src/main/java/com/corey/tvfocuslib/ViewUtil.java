package com.corey.tvfocuslib;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

/**
 * @ClassName: ViewUtil
 * @Description:
 * @author: liyang
 * @date 2015-3-16 下午5:01:12
 */
@SuppressLint("NewApi")
public class ViewUtil {

    /**
     * 移除OnGlobalLayoutListener
     * @param view
     * @param listener
     */
    @SuppressWarnings("deprecation")
    public static void removeOnGlobalLayoutListener(View view,
            OnGlobalLayoutListener listener) {
        if (view != null && listener != null) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= 16) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(
                            listener);
                } else {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(
                            listener);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取View在屏幕中的位置
     */
    public static int[] getLocation(View view) {
        if (view == null) {
            return null;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location;
    }

    /**
     * 判断View是否可见
     * @param view
     * @return
     */
    public static boolean isVisible(View view) {
        if (view == null) {
            return false;
        }
        return view.getVisibility() == View.VISIBLE;
    }

    /**
     * 显示/隐藏View
     * @param view
     *            需要显示/隐藏的View
     * @param isShow
     *            是否显示
     */
    public static void showView(View view, boolean isShow) {
        if (view == null) {
            return;
        }
        boolean visible = ViewUtil.isVisible(view);
        if (isShow) {
            if (!visible) {
                view.setVisibility(View.VISIBLE);
            }
        } else {
            if (visible) {
                view.setVisibility(View.GONE);
            }
        }
    }

/*    public static void showView(LoadingView view, boolean isShow) {
        if (view == null) {
            return;
        }
        boolean visible = ViewUtil.isVisible(view);
        if (isShow) {
            if (!visible) {
                view.setVisibility(View.VISIBLE);
            }
        } else {
            if (visible) {
                view.setVisibility(View.GONE);
                // view.recycle();
            }
        }
    }*/

    /**
     * 传入view返回bigmap
     * @param v
     * @return
     */
    public static Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }
}
