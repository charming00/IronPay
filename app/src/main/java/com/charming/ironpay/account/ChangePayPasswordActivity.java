package com.charming.ironpay.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.charming.ironpay.MyApplication;
import com.charming.ironpay.R;
import com.charming.ironpay.transfer.MainActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

/**
 * Created by cm on 16/7/14.
 */
public class ChangePayPasswordActivity extends Activity implements View.OnClickListener {
    EditText oldPassword;
    EditText firstPassword;
    EditText secondPassword;
    Button confirmButton;
    Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pay_password);
        oldPassword = (EditText) findViewById(R.id.old_password);
        firstPassword = (EditText) findViewById(R.id.first_password);
        secondPassword = (EditText) findViewById(R.id.second_password);
        confirmButton = (Button) findViewById(R.id.change_confirm);
        cancelButton = (Button) findViewById(R.id.change_cancel);

        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_confirm:
                changePassword();
                break;
            case R.id.change_cancel:
                finish();
                break;
        }
    }

    private void changePassword() {
        if (oldPassword.getText().toString().isEmpty()) {
            Toast.makeText(ChangePayPasswordActivity.this,"请输入旧密码",Toast.LENGTH_LONG).show();
            return;
        }
        if (firstPassword.getText().toString().isEmpty()) {
            Toast.makeText(ChangePayPasswordActivity.this,"请输入新密码",Toast.LENGTH_LONG).show();
            return;
        }
        if (secondPassword.getText().toString().isEmpty()) {
            Toast.makeText(ChangePayPasswordActivity.this,"请再次输入新密码",Toast.LENGTH_LONG).show();
            return;
        }
        if (!secondPassword.getText().toString().equals(firstPassword.getText().toString())) {
            Toast.makeText(ChangePayPasswordActivity.this,"两次密码不一致",Toast.LENGTH_LONG).show();
            return;
        }

        if (firstPassword.getText().toString().length() > 16) {
            Toast.makeText(ChangePayPasswordActivity.this, "密码位数不得大于16位", Toast.LENGTH_LONG).show();
            return;
        }
        if (firstPassword.getText().toString().length() < 6) {
            Toast.makeText(ChangePayPasswordActivity.this, "密码位数不得小于6位", Toast.LENGTH_LONG).show();
            return;
        }

        sendChange();
    }

    private void sendChange() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        MyApplication myApplication = (MyApplication)getApplication();

        try {
            params.add("oldpassword", MD5.getMD5(oldPassword.getText().toString()));
            params.add("newpassword", MD5.getMD5(firstPassword.getText().toString()));
            params.add("user", myApplication.getNowUser());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        client.post(MyApplication.getServer() + "/changePayPassword", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                        String response = new String(bytes);
                        Log.e("debug", "state:" + response);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            String status = object.getString("status");
                            if (status.equals("error")) {
                                Toast.makeText(ChangePayPasswordActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                            } else if (status.equals("success")) {
                                Toast.makeText(ChangePayPasswordActivity.this, "修改成功", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(ChangePayPasswordActivity.this, MainActivity.class);
                                startActivity(intent);
                                ChangePayPasswordActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(ChangePayPasswordActivity.this, "网络错误，请检查网络设置", Toast.LENGTH_LONG).show();
                        Log.d("onFailure", throwable.toString());
                    }
                }
        );
    }
}
