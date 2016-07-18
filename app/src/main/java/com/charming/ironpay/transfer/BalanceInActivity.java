package com.charming.ironpay.transfer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.charming.ironpay.MyApplication;
import com.charming.ironpay.R;
import com.charming.ironpay.account.MD5;
import com.charming.ironpay.card.ChooseCardActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

/**
 * Created by cm on 16/7/10.
 */
public class BalanceInActivity extends Activity implements View.OnClickListener, TextWatcher {
    EditText moneyText;
    TextView typeText;
    EditText passwordText;
    Button confirmButton;
    Button cancelButton;
    String fromType = "";
    String toType = "ironpay";
    String cardId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_in);
        moneyText = (EditText) findViewById(R.id.in_money);
        typeText = (TextView) findViewById(R.id.in_type);
        passwordText = (EditText) findViewById(R.id.in_password);
        confirmButton = (Button) findViewById(R.id.in_confirm);
        cancelButton = (Button) findViewById(R.id.in_cancel);

        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        typeText.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().contains(".")) {
            if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                s = s.toString().subSequence(0,
                        s.toString().indexOf(".") + 3);
                moneyText.setText(s);
                moneyText.setSelection(s.length());
            }
        }
        if (s.toString().trim().substring(0).equals(".")) {
            s = "0" + s;
            moneyText.setText(s);
            moneyText.setSelection(2);
        }

        if (s.toString().startsWith("0")
                && s.toString().trim().length() > 1) {
            if (!s.toString().substring(1, 2).equals(".")) {
                moneyText.setText(s.subSequence(0, 1));
                moneyText.setSelection(1);
                return;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.in_confirm:
                balanceIn();
                break;
            case R.id.in_cancel:
                finish();
                break;
            case R.id.in_type:
                Intent intent = new Intent(BalanceInActivity.this, ChooseCardActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            cardId = data.getStringExtra("cardId");
            fromType = "ironbank";
            Log.d("ConfirmActivity", cardId);
            typeText.setText(cardId);
        }
    }

    void balanceIn() {
        MyApplication myApplication = (MyApplication) getApplication();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if (moneyText.getText().toString().isEmpty()) {
            Toast.makeText(BalanceInActivity.this, "请输入充值金额", Toast.LENGTH_LONG).show();
            return;
        }

        if (cardId.isEmpty()) {
            Toast.makeText(BalanceInActivity.this, "请选择支付方式", Toast.LENGTH_LONG).show();
            return;
        }

        if (passwordText.getText().toString().isEmpty()) {
            Toast.makeText(BalanceInActivity.this, "请输入支付密码", Toast.LENGTH_LONG).show();
            return;
        }



        params.add("from", cardId);
        params.add("to", myApplication.getNowUser());
        params.add("fromtype", fromType);
        params.add("totype", toType);
        params.add("money", moneyText.getText().toString());
        try {
            params.add("password", MD5.getMD5(passwordText.getText().toString()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Log.d("BalanceInActivity",params.toString());

        client.post(MyApplication.getServer() + "/balanceIn", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                        String response = new String(bytes);
                        Log.e("debug", "state:" + response);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            String status = object.getString("status");
                            if (status.equals("error")) {
                                Toast.makeText(BalanceInActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                            } else if (status.equals("notenough")) {
                                Toast.makeText(BalanceInActivity.this, "余额不足", Toast.LENGTH_LONG).show();
                            } else if (status.equals("success")) {
                                Intent intent = new Intent(BalanceInActivity.this, MainActivity.class);
                                startActivity(intent);
//                                TransferAccountActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(BalanceInActivity.this, "网络错误，请检查网络设置", Toast.LENGTH_LONG).show();
                        Log.d("onFailure", throwable.toString());
                    }
                }
        );
    }
}
