package com.example.finalproj_beta2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.Teacher.GMAIL;
import static com.example.finalproj_beta2.Teacher.LEVEL;
import static com.example.finalproj_beta2.Teacher.NAME;

public class home_screen extends AppCompatActivity {

    ImageView pfp_image;
    TextView name, email, school_txt;
    Button my_school_btn;
    String uid;
    ListView reqs;
    String reqs_ids;
    String school, tmpSchool, get_school_id;

    FirebaseAuth mAuth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;
    String lvl1;
    SharedPreferences settings;

    ValueEventListener teacherListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        my_school_btn = findViewById(R.id.my_school);
        pfp_image = findViewById(R.id.pfp_image);
        school_txt = findViewById(R.id.school_txt);

        my_school_btn.setVisibility(INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        settings = getSharedPreferences("login_info",MODE_PRIVATE);

        updateUI("Loading...", "", "2", user);

        if (acct != null) {
            uid = user.getUid();
            teacherListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dS) {
                    for (DataSnapshot data : dS.getChildren()) {
                        if (data.getKey().equals(uid)){
                            school = tmpSchool;
                            updateUI(data.child(NAME).getValue().toString(), data.child(GMAIL).getValue().toString(), data.child(LEVEL).getValue().toString(), user);
                            lvl1 = data.child(LEVEL).getValue().toString();

                            if (lvl1.equals("0")){
                                my_school_btn.setVisibility(VISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {}
            };

            get_school_id = settings.getString("school_id", "999999");
            if (!get_school_id.equals("999999")) {
                refSchools.child(get_school_id).child("users").child("teachers").addValueEventListener(teacherListener);
            }
            else{
                Toast.makeText(home_screen.this, "no school", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void sign_out_real(){
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(home_screen.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(home_screen.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                    }
                });
    }

    public void sign_out(View view) {
        sign_out_real();
    }

    public void request_print(View view) {
        Intent upload_file_screen = new Intent(this, upload_file.class);
        upload_file_screen.putExtra("uid", uid);
        upload_file_screen.putExtra("user_name", name.getText());
        upload_file_screen.putExtra("student_or_teacher", "teacher");
        startActivity(upload_file_screen);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            user = mAuth.getCurrentUser();
            uid = user.getUid();
            System.out.println(data.getStringExtra("name"));
            updateUI(data.getStringExtra("name"), data.getStringExtra("gmail"), data.getStringExtra("level"), user);
        }
        else{
            updateUI("Error", "Error", "2", user);
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
        school_txt.setText(get_school_id);

        Picasso.get().load(user.getPhotoUrl()).into(pfp_image);

//        if (level1.equals("2")){
//            pending_btn.setVisibility(INVISIBLE);
//        }
//        else if (level1.equals("1") || level1.equals("0")){
//            pending_btn.setVisibility(VISIBLE);
//        }
    }

    public void pending_requests(View view) {
        Intent intent = new Intent(this, pending_requests.class);
        intent.putExtra("uid", uid);
        intent.putExtra("lvl", lvl1);
        startActivity(intent);
    }

    public void create_school(View view) {
        Intent intent = new Intent(this, create_school.class);
        startActivity(intent);
    }

    public void reset_shared(View view) {
        SharedPreferences.Editor editor=settings.edit();
        editor.putString("school_id","999999");
        editor.commit();
        updateUI("", "", "", user);
    }

    public void my_school(View view) {
        Intent intent = new Intent(this, My_school.class);
        intent.putExtra("school_id",get_school_id);
        startActivity(intent);
    }

    public void onPause() {
        super.onPause();
        refSchools.child(get_school_id).child("users").child("teachers").removeEventListener(teacherListener);
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