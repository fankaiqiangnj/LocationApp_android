package com.example.kail.locationapp.base.activity;

import android.os.Bundle;

import com.example.kail.locationapp.base.mvp.MvpPresenter;
import com.example.kail.locationapp.base.mvp.MvpView;


public abstract class BaseMvpActivity<P extends MvpPresenter> extends BaseActivity implements MvpView {
    protected P presenter;
    protected abstract P createPresenter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        presenter = createPresenter();
        if (presenter == null) {
            throw new NullPointerException("Presenter is null! Do you return null in createPresenter()?");
        }
        presenter.onMvpAttachView(this, savedInstanceState);
        super.onCreate(savedInstanceState);

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.onMvpStart();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.onMvpResume();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (presenter != null) {
            presenter.onMvpPause();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.onMvpStop();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (presenter != null) {
            presenter.onMvpSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onMvpDetachView(false);
            presenter.onMvpDestroy();
        }
    }

}
