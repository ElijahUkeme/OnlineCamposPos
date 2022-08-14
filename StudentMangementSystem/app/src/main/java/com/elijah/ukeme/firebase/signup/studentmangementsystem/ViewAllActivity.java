package com.elijah.ukeme.firebase.signup.studentmangementsystem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import com.elijah.ukeme.firebase.signup.studentmangementsystem.adapter.StudentAdapter;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.data.StudentDatabase;

public class ViewAllActivity extends AppCompatActivity {
    StudentDatabase studentDatabase;
    SQLiteDatabase database;
    RecyclerView recycler;
    public StudentAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);
        studentDatabase = new StudentDatabase(this);
        recycler = findViewById(R.id.recyclerView_list);
        try {

        recycler.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StudentAdapter(this,getAllStudents());
        recycler.setAdapter(mAdapter);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor getAllStudents(){
        return database.query(studentDatabase.STUDENT_TABLE,
                null,
                null,
                null,
                null,
                null,
                studentDatabase.COLUMN_REGISTRATION_NUMBER+" ASC");
    }
}