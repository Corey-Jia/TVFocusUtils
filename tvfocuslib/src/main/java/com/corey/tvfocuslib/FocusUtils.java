package com.corey.tvfocuslib;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;


public class FocusUtils {
    private static int ANIM_TIME = 200;
    private static boolean isShowFocusMoveAni = true;
    /**
     * 焦点框边缘发光部分margin
     */
    public static int focusMarginLeft = DisplayUtil.dp2px(ContextProvider.getApplicationContext(), 7);
    public static int focusMarginTop = DisplayUtil.dp2px(ContextProvider.getApplicationContext(), 7);
    public static int focusMarginRight = DisplayUtil.dp2px(ContextProvider.getApplicationContext(), 7);
    public static int focusMarginBottom = DisplayUtil.dp2px(ContextProvider.getApplicationContext(), 7);

    /**
     * 设置焦点框margin   单位dp  默认7dp
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public static void setFocusMargin(int left, int top, int right, int bottom) {
        focusMarginLeft = DisplayUtil.dp2px(ContextProvider.getApplicationContext(), left);
        focusMarginTop = DisplayUtil.dp2px(ContextProvider.getApplicationContext(), top);
        focusMarginRight = DisplayUtil.dp2px(ContextProvider.getApplicationContext(), right);
        focusMarginBottom = DisplayUtil.dp2px(ContextProvider.getApplicationContext(), bottom);
    }

    /**
     * 设置焦点框移动动画时长，默认200毫秒
     * @param animTime
     */
    public static void setAnimTime(int animTime) {
        ANIM_TIME = animTime;
    }

    public static ImageView getFocusView(Context context, int imgId) {
        if (context == null) {
            return null;
        }
        ImageView focusView = new ImageView(context);
        focusView.setFocusable(false);
        focusView.setFocusableInTouchMode(false);
        focusView.setScaleType(ScaleType.CENTER);
        focusView.setBackgroundResource(imgId);


        return focusView;
    }


    /**
     * 设置是否显示移动动画，默认显示
     *
     * @param isShowFocusMoveAni
     */
    public static void setIsShowFocusMoveAni(boolean isShowFocusMoveAni) {
        FocusUtils.isShowFocusMoveAni = isShowFocusMoveAni;
    }

    public static LayoutParams getLayoutParams(int width, int height) {
        if (width <= 0 || height <= 0) {
            return null;
        }
        LayoutParams params = new LayoutParams(width, height);
        params.width = width + focusMarginLeft + focusMarginRight;
        params.height = height + focusMarginTop + focusMarginBottom;
        return params;
    }

    public static void initFocusViewLocation(ImageView focusView, View view) {
        if (focusView == null || view == null || view.getWidth() <= 0
                || view.getHeight() <= 0) {
            return;
        }
        int location[] = ViewUtil.getLocation(view);
        if (location == null || location[0] < 0 || location[1] < 0) {
            return;
        }
        int width = view.getWidth();
        int height = view.getHeight();
        int x = location[0];
        int y = location[1];
        int focusX = x - FocusUtils.focusMarginLeft;
        int focusY = y - FocusUtils.focusMarginTop;
        LayoutParams focusParams = getLayoutParams(width, height);
        focusView.setX(focusX);
        focusView.setY(focusY);
        focusView.setLayoutParams(focusParams);
    }

    /**
     * 设置焦点框到某view的位置
     *
     * @param focusView
     * @param view
     */
    public static void setFocusViewLocation(ImageView focusView, View view) {
        if (focusView == null || view == null || view.getWidth() <= 0
                || view.getHeight() <= 0) {
            return;
        }
        int location[] = ViewUtil.getLocation(view);
        if (location == null || location[0] < 0 || location[1] < 0) {
            return;
        }
        int width = view.getWidth();
        int height = view.getHeight();
        int x = location[0];
        int y = location[1];
        int focusX = x - FocusUtils.focusMarginLeft;
        int focusY = y - FocusUtils.focusMarginTop;
        // 如果焦点框大小、位置未改变，则不需要重绘焦点框
        if (focusView.getX() == focusX && focusView.getY() == focusY
                && focusView.getWidth() == width + focusMarginLeft + focusMarginRight
                && focusView.getHeight() == height + focusMarginTop + focusMarginBottom) {
            return;
        }
        LayoutParams focusParams = getLayoutParams(width, height);
        if (isShowFocusMoveAni) {
            startMoveFocusAnimation(focusView, focusX, focusY,
                    focusParams.width, focusParams.height);
        } else {
            focusView.setX(focusX);
            focusView.setY(focusY);
            focusView.setLayoutParams(focusParams);
        }
    }

    private static void startMoveFocusAnimation(ImageView focusView,
                                                int focusX, int focusY, int width, int height) {
        if (focusView == null || width <= 0 || height <= 0) {
            return;
        }
        PropertyValuesHolder focusXHolder = PropertyValuesHolder.ofFloat("x",
                focusX);
        PropertyValuesHolder focusYHolder = PropertyValuesHolder.ofFloat("y",
                focusY);
        ObjectAnimator locationAnim = ObjectAnimator.ofPropertyValuesHolder(
                focusView, focusXHolder, focusYHolder);
        locationAnim.setDuration(ANIM_TIME);
        locationAnim.start();

        ViewWrapper wrapper = new ViewWrapper(focusView);
        PropertyValuesHolder widthHolder = PropertyValuesHolder.ofInt("width",
                width);
        PropertyValuesHolder heightHolder = PropertyValuesHolder.ofInt(
                "height", height);
        ObjectAnimator sizeAnim = ObjectAnimator.ofPropertyValuesHolder(
                wrapper, widthHolder, heightHolder);
        sizeAnim.setDuration(ANIM_TIME);
        sizeAnim.start();

    }
}
