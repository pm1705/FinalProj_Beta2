package com.example.finalproj_beta2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.Student.CREDITS;
import static com.example.finalproj_beta2.Teacher.GMAIL;
import static com.example.finalproj_beta2.Teacher.LEVEL;
import static com.example.finalproj_beta2.Teacher.NAME;

public class MainActivity extends AppCompatActivity {

    SignInButton signin;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseUser user;
    private static final int RC_SIGN_IN = 1;
    String uid;
    boolean exists_student = false;
    boolean exists_teacher = false;
    DataSnapshot data_teacher,data_student;
    EditText school_id;
    SharedPreferences settings;
    String get_school_id;
    boolean signed_in = false;
    ArrayList<String> school_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signin = findViewById(R.id.sign_in_button);

        school_id = findViewById(R.id.school_id_login);

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
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        user = mAuth.getCurrentUser();
        updateUI();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

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
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("Working!", "signInWithCredential:success");
                                    user = mAuth.getCurrentUser();
                                    signed_in = true;

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("Error!", "signInWithCredential:failure", task.getException());
                                }
                            }

                        });

            }


            // Signed in successfully, show authenticated UI.
//            startActivity(siTeacher);
        } catch (ApiException e) {

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }


    public void updateUI() {

        get_school_id = settings.getString("school_id", "999999");
        if (user != null && !get_school_id.equals("999999")) {

            exists_student = false;
            exists_teacher = false;
            uid = user.getUid();
            System.out.println("update");

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
                        back.putExtra("name", data_teacher.child(NAME).getValue().toString());
                        back.putExtra("gmail", data_teacher.child(GMAIL).getValue().toString());
                        back.putExtra("level", data_teacher.child(LEVEL).getValue().toString());
                        setResult(RESULT_OK, back);
                        Intent home_screen = new Intent(getApplicationContext(), home_screen.class);
                        System.out.println("he is exist teacher");
                        startActivity(home_screen);
                    } else if (exists_student) {
                        Intent back = getIntent();
                        back.putExtra("name", data_student.child(NAME).getValue().toString());
                        back.putExtra("gmail", data_student.child(GMAIL).getValue().toString());
                        back.putExtra("credits", data_student.child(CREDITS).getValue().toString());
                        setResult(RESULT_OK, back);
                        Intent home_screen = new Intent(getApplicationContext(), home_screen_student.class);
                        System.out.println("he is exist student");
                        startActivity(home_screen);
                    } else {
                        Intent auth = new Intent(getApplicationContext(), first_signin.class);
                        auth.putExtra("gmail", user.getEmail());
                        auth.putExtra("name", user.getDisplayName());
                        auth.putExtra("uid", user.getUid());
                        auth.putExtra("school", school_id.getText().toString());
                        System.out.println("he isnt exist");
                        startActivity(auth);
                    }
                }

                @Override
                public void onCancelled(Database Error error) {

                }
            });
        }

    }


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

    public void sign_out(View view) {
        mGoogleSignInClient.signOut();
        signed_in = false;
    }

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
