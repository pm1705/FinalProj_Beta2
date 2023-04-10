package com.example.finalproj_beta2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.example.finalproj_beta2.DB_refs.refSchools;

public class first_signin extends AppCompatActivity {

    TextView mail_view;
    String gmail, name, uid, school;
    Intent google_auth_signin;
    EditText name_et, school_id_et;

    SharedPreferences settings;
    String get_school_id;

    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_signin);

        google_auth_signin = getIntent();
        gmail = google_auth_signin.getStringExtra("gmail");
        name = google_auth_signin.getStringExtra("name");
        uid = google_auth_signin.getStringExtra("uid");
        school = google_auth_signin.getStringExtra("school");


        mail_view = (TextView) findViewById(R.id.set_mail);
        mail_view.setText(gmail);

        name_et = (EditText) findViewById(R.id.name_et);
        name_et.setText(name);

        settings = getSharedPreferences("login_info",MODE_PRIVATE);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    public void teacher_next(View view) {
        Intent teacher_sign_in = new Intent(getApplicationContext(), teacher_signin.class);

        teacher_sign_in.putExtra("gmail", gmail);
        teacher_sign_in.putExtra("uid", uid);
        teacher_sign_in.putExtra("name", name_et.getText().toString());
        teacher_sign_in.putExtra("school", school);
        startActivity(teacher_sign_in);
    }

    public void student_next(View view) {
        Student newStudent = new Student(name, school, gmail);
        get_school_id = settings.getString("school_id", "999999");
        refSchools.child(get_school_id).child("users").child("students").child(uid).setValue(newStudent);
        refSchools.child(get_school_id).child("users").child("students").child(uid).child("image_url").setValue(user.getPhotoUrl().toString());
        Intent intent = new Intent(this, home_screen_student.class);
        startActivity(intent);
    }
}