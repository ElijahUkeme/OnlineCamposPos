package com.elijah.ukeme.firebase.signup.studentmangementsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.elijah.ukeme.firebase.signup.studentmangementsystem.data.Student;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.data.StudentDatabase;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.interfaces.ClearAble;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.interfaces.ShowAbleMessage;

import java.util.ArrayList;
import java.util.List;

public class ViewActivity extends AppCompatActivity implements ShowAbleMessage, ClearAble {
EditText regNum;
Button view;
ListView listView;
List<Student> list;
    StudentDatabase studentDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        regNum=findViewById(R.id.editText_view);
        view=findViewById(R.id.button_viewOne);
        listView = findViewById(R.id.list_view_item);
        studentDatabase = new StudentDatabase(this);
            view.setOnClickListener(v -> {
                if(regNum.getText().toString().trim().isEmpty()){
                    showMessage("Error","Please input your regNum");
                    return;
                }
               /* if(!regNum.getText().toString().trim().isEmpty()) {
                    Student student = new Student();
                    student.setRegNum(regNum.getText().toString());
                    student.getRegNum();
                    StudentDatabase studentDatabase = new StudentDatabase(this);
                    if (student.getRegNum().equalsIgnoreCase(studentDatabase.COLUMN_REGISTRATION_NUMBER)){
                        studentDatabase.viewOne(student);
                        Toast.makeText(this,studentDatabase.toString(),Toast.LENGTH_LONG).show();
                    }

                }else {
                    showMessage("Error","Invalid regNum");
                }
//                StudentDatabase studentDatabase = new StudentDatabase(this);
//                list = studentDatabase.getEveryOne();
//                Toast.makeText(this, list.toString(), Toast.LENGTH_SHORT).show();
                 studentDatabase = new StudentDatabase(ViewActivity.this);
                List<Student> allStudent = studentDatabase.getEveryOne();
                Toast.makeText(this, allStudent.toString(), Toast.LENGTH_SHORT).show();*/

              showData();
              return;
            });
    }

    public void showData(){
        try {
            list = new ArrayList<>();
        Cursor cursor = studentDatabase.giveMedata();
       // List<Student> data = database.getEveryOne();
        if (cursor.getCount()==0){
            showMessage("Error","No record found");
            return;

        }
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()){
            buffer.append("Name: "+cursor.getString(cursor.getColumnIndex(studentDatabase.COLUMN_STUDENT_NAME))+"\n");
            buffer.append("Registration Number: "+cursor.getString(cursor.getColumnIndex(studentDatabase.COLUMN_REGISTRATION_NUMBER))+"\n");
            buffer.append("Score: "+cursor.getInt(cursor.getColumnIndex(studentDatabase.COLUMN_STUDENT_MARK))+"\n");
            buffer.append("Attendance status: "+cursor.getString(cursor.getColumnIndex(studentDatabase.COLUMN_STUDENT_ATTENDANCE))+"\n");
            buffer.append("Date: "+cursor.getString(cursor.getColumnIndex(studentDatabase.COLUMN_ATTENDANCE_DATE))+"\n");
            showMessage("All the student found are: ",buffer.toString());

        }}catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }}

    @Override
    public void clearText() {
        regNum.getText().clear();
    }

    @Override
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

}