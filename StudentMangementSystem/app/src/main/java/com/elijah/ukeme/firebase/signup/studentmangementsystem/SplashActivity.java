package com.elijah.ukeme.firebase.signup.studentmangementsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    ImageView splashImage;
    private static int splashTimeOut =2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashImage = findViewById(R.id.imageView_splash);
/*
        new Handler().postDelayed((){
            Intent intoMain = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intoMain);
            finish();
        },splashTimeOut);
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.)*/
    }
}