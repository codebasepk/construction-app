package com.byteshaft.hafizconstructionworks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class QuarterDetail extends AppCompatActivity {
    private int mQuarterId;
    private String mQuarterName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);
        mQuarterId = getIntent().getIntExtra("q_id", -1);
        mQuarterName = getIntent().getStringExtra("q_name");
        setTitle(mQuarterName);
    }
}
