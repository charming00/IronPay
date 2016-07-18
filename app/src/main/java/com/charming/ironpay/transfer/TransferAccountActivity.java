package com.charming.ironpay.transfer;

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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cm on 16/7/6.
 */
public class TransferAccountActivity extends Activity implements View.OnClickListener {

    Intent intent;
    EditText accountText;
    Button confirmButton;
    Button cancelButton;



    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_account);
        accountText = (EditText) findViewById(R.id.transfer_account);
        confirmButton = (Button) findViewById(R.id.transfer_confirm);
        cancelButton = (Button) findViewById(R.id.transfer_cancel);
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        MyApplication myApplication = (MyApplication)getApplication();
        switch (v.getId()) {
            case R.id.transfer_confirm:
                checkAccount();
//                Log.d("TransferAccountActivity", "transfer_confirm is clicked");
//                intent = new Intent(TransferAccountActivity.this,ConfirmActivity.class);
//                Bundle bundle = new Bundle();
////                if (myApplication == null) {
////                    Log.d("TransferAccountActivity", "null");
////                } else {
////                    Log.d("TransferAccountActivity", "not null");
////                }
//                bundle.putString("from", myApplication.getNowUser());
//                bundle.putString("to", accountText.getText().toString());
//                intent.putExtra("bundle",bundle);
//                startActivity(intent);
                break;
            case R.id.transfer_cancel:
                Log.d("TransferAccountActivity", "transfer_cancel is clicked");
                finish();
                break;
        }
    }

    void checkAccount() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if (accountText.getText().toString().isEmpty()) {
            Toast.makeText(TransferAccountActivity.this, "请输入对方账号", Toast.LENGTH_LONG).show();
            return;
        }
        params.add("phone", accountText.getText().toString());

        client.post(MyApplication.getServer() + "/checkAccount", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                        String response = new String(bytes);
                        Log.e("debug", "state:" + response);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            String status = object.getString("status");
                            if (status.equals("error")) {
                                Toast.makeText(TransferAccountActivity.this, "账户不存在", Toast.LENGTH_LONG).show();
                            } else if (status.equals("success")) {
                                Intent intent = new Intent(TransferAccountActivity.this, TransferMoneyActivity.class);
                                intent.putExtra("account", accountText.getText().toString());
                                startActivity(intent);
//                                TransferAccountActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(TransferAccountActivity.this, "网络错误，请检查网络设置", Toast.LENGTH_LONG).show();
                        Log.d("onFailure", throwable.toString());
                    }
                }
        );
    }
}
