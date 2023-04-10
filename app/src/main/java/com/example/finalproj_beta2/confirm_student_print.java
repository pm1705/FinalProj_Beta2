package com.example.finalproj_beta2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class confirm_student_print extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_student_print);
    }

    public void done(View view) {
        Intent intent = new Intent(this, home_screen_student.class);
        startActivity(intent);
    }
}