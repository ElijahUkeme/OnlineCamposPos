package com.elijah.ukeme.hymn.book;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreenActivity extends AppCompatActivity {
    TextView title;
    ImageView logo;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        title= findViewById(R.id.textView);
        logo= findViewById(R.id.imageView);
        //lottieAnimationView = findViewById(R.id.lottie_animation);

        logo.animate().translationYBy(-1800).setDuration(6000).setStartDelay(500);
        title.animate().translationY(-2000).setDuration(6000).setStartDelay(2000);
        //lottieAnimationView.animate().translationY(1400).setDuration(4000).setStartDelay(5000);

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(7000);
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent intoMain = new Intent(SplashScreenActivity.this,HomeActivity.class);
                    startActivity(intoMain);

                }
            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}