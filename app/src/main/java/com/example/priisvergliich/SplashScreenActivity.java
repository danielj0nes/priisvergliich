package com.example.priisvergliich;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(() -> {
            /* Create an Intent that will start the MainActivity. */
            Intent mainIntent = new Intent(SplashScreenActivity.this,
                    MainActivity.class);
            SplashScreenActivity.this.startActivity(mainIntent);
            SplashScreenActivity.this.finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}