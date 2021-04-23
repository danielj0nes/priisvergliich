package com.danielj.priisvergliich.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.danielj.priisvergliich.R;
import com.r0adkll.slidr.Slidr;

public class SavedComparisonsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_comparisons);
        Slidr.attach(this);
    }
}