package com.byteshaft.hafizconstructionworks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.byteshaft.requests.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class AddItemActivity extends AppCompatActivity {

    private int mQuarterId;
    private EditText mItemName, mItemQuantity, mItemPrice;
    private Button mAddButton;

    private String name;
    private String quantity;
    private String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        mQuarterId = getIntent().getIntExtra("quarter_id", -1);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mItemName = findViewById(R.id.et_item_name);
        mItemQuantity = findViewById(R.id.et_item_quantity);
        mItemPrice = findViewById(R.id.et_item_price);
        mAddButton = findViewById(R.id.add_item);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = mItemName.getText().toString();
                quantity = mItemQuantity.getText().toString();
                price = mItemPrice.getText().toString();
                addItemDetails(mQuarterId, name, quantity, price);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void addItemDetails(int qurterId, String itemName, String itemQuantity, String itemPrice) {
        HttpRequest request = new HttpRequest(getApplicationContext());
        request.setOnReadyStateChangeListener(new HttpRequest.OnReadyStateChangeListener() {
            @Override
            public void onReadyStateChange(HttpRequest httpRequest, int i) {
                switch (i) {
                    case HttpRequest.STATE_DONE:
                        switch (httpRequest.getStatus()) {
                            case HttpURLConnection.HTTP_CREATED:
                                Toast.makeText(AddItemActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                finish();
                        }
                }
            }
        });
        request.open("POST", String.format("%sadd_items/%s", AppGlobals.SERVER_IP, qurterId));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("item_name", itemName);
            jsonObject.put("price", itemPrice);
            jsonObject.put("quantity", itemQuantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request.send(jsonObject.toString());
    }
}
