package com.elijah.ukeme.firebase.signup.studentmangementsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elijah.ukeme.firebase.signup.studentmangementsystem.data.Student;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.data.StudentDatabase;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.interfaces.ClearAble;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.interfaces.ShowAbleMessage;

public class RegisterActivity extends AppCompatActivity implements ClearAble, ShowAbleMessage {
    EditText name, reg, attendance, date, mark;
    Button registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.editText_name);
        reg = findViewById(R.id.editText_regNum);
        attendance = findViewById(R.id.editText_attendance);
        date = findViewById(R.id.editText_date);
        mark = findViewById(R.id.editText_mark);
        registerButton = findViewById(R.id.button_register);

        registerButton.setOnClickListener(v -> {
            if (name.getText().toString().isEmpty() || reg.getText().toString().isEmpty() || attendance.getText().toString()
                    .isEmpty() || date.getText().toString().isEmpty() || mark.getText().toString().isEmpty()) {
                showMessage("Error", "Please fill all values");
                return;
            } if (!(name.getText().toString().isEmpty() || reg.getText().toString().isEmpty() || attendance.getText().toString()
                    .isEmpty() || date.getText().toString().isEmpty() || mark.getText().toString().isEmpty())) {

                Student student = new Student(name.getText().toString(), reg.getText().toString(), attendance.getText().toString(),
                        date.getText().toString(), Integer.parseInt(mark.getText().toString()));

                StudentDatabase studentDatabase = new StudentDatabase(this);
                studentDatabase.addStudent(student);
                Toast.makeText(this, "Successfully registered a student", Toast.LENGTH_LONG).show();
                clearText();
            } else {
                Toast.makeText(this, "Error in registration", Toast.LENGTH_LONG).show();
            }

        });
    }

    @Override
    public void clearText() {
        name.getText().clear();
        reg.getText().clear();
        attendance.getText().clear();
        date.getText().clear();
        mark.getText().clear();
    }

    @Override
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}