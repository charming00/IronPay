package com.charming.ironpay.card;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.charming.ironpay.MyApplication;
import com.charming.ironpay.R;
import com.charming.ironpay.account.MD5;
import com.charming.ironpay.transfer.MainActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

/**
 * Created by cm on 16/7/8.
 */
public class BindCardActivity extends Activity implements View.OnClickListener {

    Button confirmButton;
    Button cancelButton;
    EditText passwordText;
    EditText accountText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_card);

        confirmButton = (Button) findViewById(R.id.bind_confirm);
        cancelButton = (Button) findViewById(R.id.bind_cancel);
        passwordText = (EditText) findViewById(R.id.bind_password);
        accountText = (EditText) findViewById(R.id.bind_account);

        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind_confirm:
                bindCard();
                break;
            case R.id.bind_cancel:
                finish();
                break;
        }
    }

    void bindCard() {
        MyApplication myApplication = (MyApplication) getApplication();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if (accountText.getText().toString().isEmpty()) {
            Toast.makeText(BindCardActivity.this, "请输入银行卡号", Toast.LENGTH_LONG).show();
            return;
        }

        if (passwordText.getText().toString().isEmpty()) {
            Toast.makeText(BindCardActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
            return;
        }

        params.add("payAccount", myApplication.getNowUser());
        params.add("bankAccount", accountText.getText().toString());
        try {
            params.add("password", MD5.getMD5(passwordText.getText().toString()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        client.post(MyApplication.getServer() + "/bindCard", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                        String response = new String(bytes);
                        Log.e("debug", "state:" + response);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            String status = object.getString("status");
                            if (status.equals("error")) {
                                Toast.makeText(BindCardActivity.this, "卡号或密码错误", Toast.LENGTH_LONG).show();
                            } else if (status.equals("exist")) {
                                Toast.makeText(BindCardActivity.this, "已绑定此银行卡", Toast.LENGTH_LONG).show();
                            } else if (status.equals("success")) {
                                showMessage();
//                                TransferAccountActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(BindCardActivity.this, "网络错误，请检查网络设置", Toast.LENGTH_LONG).show();
                        Log.d("onFailure", throwable.toString());
                    }
                }
        );
    }

    public void showMessage() {
        new AlertDialog.Builder(this)
                .setTitle("系统消息")
                .setMessage("绑定银行卡成功")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                }).show();
    }
}
