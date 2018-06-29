package com.example.kail.locationapp.base.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kail on 2017/8/28.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecylerViewHolder>{
    protected static String TAG_LOG = null;
    /**
     * listview的item资源id
     */
    private int resourceId;
    protected Context mContext;

    protected List<T> list = new ArrayList<>();
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private View mHeaderView;

    public BaseRecyclerAdapter(Context context, int resource, List<T> list) {
        this.list = list;
        this.resourceId = resource;
        mContext = context;
        TAG_LOG = this.getClass().getSimpleName();
    }
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        LinearLayout.LayoutParams sp_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mHeaderView.setLayoutParams(sp_params);
        notifyItemInserted(0);
    }
    public View getHeaderView() {
        return mHeaderView;
    }


    @Override
    public int getItemViewType(int position) {
        if(mHeaderView == null) {return TYPE_NORMAL;}
        if(position == 0) {return TYPE_HEADER;}
        return TYPE_NORMAL;
    }

    @Override
    public BaseRecylerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mHeaderView != null && viewType == TYPE_HEADER) {return new BaseRecylerViewHolder(mHeaderView);}
        BaseRecylerViewHolder viewHolder = new BaseRecylerViewHolder(LayoutInflater.from(
                mContext ).inflate(resourceId, parent,
                false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseRecylerViewHolder holder, int position)
    {
        if(getItemViewType(position) == TYPE_HEADER) {
            return;
        }
        if (mHeaderView!=null){
            setConvert(holder,list.get(position-1));

        }else {
            setConvert(holder, list.get(position));
        }

    }


    @Override
    public int getItemCount()
    {
        return mHeaderView == null?list.size():list.size()+1;
    }



    /**
     * @param @param viewHolder
     * @param @param t 设定文件
     * @return void 返回类型
     * @Title: setConvert
     * @Description: 抽象方法，由子类去实现每个itme如何设置
     */
    public abstract void setConvert(BaseRecylerViewHolder viewHolder, T t);

    public void setList(List<T> list) {
        this.list = list;
    }

    public List<T> getList(){
        return list;
    }



    /**
     * 跳转另一个活动
     *
     * @param clazz
     */
    protected void go(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }

    /**
     * 跳转另一个活动并传递参数
     *
     * @param clazz
     * @param bundle
     */
    protected void go(Context context, Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
    }

    /**
     * 跳转另一个活动并结束当前
     *
     * @param clazz
     */
    protected void goThenKill(Context context, Class<?> clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    /**
     * 跳转另一个活动并结束，并传递参数
     *
     * @param clazz
     * @param bundle
     */
    protected void goThenKill(Context context, Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(context, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        context.startActivity(intent);
        ((Activity) context).finish();
    }



}
