package com.example.finalproj_beta2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.Student.CREDITS;
import static com.example.finalproj_beta2.Teacher.GMAIL;
import static com.example.finalproj_beta2.Teacher.LEVEL;
import static com.example.finalproj_beta2.Teacher.NAME;

public class main_activity extends AppCompatActivity {

    // this activity is the first to open,
    // and the hub from where the user can log in, sign up, and create a new school

    SignInButton signin;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private static final int RC_SIGN_IN = 1;
    String uid;
    boolean exists_student = false;
    boolean exists_teacher = false;
    boolean signed_in = false;
    DataSnapshot data_teacher,data_student;
    EditText school_id;
    SharedPreferences settings;
    String get_school_id;
    ArrayList<String> school_ids;
    ProgressDialog progressDialog;
    TextView emailTv;
    LinearLayout second_step;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signin = findViewById(R.id.sign_in_button);

        emailTv = findViewById(R.id.gmail);
        second_step = findViewById(R.id.second_step);
        second_step.setVisibility(View.INVISIBLE);

        school_id = findViewById(R.id.school_name_et);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        break;
                }
            }
        });

        school_ids = new ArrayList<String>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading Information...");
        progressDialog.setCancelable(false);


        refSchools.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                school_ids.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    school_ids.add(data.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        settings = getSharedPreferences("login_info",MODE_PRIVATE);
    }

    public void onStart() {
        super.onStart();
        user = mAuth.getCurrentUser();
        updateUI();
    }

    //opens the google sign in UI
    private void signIn() {
        mGoogleSignInClient.signOut();
        signed_in = false;
        second_step.setVisibility(View.INVISIBLE);
        emailTv.setText("First Sign In With Google.");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //after signed in with google in google ui, this function is called.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    //validates the sign in has occured correctly and creates a boolean variable to save the result.
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            if (idToken != null) {
                // Got an ID token from Google. Use it to authenticate
                // with Firebase.
                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                mAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Working!", "signInWithCredential:success");
                                    user = mAuth.getCurrentUser();
                                    signed_in = true;
                                    second_step.setVisibility(View.VISIBLE);
                                    emailTv.setText("Signed In With: " + user.getEmail());

                                } else {
                                    Log.w("Error!", "signInWithCredential:failure", task.getException());
                                }
                            }

                        });

            }

        } catch (ApiException e) {
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    //update the user interface and the vairiables one someone signed in
    // if the signed in account exists in the school, the activity will transfer to home screen
    // if it dosent it will transfer to sign up.
    public void updateUI() {

        get_school_id = settings.getString("school_id", "999999");
        if (user != null && !get_school_id.equals("999999")) {

            progressDialog.show();
            exists_student = false;
            exists_teacher = false;
            uid = user.getUid();

            refSchools.child(get_school_id).child("users").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    System.out.println(snapshot.getChildrenCount());
                    for (DataSnapshot data : snapshot.child("teachers").getChildren()) {
                        if (data.getKey().equals(uid)) {
                            exists_teacher = true;
                            data_teacher = data;
                        }
                    }

                    for (DataSnapshot data : snapshot.child("students").getChildren()) {
                        if (data.getKey().equals(uid)) {
                            exists_student = true;
                            data_student = data;
                        }
                    }

                    if (exists_teacher) {
                        Intent back = getIntent();
                        Intent home_screen = new Intent(getApplicationContext(), home_screen.class);

                        home_screen.putExtra("name", data_teacher.child(NAME).getValue().toString());
                        home_screen.putExtra("gmail", data_teacher.child(GMAIL).getValue().toString());
                        home_screen.putExtra("level", data_teacher.child(LEVEL).getValue().toString());

                        SharedPreferences.Editor editor=settings.edit();
                        editor.putString("teacher_or_student","teacher");
                        editor.putString("school_id",get_school_id);
                        editor.commit();
                        startActivity(home_screen);

                    } else if (exists_student) {
                        Intent home_screen = new Intent(getApplicationContext(), home_screen.class);

                        home_screen.putExtra("name", data_student.child(NAME).getValue().toString());
                        home_screen.putExtra("gmail", data_student.child(GMAIL).getValue().toString());
                        home_screen.putExtra("credits", data_student.child(CREDITS).getValue().toString());

                        SharedPreferences.Editor editor=settings.edit();
                        editor.putString("teacher_or_student","student");
                        editor.putString("school_id",get_school_id);
                        editor.commit();

                        startActivity(home_screen);
                    } else {
                        Intent auth = new Intent(getApplicationContext(), first_signin.class);
                        auth.putExtra("gmail", user.getEmail());
                        auth.putExtra("name", user.getDisplayName());
                        auth.putExtra("uid", user.getUid());
                        auth.putExtra("school", get_school_id);
                        startActivity(auth);
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError error) {

                }
            });
        }

    }

    //make sure the dialog box is closed when the activity is destroyed.
    public void onDestroy() {
        progressDialog.dismiss();
        super.onDestroy();
    }

    //if user is signed in transfer to creatge new school activity
    public void create_school(View view) {
        if (signed_in) {
            Intent create_school = new Intent(this, create_school.class);
            create_school.putExtra("gmail", user.getEmail());
            create_school.putExtra("name", user.getDisplayName());
            create_school.putExtra("uid", user.getUid());
            create_school.putExtra("url",user.getPhotoUrl().toString());
            startActivity(create_school);
        }
        else{
            Toast.makeText(this, "please sign in with google", Toast.LENGTH_SHORT).show();
        }
    }

    //sign out of account
    public void sign_out(View view) {
        mGoogleSignInClient.signOut();
        signed_in = false;
        second_step.setVisibility(View.INVISIBLE);
        emailTv.setText("First Sign In With Google.");
    }
    
    //if the user wants to log in to existing account or create a new one it will check in the
    // "update UI" function and send him to the right activity.
    public void log_in(View view) {
        if (signed_in) {

            if (user != null && school_id.getText().toString().length() == 6) {
                if (school_ids.contains(school_id.getText().toString())){
                    SharedPreferences.Editor editor=settings.edit();
                    editor.putString("school_id",school_id.getText().toString());
                    editor.commit();
                    get_school_id = school_id.getText().toString();
                    updateUI();
                }
                else{
                    Toast.makeText(this, "please enter an existing school code", Toast.LENGTH_SHORT).show();
                }

            }
            else{
                Toast.makeText(this, "please enter a school code", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "please sign in with google", Toast.LENGTH_SHORT).show();
        }
    }
}
