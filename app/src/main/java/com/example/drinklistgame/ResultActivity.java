package com.example.drinklistgame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ResultActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resu);
        //Intent intent = getIntent();
        //Bundle b = getIntent().getExtras();
    }
    public void returnToMain(View view){
        Intent i = new Intent(this, MainActivity.class);
        Bundle b = new Bundle();

        //Intent myIntent = new Intent(RollingBallPanel.this.getContext().getApplicationContext(), ResultScreen.class);
        i.putExtras(b);

        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivity(i);
    }

}
