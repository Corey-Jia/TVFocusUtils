package com.corey.sample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.corey.tvfocuslib.ContextProvider;
import com.corey.tvfocuslib.FocusUtils;
import com.corey.tvfocuslib.ResolutionUtil;

/**
 * Created by corey_jia on 17/8/7.
 */

public class BaseActivity extends FragmentActivity implements
        ViewTreeObserver.OnGlobalFocusChangeListener {

    private static final String TAG = "BaseActivity";
    protected ResolutionUtil resolutionUtil;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        resolutionUtil = new ResolutionUtil(this);
        ContextProvider.init(this);
    }

    /**焦点框**/
    /**
     * 焦点框延时显示时间，解决部分页面第一次进入时丢失焦点问题
     */
    private static final int FOCUS_DELAY_TIME = 50;
    /**
     * Activity根布局
     */
    protected ViewGroup mRootView;
    /**
     * 焦点框
     */
    protected ImageView mFocus;

    private View mFocusView;

    //光标移动回调接口
    private OnCJFocusChangeListener onCJFocusChangeListener;

    public interface OnCJFocusChangeListener {
        public void onCJFocusChangeListener(View oldView, View newView);
    }

    public void setOnCJFocusChangeListener(OnCJFocusChangeListener onCJFocusChangeListener) {
        this.onCJFocusChangeListener = onCJFocusChangeListener;
    }

    private final Handler mHandler = new Handler();

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        this.initRootView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        this.initRootView();
    }

    @Override
    public void setContentView(View view,
                               android.view.ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        this.initRootView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 页面显示时，需要显示焦点框
        this.showFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 页面隐藏时，需要隐藏焦点框
        this.hideFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除全局焦点监听
        if (this.mRootView != null) {
            this.mRootView.getViewTreeObserver()
                    .removeOnGlobalFocusChangeListener(this);
        }
    }

    /**
     * 初始化根布局，设置全局焦点监听器
     */
    private void initRootView() {
        this.mRootView = (ViewGroup) this.findViewById(R.id.root_view);
        // 添加全局焦点监听
        if (this.mRootView != null) {
            this.mRootView.getViewTreeObserver().addOnGlobalFocusChangeListener(this);
        }
    }

    public ImageView getFocus() {
        return this.mFocus;
    }

    public View getFocusView() {
        return this.mFocusView;
    }

    /**
     * 隐藏焦点框
     */
    public void hideFocus() {
        if (this.mFocus != null) {
            this.mFocus.setVisibility(View.GONE);
        }
    }

    /**
     * 显示焦点框
     */
    public void showFocus() {
        if (this.mFocus != null) {
            this.mFocus.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 如果继承的AbsListView需调用此方法
     * @param view
     */
    public void setAdapterViewFocus(AdapterView view) {
        if (view == null) {
            return;
        }
        if (view.hasFocus()) {
            View item = view.getSelectedView();
            if (this.onCJFocusChangeListener != null) {
                this.onCJFocusChangeListener.onCJFocusChangeListener(null, view);
            }
            this.onGlobalFocusChanged(null, item);
        }
    }

    /**
     * 设置焦点延时显示
     */
    public void setDelayFocus(final View view) {
        if (view == null) {
            return;
        }
        this.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.requestFocus();
            }
        }, FOCUS_DELAY_TIME);
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (newFocus == null) {
            return;
        }
        // 过滤焦点特殊处理的View
        if (newFocus instanceof AdapterView) {
            return;
        }
        if (this.onCJFocusChangeListener != null && oldFocus != null && newFocus != null) {
            this.onCJFocusChangeListener.onCJFocusChangeListener(oldFocus, newFocus);
        }
        if (this.mFocus == null) {
            this.initFocus(newFocus);
        } else {
            this.moveFocus(newFocus);
        }
        if (newFocus != null) {
            if (newFocus.getParent() instanceof AdapterView) {
                this.mFocusView = (View) newFocus.getParent();
            } else {
                this.mFocusView = newFocus;
            }
        }
    }

    private void initFocus(final View view) {
        if (view == null || view.getWidth() <= 0 || view.getHeight() <= 0) {
            return;
        }
        BaseActivity.this.mFocus = FocusUtils.getFocusView(BaseActivity.this,R.mipmap.image_focus);
        FocusUtils.initFocusViewLocation(BaseActivity.this.mFocus, view);
        this.mRootView.addView(this.mFocus);
    }

    public void moveFocus(final View view) {
        FocusUtils.setFocusViewLocation(this.mFocus, view);
    }
}
