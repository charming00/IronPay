package com.charming.ironpay.card;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.charming.ironpay.MyApplication;
import com.charming.ironpay.R;
import com.charming.ironpay.account.MD5;
import com.charming.ironpay.transfer.MainActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cm on 16/7/7.
 */

public class CardListActivity extends Activity implements View.OnClickListener {

    ListView listView;
    private CardlistAdapter cardlistAdapter;
    Button bindCardButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        listView = (ListView) findViewById(R.id.list_view);
        bindCardButton = (Button) findViewById(R.id.bind_card);

        bindCardButton.setOnClickListener(this);
    }

    private void getData() {
        MyApplication myApplication = (MyApplication) getApplication();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("user", myApplication.getNowUser());
        client.post(MyApplication.getServer() + "/getCardList", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes) {
                        String response = new String(bytes);
                        Log.e("debug", "state:" + response);
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response);
                            String status = object.getString("status");
                            if (status.equals("error")) {
                                Toast.makeText(CardListActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                            } else if (status.equals("success")) {
                                JSONArray jsonArray = object.getJSONArray("cardList");
                                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

                                for (int j = 0; j < jsonArray.length(); j++) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("title", jsonArray.getJSONObject(j).getString("cardId"));
                                    map.put("img", R.drawable.app_creditcard);
                                    list.add(map);
                                }
                                cardlistAdapter = new CardlistAdapter(CardListActivity.this, list);
                                listView.setAdapter(cardlistAdapter);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, byte[] bytes, Throwable throwable) {
                        Toast.makeText(CardListActivity.this, "网络错误，请检查网络设置", Toast.LENGTH_LONG).show();
                        Log.d("onFailure", throwable.toString());
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind_card:
                Intent intent = new Intent(CardListActivity.this, BindCardActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}