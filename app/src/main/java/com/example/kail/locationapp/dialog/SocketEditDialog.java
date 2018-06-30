package com.example.kail.locationapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.kail.locationapp.R;
import com.example.kail.locationapp.util.BaseUtil;
import com.example.kail.locationapp.util.SPUtils;

/**
 * Created by fan on 2018/4/15.
 */

public class SocketEditDialog extends Dialog {
    Context mContext;
    EditText edit_main;
    EditText edit_port;
    TextView txt_btn_cancel;
    TextView txt_btn_yes;
    String ip ="";
    String port ="";
    CallBack dialogCallBack;

    public SocketEditDialog(@NonNull Context context, String ip,String port, CallBack callBack) {
        super(context, R.style.MyDialog);
        mContext = context;
        dialogCallBack = callBack;
        this.ip = ip;
        this.port = port;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_dialog_socket);
        edit_main = findViewById(R.id.edit_main);
        edit_port = findViewById(R.id.edit_port);
        txt_btn_cancel = findViewById(R.id.txt_btn_cancel);
        txt_btn_yes = findViewById(R.id.txt_btn_yes);
        edit_main.setText(ip);
        edit_port.setText(port);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.txt_btn_cancel:
                        dismiss();
                        break;

                    case R.id.txt_btn_yes:
                        if (TextUtils.isEmpty(edit_main.getText().toString().trim())||TextUtils.isEmpty(edit_port.getText().toString().trim())) {
                            BaseUtil.showToast(mContext,"请输入工单ip和端口");

                        }else {
                            SPUtils.put(mContext, "socketIp", edit_main.getText().toString().trim());
                            SPUtils.put(mContext,"socketPort",edit_port.getText().toString().trim());
                            dialogCallBack.dialogCarllBack(edit_main.getText().toString().trim(),edit_port.getText().toString().trim());
                            dismiss();
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
       void  dialogCarllBack(String ip,String port);
    }

}
