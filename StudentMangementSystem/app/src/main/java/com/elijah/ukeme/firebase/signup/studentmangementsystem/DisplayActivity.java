package com.elijah.ukeme.firebase.signup.studentmangementsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.IResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.elijah.ukeme.firebase.signup.studentmangementsystem.data.Student;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.data.StudentDatabase;

import java.util.List;

public class DisplayActivity extends AppCompatActivity {
    Button update,delete,view,viewAll,active;
    private List<Student> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        update= findViewById(R.id.button_update);
        delete=findViewById(R.id.button_delete);
        view=findViewById(R.id.button_view);
        viewAll=findViewById(R.id.button_viewAll);
        active=findViewById(R.id.button_active_students);

        update.setOnClickListener(v -> {
            Intent intoUpdate = new Intent(DisplayActivity.this,UpdateActivity.class);
            startActivity(intoUpdate);

        });
        delete.setOnClickListener(v -> {
            Intent intoDelete = new Intent(DisplayActivity.this,DeleteActivity.class);
            startActivity(intoDelete);
        });
        view.setOnClickListener(v -> {
            Intent intoView = new Intent(DisplayActivity.this,ViewActivity.class);
            startActivity(intoView);
        });
        viewAll.setOnClickListener(v -> {
            Intent intent = new Intent(this,ViewAllActivity.class);
            startActivity(intent);
        });

        active.setOnClickListener(v -> {

        });
    }

}