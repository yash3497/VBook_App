package com.example.vbookapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashScreenActivity extends AppCompatActivity {
    TextView logo;
    ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        logo = findViewById(R.id.logo);
        constraintLayout = findViewById(R.id.mainlayout);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,MainActivity.class);
                Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(getApplicationContext(),
                        android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
                startActivity(intent,bundle);
                finish();
            }
        },1000);
    }
}