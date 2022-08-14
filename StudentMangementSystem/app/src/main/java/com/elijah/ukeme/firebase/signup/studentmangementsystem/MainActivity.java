package com.elijah.ukeme.firebase.signup.studentmangementsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog.Builder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button login,register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = findViewById(R.id.login_button);
        register = findViewById(R.id.register_button);

        login.setOnClickListener(v -> {
            Intent intoLogin = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intoLogin);

        });

        register.setOnClickListener(v -> {
            try {


            Intent intoRegister = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intoRegister);
            }catch (Exception e){
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
            }

        });
    }
}