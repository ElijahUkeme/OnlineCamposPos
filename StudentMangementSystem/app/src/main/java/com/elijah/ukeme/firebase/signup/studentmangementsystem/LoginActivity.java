package com.elijah.ukeme.firebase.signup.studentmangementsystem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elijah.ukeme.firebase.signup.studentmangementsystem.data.Student;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.data.StudentDatabase;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.interfaces.ClearAble;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.interfaces.ShowAbleMessage;

public class LoginActivity extends AppCompatActivity implements ShowAbleMessage, ClearAble {
    EditText regnum;
    Button login;
    StudentDatabase studentDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        regnum =findViewById(R.id.editText_login);
        login=findViewById(R.id.button_login);
        studentDatabase = new StudentDatabase(this);
        String registration_number = regnum.getText().toString();
        login.setOnClickListener(v -> {


            Cursor cursor = studentDatabase.giveMedata();
            if (cursor.getCount()==0){
                showMessage("Error","No record found");
                return;
            }
            while (cursor.moveToNext()){
                cursor = studentDatabase.login(registration_number);
                Intent intent = new Intent(this,DisplayActivity.class);
                startActivity(intent);
            }
            showMessage("Error","Registration Number not found");
            return;
        });
    }

    @Override
    public void clearText() {
        regnum.getText().clear();
    }

    @Override
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }
    public void verifyDetails(){

            }
        }

