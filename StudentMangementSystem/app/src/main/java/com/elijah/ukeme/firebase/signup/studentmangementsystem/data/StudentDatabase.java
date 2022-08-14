package com.elijah.ukeme.firebase.signup.studentmangementsystem.data;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.elijah.ukeme.firebase.signup.studentmangementsystem.ViewAllActivity;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.data.Student;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.interfaces.ShowAbleMessage;

import java.util.ArrayList;
import java.util.List;

public class StudentDatabase extends SQLiteOpenHelper implements ShowAbleMessage {
    public   String STUDENT_TABLE = "STUDENT_TABLE";
    public   String COLUMN_STUDENT_NAME = "STUDENT_NAME";
    public   String COLUMN_REGISTRATION_NUMBER = "REGISTRATION_NUMBER";
    public   String COLUMN_STUDENT_ATTENDANCE = "STUDENT_ATTENDANCE";
    public   String COLUMN_ATTENDANCE_DATE = "ATTENDANCE_DATE";
    public   String COLUMN_STUDENT_MARK = "STUDENT_MARK";

    private ViewAllActivity viewAllActivity;

    private Context context;
    public StudentDatabase(@Nullable Context context) {

        super(context, "student.db", null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE "+STUDENT_TABLE+" ( "+COLUMN_STUDENT_NAME+" TEXT, "+" "+COLUMN_REGISTRATION_NUMBER+
                " TEXT, "+COLUMN_STUDENT_ATTENDANCE+" TEXT, "+COLUMN_ATTENDANCE_DATE+" TEXT, "+COLUMN_STUDENT_MARK+" INT)";
        db.execSQL(createTableStatement);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+STUDENT_TABLE);
        onCreate(db);

    }
    public boolean addStudent(Student student){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STUDENT_NAME,student.getName());
        cv.put(COLUMN_REGISTRATION_NUMBER,student.getRegNum());
        cv.put(COLUMN_STUDENT_ATTENDANCE,student.getAttendance());
        cv.put(COLUMN_ATTENDANCE_DATE,student.getDate());
        cv.put(COLUMN_STUDENT_MARK,student.getMark());
        long insertt = db.insert(STUDENT_TABLE,null,cv);
        if(insertt==-1){
            return false;
        }else {
            return true;
        }
    }
    public boolean deleteOne(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE * FROM " + STUDENT_TABLE + " WHERE " + COLUMN_REGISTRATION_NUMBER + " =" + student.getRegNum();
        Cursor cursor = db.rawQuery(queryString, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
    public List<Student> getEveryOne(){
        List<Student> returnList = new ArrayList<>();
        //String queryString = "SELECT * FROM "+STUDENT_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        String queryString = "SELECT * FROM " +STUDENT_TABLE;
        Cursor cursor = db.rawQuery(queryString,null);
        if (cursor !=null){
        if (cursor.moveToFirst()) {
            do {
                Student newStudent = new Student();
                newStudent.setName(cursor.getString(cursor.getColumnIndex(COLUMN_STUDENT_NAME)));
                newStudent.setRegNum(cursor.getString(cursor.getColumnIndex(COLUMN_REGISTRATION_NUMBER)));
                newStudent.setAttendance(cursor.getString(cursor.getColumnIndex(COLUMN_STUDENT_ATTENDANCE)));
                newStudent.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_ATTENDANCE_DATE)));
                newStudent.setMark(cursor.getInt(cursor.getColumnIndex(COLUMN_STUDENT_MARK)));
                returnList.add(newStudent);

            } while (cursor.moveToNext());
        }
        }else {

        }
        return returnList;
    }
    public List<Student> getPresentStudent(){
        List<Student> availableStudent = new ArrayList<>();
        String queryString = "SELECT * FROM "+STUDENT_TABLE+" WHERE "+COLUMN_STUDENT_ATTENDANCE+" LIKE"+"(present)";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        if(cursor.moveToFirst()){
            do{
                String studentName =cursor.getString(0);
                String studentRegNum = cursor.getString(1);
                String studentAttendance = cursor.getString(2);
                String attendanceDate = cursor.getString(3);
                int mark = cursor.getInt(4);

                Student student1 = new Student(studentName,studentRegNum,studentAttendance,attendanceDate,mark);
                availableStudent.add(student1);
            }while (cursor.moveToNext());

        }else {

        }
        cursor.close();
        db.close();
        return availableStudent;
    }

    public boolean viewOne(Student student){
        try {
            String queryString = ("SELECT COLUMN_STUDENT_NAME,COLUMN_REGISTRATION_NUMBER,COLUMN_STUDENT_ATTENDANCE," +
                    "COLUMN_ATTENDANCE_DATE,COLUMN_STUDENT_MARK FROM "+STUDENT_TABLE+" WHERE "+COLUMN_REGISTRATION_NUMBER+"="+student.getRegNum());
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(queryString,null);
            if(cursor.moveToFirst()) {
                String studentName = cursor.getString(0);
                String studentRegNum = cursor.getString(1);
                String studentAttendance = cursor.getString(2);
                String attendanceDate = cursor.getString(3);
                int mark = cursor.getInt(4);
                Student student1 = new Student(studentName, studentRegNum, studentAttendance, attendanceDate, mark);
            }
        }catch (Exception e){
            e.printStackTrace();
        }return true;

    }
    public boolean updateOne(Student student){
        SQLiteDatabase db = this.getWritableDatabase();
        String queryString =("UPDATE "+STUDENT_TABLE+" SET "+COLUMN_STUDENT_NAME+" "+student.getName()+","+COLUMN_REGISTRATION_NUMBER+
                " "+student.getRegNum()+","+COLUMN_STUDENT_ATTENDANCE+" "+student.getAttendance()+","+COLUMN_ATTENDANCE_DATE+" "+
                student.getDate()+","+COLUMN_STUDENT_MARK+" "+student.getMark()+"");
        Cursor cursor = db.rawQuery("SELECT * FROM "+STUDENT_TABLE+" WHERE "+COLUMN_REGISTRATION_NUMBER+" ="+student.getRegNum()+"'",null);
        if(cursor.moveToFirst()){
            db.execSQL(queryString);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void showMessage(String title, String message) {

    }
    public Cursor giveMedata(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+STUDENT_TABLE,null);
        return data;
    }
    public Cursor login(String registrationNumber){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = ("SELECT * FROM "+STUDENT_TABLE+" WHERE "+COLUMN_REGISTRATION_NUMBER+"="+registrationNumber);
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }
}
