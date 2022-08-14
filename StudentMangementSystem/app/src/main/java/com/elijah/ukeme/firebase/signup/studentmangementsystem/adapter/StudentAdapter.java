package com.elijah.ukeme.firebase.signup.studentmangementsystem.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elijah.ukeme.firebase.signup.studentmangementsystem.R;
import com.elijah.ukeme.firebase.signup.studentmangementsystem.data.StudentDatabase;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    StudentDatabase studentDatabase;

    private Context context;
    private Cursor cursor;

    public StudentAdapter(Context context, Cursor cursor){
        this.context = context;
        this.cursor = cursor;
        studentDatabase = new StudentDatabase(context);

    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        studentDatabase = new StudentDatabase(parent.getContext());
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.student_items,parent,false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        if (cursor.moveToPosition(position)){
            return;
        }
       String name = cursor.getString(cursor.getColumnIndex(studentDatabase.COLUMN_STUDENT_NAME));
        String registrationNumber = cursor.getString(cursor.getColumnIndex(studentDatabase.COLUMN_REGISTRATION_NUMBER));
        String attendancestatus = cursor.getString(cursor.getColumnIndex(studentDatabase.COLUMN_STUDENT_ATTENDANCE));
        String date = cursor.getString(cursor.getColumnIndex(studentDatabase.COLUMN_ATTENDANCE_DATE));
        int score = cursor.getInt(cursor.getColumnIndex(studentDatabase.COLUMN_STUDENT_MARK));
        holder.nameText.setText(name);
        holder.regText.setText("Registration Number: "+registrationNumber);
        holder.attendText.setText("Attendance Status: "+attendancestatus);
        holder.scoreText.setText("Score: "+score);
        holder.dateText.setText("Date: "+date);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder{
        public TextView nameText;
        public TextView regText;
        public TextView attendText;
        public TextView scoreText;
        public TextView dateText;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.textview_item_list_name);
            regText = itemView.findViewById(R.id.textview_registration_number_item_list);
            attendText = itemView.findViewById(R.id.textview_attendance_status_item_list);
            scoreText = itemView.findViewById(R.id.textview_mark_item_list);
            dateText = itemView.findViewById(R.id.textview_date_item_list);
        }
    }
    public void swapCursor(Cursor newCursor){
        if (cursor !=null){
            cursor.close();
        }
        cursor =newCursor;
        if (newCursor !=null){
            notifyDataSetChanged();
        }
    }
}
