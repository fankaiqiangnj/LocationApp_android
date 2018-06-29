package com.example.kail.locationapp.base.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import com.example.kail.locationapp.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

public abstract class BaseActivity extends AppCompatActivity {
    /**
     * 几种页面切换动画的枚举类
     */
    public enum TransitionMode {
        LEFT, RIGHT, TOP, BOTTOM, SCALE, FADE
    }

    private SweetAlertDialog progesDialog;
    /**
     * 设备屏幕信息参数
     */
    protected int mScreenWidth = 0;

    protected int mScreenHeight = 0;

    protected float mScreenDensity = 0.0f;
    Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (isCustomPendingTransition()) {
            if (getCustomPendingTransitionType() == null) {
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            } else {
                switch (getCustomPendingTransitionType()) {
                    case LEFT:
                        overridePendingTransition(R.anim.left_in, R.anim.left_out);
                        break;
                    case RIGHT:
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                        break;
                    case TOP:
                        overridePendingTransition(R.anim.top_in, R.anim.top_out);
                        break;
                    case BOTTOM:
                        overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                        break;
                    case SCALE:
                        overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                        break;
                    case FADE:
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                    default:
                        break;
                }
            }
        }
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        // 如果有extras，则在getBundleExtras（）进行处理
        if (null != extras) {
            getBundleExtras(extras);
        }
        // 是否全屏应用
        setTranslucentStatus(isApplyStatusBarTranslucency());
        // 页面堆栈管理
        ActivityCollections.getInstance().addActivity(this);
        // 初始化屏幕相关信息
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
        // 给activity绑定布局文件
        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID());
        } else {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }
        mUnbinder = ButterKnife.bind(this);
        initViewsAndEvents();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        ActivityCollections.getInstance().removeActivity(this);
    }

    /**
     * 跳转另一个活动
     *
     * @param clazz
     */
    protected void go(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    /**
     * 跳转另一个活动并传递参数
     *
     * @param clazz
     * @param bundle
     */
    protected void go(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转另一个活动并结束当前
     *
     * @param clazz
     */
    protected void goThenKill(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转另一个活动并结束，并传递参数
     *
     * @param clazz
     * @param bundle
     */
    protected void goThenKill(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
    }

    /**
     * 开始一个活动，并等待返回结果，并传递参数
     *
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    protected void goForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 开始一个活动，并等待返回结果
     *
     * @param clazz
     * @param requestCode
     */
    protected void goForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 标题弹框
     */
    public SweetAlertDialog showSweetDialogPrompt(String title, SweetAlertDialog.OnSweetClickListener sweetCallBack) {
        SweetAlertDialog sw = new SweetAlertDialog(this)
                .setTitleText(title)
                .setConfirmText("确定")
                .setConfirmClickListener(sweetCallBack);
        sw.show();
        return sw;
    }

    /**
     * 标题弹框
     */
    public SweetAlertDialog showSweetDialogPrompt(String title, String content, SweetAlertDialog.OnSweetClickListener sweetCallBack) {
        SweetAlertDialog sw = new SweetAlertDialog(this)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("确定")
                .setConfirmClickListener(sweetCallBack);
        sw.show();
        return sw;
    }

    /**
     * 等待框
     *
     * @param context
     */
    public void showProgesDialog(Context context) {
        if (!(progesDialog!=null&&progesDialog.isShowing())){
            progesDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            progesDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            progesDialog.setCanceledOnTouchOutside(true);
            progesDialog.setTitleText("Loading");
            progesDialog.show();
        }
    }
    protected void disMissProgess(){
        if (progesDialog!=null&&progesDialog.isShowing()){
            progesDialog.dismissWithAnimation();
        }
    }


    /**
     * 提示类型的弹出框
     *
     * @param title
     * @param content
     */
    protected SweetAlertDialog showSweetDialogPrompt(String title, String content) {
        SweetAlertDialog sw = new SweetAlertDialog(this)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("确定");
        sw.show();
        return sw;

    }


    /**
     * 成功类型的弹出框
     *
     * @param title
     * @param content
     */
    protected SweetAlertDialog showSweetDialogSuccess(String title, String content) {
        SweetAlertDialog sw = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("确定");
        sw.show();
        return sw;
    }

    /**
     * 成功类型的弹出框
     *
     * @param title
     */
    protected SweetAlertDialog showSweetDialogSuccess(String title) {
        SweetAlertDialog sw = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(title)
                .setConfirmText("确定");
        sw.show();
        return sw;
    }

    /**
     * 错误提示类型的弹出框
     *
     * @param title
     * @param content
     */
    protected SweetAlertDialog showSweetDialogFail(String title, String content) {
        SweetAlertDialog sw = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(title)
                .setContentText(content)
                .setConfirmText("确定");

        // 可以点击外部取消
        sw.setCanceledOnTouchOutside(false);
        sw.show();
        return sw;
    }


    /**
     * 提示警告
     */
    protected SweetAlertDialog showSweetDialogConfirm(String title) {
        SweetAlertDialog sw = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setConfirmText("确定")
                .showCancelButton(true);
        // 可以点击外部取消
        sw.setCanceledOnTouchOutside(false);
        sw.show();
        return sw;
    }

    /**
     * 提示警告
     */
    protected void showSweetDialogConfirm(String title, SweetAlertDialog.OnSweetClickListener sweetCallBack) {
        SweetAlertDialog sw = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setConfirmText("确定")
                .setCancelText("取消")
                .setConfirmClickListener(sweetCallBack)
                .showCancelButton(true);
        // 可以点击外部取消
        sw.setCanceledOnTouchOutside(false);
        sw.show();
    }

    /**
     * 处理Bundle传参
     *
     * @param extras
     */
    protected abstract void getBundleExtras(Bundle extras);

    /**
     * set status bar translucency 安卓4.4以上可以开启APP全屏模式 windowTranslucentStatus Flag indicating whether this window
     * requests a translucent status bar. 大意就是说状态栏是否半透明，如果是true的话，你会发现你的Toolbar陷入到状态栏里面了，
     * 所以为了预留空间，需要下面的属性：android:fitsSystemWindows
     *
     * @param on
     */
    protected void setTranslucentStatus(boolean on) {
        if (on) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Window win = getWindow();
                win.setFlags(
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }


    protected void changeFragment(int resId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(resId,fragment,tag).commit();
    }

    /**
     * 添加Fragment到FragmentList中
     */
    protected void addFragment(int resId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(resId, fragment, tag);
        transaction.commit();
    }

    /**
     * 清空fragmentList的所有Fragment，替换成新的Fragment，注意Fragment里面的坑
     */
    protected void replaceFragment(int resId, Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(resId, fragment, tag);
        transaction.commit();
    }

    /**
     * 移除指定的Fragment
     */
    protected void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
    }

    /**
     * 把Fragment设置成显示状态，但是并没有添加到FragmentList中
     */
    protected void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(fragment);
        transaction.commit();
    }

    /**
     * 把Fragment设置成不显示状态，但是并没有添加到FragmentList中
     */
    protected void hideFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(fragment);
        transaction.commit();
    }

    /**
     * 效果和show相近，创建视图，添加到containerid指定的Added列表，FragmentList依然保留，但是会引起生命周期的变化
     */
    protected void attachFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.attach(fragment);
        transaction.commit();
    }

    /**
     * 效果和hide相近，清除视图，从containerid指定的Added列表移除，FragmentList依然保留，但是会引起生命周期的变化
     */
    protected void detachFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.detach(fragment);
        transaction.commit();
    }


    /**
     * 绑定布局文件
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();

    /**
     * 是否开启应用的全屏展示 安卓4.4以上可以开启APP全屏模式
     *
     * @return
     */
    protected abstract boolean isApplyStatusBarTranslucency();

    /**
     * @return true--自定义页面的切换动画   false--不自定义
     */
    protected abstract boolean isCustomPendingTransition();

    /**
     * @return 返回自定义的动画切换方式
     */
    protected abstract TransitionMode getCustomPendingTransitionType();

    /**
     * 初始化所有布局和event事件
     */
    protected abstract void initViewsAndEvents();
}
