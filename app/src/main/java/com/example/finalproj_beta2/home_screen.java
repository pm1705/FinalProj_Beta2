package com.example.finalproj_beta2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.Request.APPROVED;
import static com.example.finalproj_beta2.Request.COLORFUL;
import static com.example.finalproj_beta2.Request.COPIES;
import static com.example.finalproj_beta2.Request.DATE_PRINTED;
import static com.example.finalproj_beta2.Request.DOUBLE_SIDED;
import static com.example.finalproj_beta2.Request.RELEVANT;
import static com.example.finalproj_beta2.Request.USER_ID;
import static com.example.finalproj_beta2.Request.USER_NAME;
import static com.example.finalproj_beta2.Student.CREDITS;
import static com.example.finalproj_beta2.Student_print.COST;
import static com.example.finalproj_beta2.Student_print.PRINTED;
import static com.example.finalproj_beta2.Teacher.GMAIL;
import static com.example.finalproj_beta2.Teacher.LEVEL;
import static com.example.finalproj_beta2.Teacher.NAME;

import static java.lang.Long.parseLong;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class home_screen extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnCreateContextMenuListener {

    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * the home screen activity for both students and teachers.
     * here a student can view how many credits he has, he can send a print or view his past past prints.
     * here a teacher can view how many pending requests there are, he can send a print and view all pending requests
     * if he is not a reviewer that information will be hidden.
     */

    String get_school_id;
    FirebaseAuth mAuth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;
    String uid;
    ArrayList<String> pending_requests, displays, uris, copies, settings_list, due_dates;
    ArrayList<Boolean> urgent;
    String lvl1;
    SharedPreferences settings;
    Activity currentActivity = this;
    ListView pending_requests_lv;
    String get_name, get_school_name, get_email;
    String get_credits = "0";
    String temp_setting_string;
    ValueEventListener requestListener;
    Long current_millis;
    CostumeAdapter customadp;
    ImageButton menu_button;
    TextView pending_num, pending_or_credits, welcome_tv;
    String teacher_or_student = "";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnCreateContextMenuListener(this);

        pending_requests_lv = findViewById(R.id.lv_requests);
        pending_requests_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        pending_requests_lv.setOnItemClickListener(this);

        pending_num = findViewById(R.id.pending_num);
        pending_or_credits = findViewById(R.id.pending_or_credits);
        welcome_tv = findViewById(R.id.welcome_tv);

        pending_requests = new ArrayList<String>();
        displays = new ArrayList<String>();
        uris = new ArrayList<String>();
        copies = new ArrayList<String>();
        settings_list = new ArrayList<String>();
        due_dates = new ArrayList<String>();
        urgent = new ArrayList<Boolean>();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        settings = getSharedPreferences("login_info", MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading Information...");
        progressDialog.setCancelable(false);

        if (acct != null) {

            progressDialog.show();

            uid = user.getUid();
            get_school_id = settings.getString("school_id", "999999");
            teacher_or_student = settings.getString("teacher_or_student", "");

            if (teacher_or_student.equals("student")) {
                refSchools.child(get_school_id).child("users").child("students").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dS) {
                        for (DataSnapshot data : dS.getChildren()) {
                            if (data.getKey().equals(uid)) {
                                lvl1 = "3";
                                get_name = data.child(NAME).getValue().toString();
                                get_email = data.child(GMAIL).getValue().toString();
                                get_credits = data.child(CREDITS).getValue().toString();
                                pending_num.setText("" + get_credits);
                                pending_or_credits.setText("Credits");
                                welcome_tv.setText("Welcome, " +  get_name);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
                requestListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dS) {
                        current_millis = System.currentTimeMillis();
                        pending_requests.clear();
                        displays.clear();
                        uris.clear();
                        copies.clear();
                        settings_list.clear();
                        due_dates.clear();
                        urgent.clear();
                        for (DataSnapshot data : dS.getChildren()) {
                            if (data.child(USER_ID).getValue().toString().equals(user.getUid())) {
                                pending_requests.add(data.getKey());
                                if (Boolean.parseBoolean(data.child(PRINTED).getValue().toString())){
                                    displays.add("Printed");
                                }
                                else{
                                    displays.add("Not Printed");
                                }
                                uris.add(data.child("image_url").getValue().toString());
                                copies.add("" + data.child(COPIES).getValue().toString() + " copies");
                                temp_setting_string = "";
                                if (Boolean.parseBoolean(data.child(COLORFUL).getValue().toString())){
                                    temp_setting_string += "Colorful, ";
                                }
                                else{
                                    temp_setting_string += "B&W, ";
                                }
                                if (Boolean.parseBoolean(data.child(DOUBLE_SIDED).getValue().toString())){
                                    temp_setting_string += "Double sided";
                                }
                                else{
                                    temp_setting_string += "one sided";
                                }
                                settings_list.add(temp_setting_string);
                                due_dates.add("costs " + data.child(COST).getValue().toString() + " credits");
                                urgent.add(Boolean.parseBoolean(data.child(PRINTED).getValue().toString()));
                            }
                        }

                        pending_num.setText("" + get_credits);
                        pending_or_credits.setText("Credits");
                        customadp = new CostumeAdapter(getApplicationContext(), displays, uris, copies, settings_list, due_dates, urgent);
                        pending_requests_lv.setAdapter(customadp);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                };
            }
            else{
                requestListener = new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dS) {
                        current_millis = System.currentTimeMillis();
                        pending_requests.clear();
                        displays.clear();
                        uris.clear();
                        copies.clear();
                        settings_list.clear();
                        due_dates.clear();
                        urgent.clear();
                        for (DataSnapshot data : dS.getChildren()) {
                            if (!(boolean) data.child(APPROVED).getValue() && (boolean) data.child(RELEVANT).getValue()) {
                                if (is_relevant(data.child(DATE_PRINTED).getValue().toString())) {
                                    if ((lvl1.equals("2") && data.child(USER_ID).getValue().toString().equals(user.getUid())) || lvl1.equals("0") || lvl1.equals("1")) {
                                        pending_requests.add(data.getKey());
                                        displays.add("" + data.child(USER_NAME).getValue().toString());
                                        uris.add(data.child("image_url").getValue().toString());
                                        copies.add("" + data.child(COPIES).getValue().toString() + " copies");

                                        temp_setting_string = "";
                                        if (Boolean.parseBoolean(data.child(COLORFUL).getValue().toString())){
                                            temp_setting_string += "Colorful, ";
                                        }
                                        else{
                                            temp_setting_string += "B&W, ";
                                        }

                                        if (Boolean.parseBoolean(data.child(DOUBLE_SIDED).getValue().toString())){
                                            temp_setting_string += "Double sided";
                                        }
                                        else{
                                            temp_setting_string += "one sided";
                                        }
                                        settings_list.add(temp_setting_string);

                                        due_dates.add("due in " + getDaysBetween(current_millis, Long.parseLong(data.child(DATE_PRINTED).getValue().toString())) + " days");

                                        urgent.add(getDaysBetween(current_millis, Long.parseLong(data.child(DATE_PRINTED).getValue().toString())) <= 10);
                                    }
                                }
                                else {
                                    refSchools.child(get_school_id).child("requests").child(data.getKey()).child(RELEVANT).setValue(false);
                                }

                            }
                        }

                        pending_num.setText("" + pending_requests.size());
                        pending_or_credits.setText("Pending");
                        customadp = new CostumeAdapter(getApplicationContext(), displays, uris, copies, settings_list, due_dates, urgent);
                        pending_requests_lv.setAdapter(customadp);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                };
                refSchools.child(get_school_id).child("users").child("teachers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dS) {
                        for (DataSnapshot data : dS.getChildren()) {
                            if (data.getKey().equals(uid)) {
                                lvl1 = data.child(LEVEL).getValue().toString();
                                get_name = data.child(NAME).getValue().toString();
                                get_email = data.child(GMAIL).getValue().toString();
                                get_credits = "0";
                                welcome_tv.setText("Welcome, " +  get_name);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
            }

        }
        else{
            finish();
        }
    }

    // this function is a helper function to sign_out because it doesent need an input of a view.
    public void sign_out_real() {

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Sign out");
        adb.setMessage("are you sure you want to sign out?");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(currentActivity, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                FirebaseAuth.getInstance().signOut();
                                Toast.makeText(home_screen.this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(home_screen.this, main_activity.class);
                                finish();
                                startActivity(intent);
                            }
                        });
            }
        });

        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    //this transfers the user to the my profile activity
    public void my_profile(){
        Intent intent = new Intent(home_screen.this, my_profile.class);
        intent.putExtra("name", get_name);
        intent.putExtra("email", get_email);
        intent.putExtra("url", user.getPhotoUrl().toString());
        intent.putExtra("school_name", get_school_name);
        intent.putExtra("school_code", get_school_id);
        intent.putExtra("lvl", lvl1);
        startActivity(intent);
    }

    // this transfers the user to the upload file activity
    public void request_print(View view) {
        Intent upload_file_screen = new Intent(this, upload_file.class);
        upload_file_screen.putExtra("uid", uid);
        upload_file_screen.putExtra("user_name", get_name);
        upload_file_screen.putExtra("credits", get_credits);
        upload_file_screen.putExtra("student_or_teacher", teacher_or_student);
        startActivity(upload_file_screen);
    }

    //check if a date(millis) is relevant(after the current date)
    public boolean is_relevant(String print_millis) {
        current_millis = System.currentTimeMillis();
        if (current_millis > parseLong(print_millis)) {
            return false;
        }
        return true;
    }

    //returns the number of days between two dates(millis)
    public static long getDaysBetween(long startTimeMillis, long endTimeMillis) {
        long timeDifferenceMillis = Math.abs(endTimeMillis - startTimeMillis);
        long days = TimeUnit.MILLISECONDS.toDays(timeDifferenceMillis);
        return days;
    }

    //stop the onDataChange listener when activity is paused
    public void onPause() {
        super.onPause();
        if (teacher_or_student.equals("student")) {
            refSchools.child(get_school_id).child("student_prints").removeEventListener(requestListener);
        }
        else if (teacher_or_student.equals("teacher")){
            refSchools.child(get_school_id).child("requests").removeEventListener(requestListener);
        }
    }

    //resume the onDataChange listener when activity is resumed
    public void onResume() {
        super.onResume();

        if (teacher_or_student.equals("student")) {
            refSchools.child(get_school_id).child("student_prints").addValueEventListener(requestListener);
        }
        else if (teacher_or_student.equals("teacher")){
            Query query = refSchools.child(get_school_id).child("requests").orderByChild(DATE_PRINTED);
            query.addValueEventListener(requestListener);
        }
    }

    //if the user is a teacher send them to the view request activity
    //if they are a reviewer they can approve the request.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(teacher_or_student.equals("teacher")) {
            Intent view_request = new Intent(this, com.example.finalproj_beta2.view_request.class);
            view_request.putExtra("RQID", pending_requests.get(position));
            view_request.putExtra("uid", uid);
            view_request.putExtra("lvl", lvl1);
            startActivity(view_request);
        }
    }

    //open context menu
    public void menu_show(View view) {
        this.openContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Menu");
        menu.add("My profile");
        menu.add("Sign out");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String oper=item.getTitle().toString();
        if (oper.equals("My profile")) {
            my_profile();
        }
        else if(oper.equals("Sign out")) {
            sign_out_real();
        }
        return true;
    }
}