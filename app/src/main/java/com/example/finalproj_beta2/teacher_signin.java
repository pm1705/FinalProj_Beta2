package com.example.finalproj_beta2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.Date;

import static com.example.finalproj_beta2.DB_refs.refAuth;
import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.DB_refs.storageReference;
import static java.security.AccessController.getContext;

public class teacher_signin extends AppCompatActivity {

    String name;
    String school;
    String gmail;
    String uid;
    int perm = 2;

    FirebaseAuth mAuth;
    FirebaseUser user;

    String[] options = new String[]{"Teacher", "Admin"};
    SharedPreferences settings;
    String get_school_id;

    String real_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_signin);

        Intent info = getIntent();
        name = info.getStringExtra("name");
        gmail = info.getStringExtra("gmail");
        school = info.getStringExtra("school");
        uid = info.getStringExtra("uid");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        refSchools.child(school).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                real_code = snapshot.child("secret_code").getValue().toString();
                System.out.println("code " + real_code);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                if (selectedItem.equals("Teacher")) {
                    perm = 2;
                } else if (selectedItem.equals("Admin")) {
                    perm = 1;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        settings = getSharedPreferences("login_info",MODE_PRIVATE);
    }

    public void create_user(View view) {

        if (perm == 1){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("admin code");
            builder.setMessage("enter code to become admin: ");
            final EditText secretCode = new EditText(this);

            secretCode.setHint("Secret code");
            builder.setView(secretCode);

            // Set the positive button
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String code = secretCode.getText().toString();
                    if (code.equals(real_code)){
                        System.out.println("IN CODE GOOD");
                        Teacher newTeacher = new Teacher(name, school, gmail, perm);
                        get_school_id = settings.getString("school_id", "999999");
                        refSchools.child(get_school_id).child("users").child("teachers").child(uid).setValue(newTeacher);
                        refSchools.child(get_school_id).child("users").child("teachers").child(uid).child("image_url").setValue(user.getPhotoUrl().toString());
                        Intent intent = new Intent(getApplicationContext(), home_screen.class);
                        startActivity(intent);
                    }
                    else{
                        System.out.println("IN CODE BAD");
                        Toast.makeText(getApplicationContext(),"Wrong code",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // Set the negative button
            builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else{
            Teacher newTeacher = new Teacher(name, school, gmail, perm);
            get_school_id = settings.getString("school_id", "999999");
            refSchools.child(get_school_id).child("users").child("teachers").child(uid).setValue(newTeacher);
            refSchools.child(get_school_id).child("users").child("teachers").child(uid).child("image_url").setValue(user.getPhotoUrl().toString());
            Intent intent = new Intent(this, home_screen.class);
            startActivity(intent);
        }
    }
}