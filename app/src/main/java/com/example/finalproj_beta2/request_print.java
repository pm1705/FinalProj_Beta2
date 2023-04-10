package com.example.finalproj_beta2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class request_print extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_print);
    }

    public void done(View view) {
        Intent intent = new Intent(this, home_screen.class);
        startActivity(intent);
    }
}