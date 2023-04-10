package com.example.finalproj_beta2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.Teacher.GMAIL;
import static com.example.finalproj_beta2.Teacher.LEVEL;
import static com.example.finalproj_beta2.Teacher.NAME;

public class create_school extends AppCompatActivity{

    private Spinner spinner;
    String get_school_id, last_school_id, new_school_id;
    SharedPreferences settings;
    EditText school_name;
    String secret_code;
    String name,email,uid,url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_school);

        Intent get = getIntent();
        name = get.getStringExtra("name");
        email = get.getStringExtra("gmail");
        uid = get.getStringExtra("uid");
        url = get.getStringExtra("url");


        school_name = findViewById(R.id.school_name_et);

        settings = getSharedPreferences("login_info",MODE_PRIVATE);
        get_school_id = settings.getString("school_id", "999999");
    }

    public void generate_school_id(){

        Query school_ids = refSchools.orderByKey().limitToLast(1);

        school_ids.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    last_school_id = data.getKey();
                }
                System.out.println(last_school_id);
                new_school_id = String.valueOf(Integer.parseInt(last_school_id) + 1);
                refSchools.child(new_school_id).child("requests").setValue("");
                refSchools.child(new_school_id).child("student_prints").setValue("");
                refSchools.child(new_school_id).child("users").child("teachers").setValue("");
                refSchools.child(new_school_id).child("users").child("students").setValue("");
                refSchools.child(new_school_id).child("name").setValue(school_name.getText().toString());
                secret_code = Integer.toString((int) ((Math.random() * (9999 - 1000)) + 1000));
                Teacher newTeacher = new Teacher(name, new_school_id, email, 0);
                refSchools.child(new_school_id).child("users").child("teachers").child(uid).setValue(newTeacher);
                refSchools.child(new_school_id).child("users").child("teachers").child(uid).child("image_url").setValue(url);

                refSchools.child(new_school_id).child("secret_code").setValue(secret_code);
                Toast.makeText(create_school.this,"School created", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }

    public void create_school(View view) {
        generate_school_id();
    }
}