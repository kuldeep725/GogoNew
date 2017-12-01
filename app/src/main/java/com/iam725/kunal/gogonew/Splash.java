package com.iam725.kunal.gogonew;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        int SPLASH_SCREEN_DELAY = 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Executed after timer is finished (Opens MainActivity)
                Intent intent = new Intent(Splash.this, MapsActivity.class);
                startActivity(intent);
                // Kills this Activity
                finish();
            }
        }, SPLASH_SCREEN_DELAY);
    }
}
