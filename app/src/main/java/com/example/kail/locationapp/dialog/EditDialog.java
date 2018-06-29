package com.example.kail.locationapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kail.locationapp.R;
import com.example.kail.locationapp.util.BaseUtil;
import com.example.kail.locationapp.util.SPUtils;

/**
 * Created by fan on 2018/4/15.
 */

public class EditDialog extends Dialog {
    Context mContext;
    EditText edit_main;
    TextView txt_btn_cancel;
    TextView txt_btn_yes;
    String url ="";
    CallBack dialogCallBack;

    public EditDialog(@NonNull Context context,String url,CallBack callBack) {
        super(context, R.style.MyDialog);
        mContext = context;
        dialogCallBack = callBack;
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_dialog);
        edit_main = findViewById(R.id.edit_main);
        txt_btn_cancel = findViewById(R.id.txt_btn_cancel);
        txt_btn_yes = findViewById(R.id.txt_btn_yes);
        edit_main.setText(url);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.txt_btn_cancel:
                        dismiss();
                        break;

                    case R.id.txt_btn_yes:
                        if (!TextUtils.isEmpty(edit_main.getText().toString().trim())) {
                            SPUtils.put(mContext, "serviceIP", edit_main.getText().toString().trim());
                            dialogCallBack.dialogCarllBack(edit_main.getText().toString().trim());
                            dismiss();
                        }else {
                            BaseUtil.showToast(mContext,"请输入服务器ip");
                        }

                        break;
                        default:
                            dismiss();
                            break;
                }
            }
        };
        txt_btn_cancel.setOnClickListener(clickListener);
        txt_btn_yes.setOnClickListener(clickListener);

    }
   public interface CallBack{
       void  dialogCarllBack(String s);
    }

}
