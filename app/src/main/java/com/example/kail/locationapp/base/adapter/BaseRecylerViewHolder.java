package com.example.kail.locationapp.base.adapter;


import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


/**
 * Created by kail on 2017/8/28.
 */
public class BaseRecylerViewHolder extends RecyclerView.ViewHolder{
    /**
     * 视图容器
     */
    private SparseArray<View> mViews;

    /**
     * 视图
     */
    private View mConvertView;



    public BaseRecylerViewHolder(View itemView) {
        super(itemView);
        mConvertView = itemView;
        mViews = new SparseArray<View>();
    }




    /**
     * @param viewId 控件id
     * @return 根据控件id获取到控件
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * @return 返回视图类
     */
    public View getConvertView() {
        return mConvertView;
    }


    /**
     * @return 设置textview相关
     */
    public BaseRecylerViewHolder setTextView(int viewId, String content) {
        TextView tv = getView(viewId);
        tv.setText(content);
        return this;
    }

    public BaseRecylerViewHolder setEditTextView(int viewId, String content) {
        EditText tv = getView(viewId);
        tv.setText(content);
        return this;
    }

    public BaseRecylerViewHolder setTextView(int viewId, Spanned content) {
        TextView tv = getView(viewId);
        tv.setText(content);
        return this;
    }

    /**
     * 设置textview相关
     *
     * @param viewId
     * @param content
     * @return
     */
    public BaseRecylerViewHolder setTextView(int viewId, SpannableString content) {
        TextView tv = getView(viewId);
        tv.setText(content);
        return this;
    }

    /**
     * 设置textview相关
     *
     * @param viewId
     * @param content
     * @return
     */
    public BaseRecylerViewHolder setTextView(int viewId, int content) {
        TextView tv = getView(viewId);
        tv.setText(content);
        return this;
    }


    /**
     * 展示资源图片
     */
    public BaseRecylerViewHolder setImageView(int viewId, int resourceId) {
        ImageView iv = getView(viewId);
        Glide.with(this.getConvertView().getContext()).load(resourceId).into(iv);
        return this;
    }
    /**
     * 展示网络或本地图片
     */
    public BaseRecylerViewHolder setImageView(int viewId, String uri) {
        ImageView iv = getView(viewId);
        Glide.with(this.getConvertView().getContext()).load(uri).into(iv);
        return this;
    }

    // TODO 可以根据自己的需要编写更多适用的方法。。。
    public BaseRecylerViewHolder setCheckBox(int viewId, boolean isCheck) {
        CheckBox iv = getView(viewId);
        iv.setChecked(isCheck);
        return this;
    }

}
