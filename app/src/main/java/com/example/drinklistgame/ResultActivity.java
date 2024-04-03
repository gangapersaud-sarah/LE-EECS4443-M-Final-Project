package com.example.drinklistgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends Activity {
    private TextView  tv1to1,tv1toM,tvMto1,tvMtoM;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resu);
        intent = getIntent();
        //Bundle b = getIntent().getExtras();
        tv1to1 = findViewById(R.id.tv1to1Average);
        tv1toM = findViewById(R.id. tv1toMAverage);
        tvMto1 = findViewById(R.id.tvMto1Average);
        tvMtoM = findViewById(R.id.tvMtoMAverage);
        tv1to1.setText(intent.getStringExtra("121"));
        tv1toM.setText(intent.getStringExtra("12m"));
        tvMto1.setText(intent.getStringExtra("m21"));
        tvMtoM.setText(intent.getStringExtra("m2m"));
    }
    public void returnToMain(View view){
        Intent i = new Intent(this, MainActivity.class);
       String value =  intent.getStringExtra("interactionType");
        i.putExtra("interactionType", value);
        this.startActivity(i);
    }

}
