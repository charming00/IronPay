package com.charming.ironpay.transfer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.charming.ironpay.MyApplication;
import com.charming.ironpay.QRcode.CaptureActivity;
import com.charming.ironpay.R;
import com.charming.ironpay.account.ChangePayPasswordActivity;
import com.charming.ironpay.account.LoginActivity;
import com.charming.ironpay.account.MD5;
import com.charming.ironpay.card.BindCardActivity;
import com.charming.ironpay.card.CardListActivity;
import com.charming.ironpay.gridview.MyGridAdapter;
import com.charming.ironpay.gridview.MyGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

/**
 * Created by cm on 16/7/5.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private MyGridView gridview;
    TextView balanceText;
    Button exitButton;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        balanceText = (TextView) findViewById(R.id.main_balance);
        exitButton = (Button) findViewById(R.id.main_exit);
        exitButton.setOnClickListener(this);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBalance();
    }

    void updateBalance() {
        MyApplication myApplication = (MyApplication)getApplication();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("id", myApplication.getNowUser());
        client.post(MyApplication.getServer() + "/getBalance", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                        String response = new String(bytes);
                        Log.e("debug", "state:" + response);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            String status = object.getString("status");
//                            balanceText.setText(object.getString("balance"));

                            if (status.equals("error")) {
                                Toast.makeText(MainActivity.this, "出现错误，请稍后重试", Toast.LENGTH_LONG).show();
                            } else if (status.equals("success")) {
                                balanceText.setText(object.getString("balance"));
//                                initLoginState();
//                                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                                startActivity(intent);
//                                MainActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(MainActivity.this, "网络错误，请检查网络设置", Toast.LENGTH_LONG).show();
                        Log.d("onFailure", throwable.toString());
//                        balanceText.setText("1234.00");
                    }
                }
        );
    }

    private void initView() {
        gridview = (MyGridView) findViewById(R.id.gridview);
        gridview.setAdapter(new MyGridAdapter(this));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        intent = new Intent(MainActivity.this, TransferAccountActivity.class);
                        startActivity(intent);
//                        overridePendingTransition(R.anim.open_in,android.R.anim.fade_out);
                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, BindCardActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(MainActivity.this, CardListActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent = new Intent(MainActivity.this, CaptureActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent = new Intent(MainActivity.this, BalanceOutActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent = new Intent(MainActivity.this, BalanceInActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent = new Intent(MainActivity.this, ChangePayPasswordActivity.class);
                        startActivity(intent);
                        break;

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_exit:
                exit();

        }
    }

    private void exit() {
        MyApplication myApplication  = (MyApplication)getApplication();
        myApplication.setNowUser("");
        SharedPreferences sharedPreferences = getSharedPreferences(MyApplication.PREFERENCE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(MyApplication.NOW_USER_NAME, "");
        editor.apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
