package com.example.kail.locationapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.example.kail.locationapp.R;
import com.example.kail.locationapp.adapter.AlarmRecycleAdapter;
import com.example.kail.locationapp.base.util.ScreenUtils;
import com.example.kail.locationapp.model.MessageEvent;

import java.util.List;

/**
 * Created by fan on 2018/4/15.
 */

public class AlarmDialog extends Dialog {
    Context mContext;
    CallBack dialogCallBack;
    List<MessageEvent> list;
    RecyclerView recyclerView;
    int height = (int) Math.ceil(ScreenUtils.getScreenHeight(mContext) * 0.618);


    public AlarmDialog(@NonNull Context context, List<MessageEvent> alarmList, CallBack callBack) {
        super(context, R.style.BottomAnimation);
        mContext = context;
        dialogCallBack = callBack;
        list = alarmList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alarm);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        recyclerView = findViewById(R.id.dialog_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        AlarmRecycleAdapter alarmRecycleAdapter = new AlarmRecycleAdapter(mContext,R.layout.item_alarm_dialog,list);
        recyclerView.setAdapter(alarmRecycleAdapter);


       /* if(list.size()>2)
        {
            this.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, height);
        }*/
    }

    public interface CallBack {
        void dialogCarllBack(String s);
    }


}
