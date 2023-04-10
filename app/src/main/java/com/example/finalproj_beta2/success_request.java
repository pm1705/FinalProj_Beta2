package com.example.finalproj_beta2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class success_request extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_request);
    }

    public void done(View view) {
        Intent intent = new Intent(this, home_screen.class);
        startActivity(intent);
    }
}