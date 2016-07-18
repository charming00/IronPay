package com.charming.ironpay.transfer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

/**
 * Created by cm on 16/7/6.
 */
public class ConfirmActivity extends Activity implements View.OnClickListener {

    MyApplication myApplication;
    TextView accountText;
    TextView moneyText;
    TextView comfirmText;
    Button confirmButton;
    Button cancelButton;
    EditText passwordText;
    String fromType = "ironpay";
    String cardId;
    String orderType = "ironpay";
    JSONArray sellerList;
    String orderId;


    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        myApplication = (MyApplication) getApplication();
        cardId = myApplication.getNowUser();

        accountText = (TextView) findViewById(R.id.confirm_account);
        moneyText = (TextView) findViewById(R.id.confirm_money);
        comfirmText = (TextView) findViewById(R.id.confirm_type);
        confirmButton = (Button) findViewById(R.id.confirm_confirm);
        cancelButton = (Button) findViewById(R.id.confirm_cancel);
        passwordText = (EditText) findViewById(R.id.confirm_password);

        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        comfirmText.setOnClickListener(this);

        Intent intent = getIntent();

        String action = intent.getAction();

        Log.d("ConfirmActivity", "oncreate" + Intent.ACTION_VIEW);
        if (Intent.ACTION_VIEW.equals(action)) {
            orderType = "ironshop";
            Uri uri = intent.getData();
            if (uri != null) {

                try {
                    JSONObject jsonObject = new JSONObject(uri.getQueryParameter("json"));
                    sellerList = jsonObject.getJSONArray("sellerlist");
                    orderId = jsonObject.getString("transaction_id");
                    moneyText.setText(jsonObject.getString("money"));
                    accountText.setText("IronShop订单支付");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("ConfirmActivity", uri.toString());


            } else {
                Log.d("ConfirmActivity", "null");
            }
        } else {
            orderType = "ironpay";
//            Bundle bundle = intent.getBundleExtra("bundle");
//            if (bundle != null) {
            Log.d("ConfirmActivity", "money" + intent.getStringExtra("money"));
            Log.d("ConfirmActivity", "sellerlist" + intent.getStringExtra("sellerlist"));
//                Log.d("ConfirmActivity", "from" + bundle.getString("from"));
//                Log.d("ConfirmActivity", "to" + bundle.getString("to"));

            orderId = "";
            try {
                sellerList = new JSONArray(intent.getStringExtra("sellerlist"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            accountText.setText(intent.getStringExtra("to"));
            moneyText.setText(intent.getStringExtra("money"));
//            } else {
//                Log.d("ConfirmActivity", "null");
//            }
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_confirm:
//                if (orderType.equals("ironpay")) {
//                    payTransfer();
//                } else {
//                    shopTransfer();
//                }
                Log.d("ConfirmActivity", "confirm_confirm clicked");
                shopTransfer();
                break;
            case R.id.confirm_cancel:
                finish();
                break;
            case R.id.confirm_type:
                Intent intent = new Intent(ConfirmActivity.this, ChooseCardActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    private void shopTransfer() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if (passwordText.getText().toString().isEmpty()) {
            Toast.makeText(ConfirmActivity.this, "请输入支付密码", Toast.LENGTH_LONG).show();
            return;
        }
        params.add("user", myApplication.getNowUser());
        params.add("from", cardId);
        params.add("fromtype", fromType);
        params.add("to", sellerList.toString());
        params.add("money", moneyText.getText().toString());
        params.add("transaction_id", orderId);

//        params.add("totype", toType);
//        params.add("money", moneyText.getText().toString());
        try {
            params.add("password", MD5.getMD5(passwordText.getText().toString()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Log.d("ConfirmActivity", "shopTransfer params " + params.toString());

        client.post(MyApplication.getServer() + "/shopTranfer", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                        String response = new String(bytes);
                        Log.e("debug", "state:" + response);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            String status = object.getString("status");
                            if (status.equals("error")) {
                                Toast.makeText(ConfirmActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                            } else if (status.equals("notenough")) {
                                Toast.makeText(ConfirmActivity.this, "余额不足", Toast.LENGTH_LONG).show();
                            } else if (status.equals("success")) {
                                showShopMessage();
//                                TransferAccountActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(ConfirmActivity.this, "网络错误，请检查网络设置", Toast.LENGTH_LONG).show();
                        Log.d("onFailure", throwable.toString());
                    }
                }
        );
    }

    void payTransfer() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        if (passwordText.getText().toString().isEmpty()) {
            Toast.makeText(ConfirmActivity.this, "请输入支付密码", Toast.LENGTH_LONG).show();
            return;
        }
        params.add("user", myApplication.getNowUser());
        params.add("from", cardId);
        params.add("fromtype", fromType);
        params.add("to", accountText.getText().toString());

//        params.add("totype", toType);
        params.add("money", moneyText.getText().toString());
        try {
            params.add("password", MD5.getMD5(passwordText.getText().toString()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Log.d("ConfirmActivity", "payTransfer params " + params.toString());


        client.post(MyApplication.getServer() + "/payTranfer", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                        String response = new String(bytes);
                        Log.e("debug", "state:" + response);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            String status = object.getString("status");
                            if (status.equals("error")) {
                                Toast.makeText(ConfirmActivity.this, "密码错误", Toast.LENGTH_LONG).show();
                            } else if (status.equals("notenough")) {
                                Toast.makeText(ConfirmActivity.this, "余额不足", Toast.LENGTH_LONG).show();
                            } else if (status.equals("success")) {
                                showPayMessage();
//                                TransferAccountActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(ConfirmActivity.this, "网络错误，请检查网络设置", Toast.LENGTH_LONG).show();
                        Log.d("onFailure", throwable.toString());
                    }
                }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            cardId = data.getStringExtra("cardId");
            fromType = "ironbank";
            Log.d("ConfirmActivity", cardId);
            comfirmText.setText(cardId);
        }
    }

    public void showPayMessage() {
        new AlertDialog.Builder(this)
                .setTitle("系统消息")
                .setMessage("支付成功")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ConfirmActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }).show();
    }

    public void showShopMessage() {
        new AlertDialog.Builder(this)
                .setTitle("系统消息")
                .setMessage("支付成功")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ConfirmActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                }).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("ConfirmActivity", "onresume");
    }
}
