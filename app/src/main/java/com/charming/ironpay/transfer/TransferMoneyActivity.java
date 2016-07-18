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
import android.widget.Toast;

import com.charming.ironpay.MyApplication;
import com.charming.ironpay.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cm on 16/7/6.
 */
public class TransferMoneyActivity extends Activity implements View.OnClickListener, TextWatcher {

    Intent intent;
    EditText moneyText;
    Button confirmButton;
    Button cancelButton;
    String toAccount;


    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_money);
        moneyText = (EditText) findViewById(R.id.transfer_money);
        moneyText.addTextChangedListener(this);
        confirmButton = (Button) findViewById(R.id.transfer_confirm);
        cancelButton = (Button) findViewById(R.id.transfer_cancel);
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        MyApplication myApplication = (MyApplication) getApplication();
        switch (v.getId()) {
            case R.id.transfer_confirm:
                Log.d("TransferAccountActivity", "transfer_confirm is clicked");

                if (moneyText.getText().toString().isEmpty()) {
                    Toast.makeText(TransferMoneyActivity.this, "请输入转账金额", Toast.LENGTH_LONG).show();
                    break;
                }

                if (Double.valueOf(moneyText.getText().toString()) > 100000) {
                    Toast.makeText(TransferMoneyActivity.this, "单次最多转账金额为10万元", Toast.LENGTH_LONG).show();
                    break;
                }

                try {
                    JSONArray sellerList = new JSONArray()
                            .put(new JSONObject()
                                    .put("seller", getIntent().getStringExtra("account"))
                                    .put("money", moneyText.getText().toString()));
                    Log.d("", "sellerlist " + sellerList.toString());
                    intent = new Intent(TransferMoneyActivity.this, ConfirmActivity.class);
                    Bundle bundle = new Bundle();

                    intent.putExtra("money", moneyText.getText().toString());
                    intent.putExtra("sellerlist", sellerList.toString());
                    intent.putExtra("to", getIntent().getStringExtra("account"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.transfer_cancel:
                Log.d("TransferAccountActivity", "transfer_cancel is clicked");
                finish();
                break;
        }
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
}
