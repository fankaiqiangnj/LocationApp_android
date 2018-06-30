package com.example.kail.locationapp.adapter;

import android.content.Context;
import android.view.View;

import com.example.kail.locationapp.R;
import com.example.kail.locationapp.base.adapter.BaseRecyclerAdapter;
import com.example.kail.locationapp.base.adapter.BaseRecylerViewHolder;
import com.example.kail.locationapp.model.MessageEvent;
import com.example.kail.locationapp.util.BaseUtil;
import com.example.kail.locationapp.util.SPUtils;

import java.util.List;

public class AlarmRecycleAdapter extends BaseRecyclerAdapter<MessageEvent> {
    public AlarmRecycleAdapter(Context context, int resource, List<MessageEvent> list) {
        super(context, resource, list);
    }

    @Override
    public void setConvert(BaseRecylerViewHolder viewHolder, MessageEvent messageEvent) {
        viewHolder.setTextView(R.id.check_id, messageEvent.getGdbh());
        viewHolder.setTextView(R.id.check_name, messageEvent.getQxnr().replaceAll(";", ";\n").trim());
        int type = (int) SPUtils.get(mContext, messageEvent.getGdbh(), 0);
        switch (type) {
            case 0:
                viewHolder.setTextView(R.id.tv_alarm,"接受处理");
                break;
            case 1:
                viewHolder.setTextView(R.id.tv_alarm,"到达现场");
                break;

            case 2:
                viewHolder.setTextView(R.id.tv_alarm,"完成处理");
                break;

            default:
                break;
        }

        viewHolder.getView(R.id.tv_alarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        viewHolder.getView(R.id.tv_daohang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseUtil.showToast(mContext, "功能正在开发中");
            }
        });
    }
}
