package com.example.finalproj_beta2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.Student.CREDITS;
import static com.example.finalproj_beta2.Teacher.GMAIL;
import static com.example.finalproj_beta2.Teacher.LEVEL;
import static com.example.finalproj_beta2.Teacher.NAME;

public class home_screen_student extends AppCompatActivity {

    TextView name, email;
    ImageView pfp_image;
    String get_school_id;

    FirebaseAuth mAuth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;
    String credits;
    SharedPreferences settings;

    String uid;

    ValueEventListener studentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_student);

        name = findViewById(R.id.student_name);
        email = findViewById(R.id.student_email);
        pfp_image = findViewById(R.id.pfp_image_student);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        settings = getSharedPreferences("login_info",MODE_PRIVATE);

        if (acct != null) {
            uid = user.getUid();
            System.out.println("-------"+user);
            studentListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dS) {
                    for (DataSnapshot data : dS.getChildren()) {
                        if (data.getKey().equals(uid)){
                            updateUI(data.child(NAME).getValue().toString(), data.child(GMAIL).getValue().toString(), data.child(CREDITS).getValue().toString(), user);
                            credits = data.child(CREDITS).getValue().toString();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {}
            };

            get_school_id = settings.getString("school_id", "999999");
            if (!get_school_id.equals("999999")) {
                refSchools.child(get_school_id).child("users").child("students").addValueEventListener(studentListener);
            }
            else{
                Toast.makeText(home_screen_student.this, "no school", Toast.LENGTH_SHORT).show();

            }
        }


    }

    public void updateUI(String name1, String gmail1, String level1, FirebaseUser user) {
        System.out.println(name1 + gmail1 + level1);
        if (!name1.equals("")){
            name.setText(name1);
        }
        if (!gmail1.equals("")){
            email.setText(gmail1);
        }
        get_school_id = settings.getString("school_id", "999999");
        System.out.println(user + "------" + pfp_image);
        Picasso.get().load(user.getPhotoUrl()).into(pfp_image);
    }

    public void print(View view) {
        Intent upload_file_screen = new Intent(this, upload_file.class);
        upload_file_screen.putExtra("uid", uid);
        upload_file_screen.putExtra("user_name", name.getText());
        upload_file_screen.putExtra("student_or_teacher", "student");
        startActivity(upload_file_screen);
    }

    public void sign_out_real(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(home_screen_student.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(home_screen_student.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
    }

    public void sign_out(View view) {
        sign_out_real();
    }

    public void onPause() {
        super.onPause();
        refSchools.child(get_school_id).child("users").child("students").removeEventListener(studentListener);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.menuSignOut){
            sign_out_real();
        }
        if (id == R.id.menuMyProfile){}

        return true;
    }
}