package com.example.finalproj_beta2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.Request.APPROVED;
import static com.example.finalproj_beta2.Request.COPIES;
import static com.example.finalproj_beta2.Request.DATE_PRINTED;
import static com.example.finalproj_beta2.Request.PENDING;
import static com.example.finalproj_beta2.Request.RELEVANT;
import static com.example.finalproj_beta2.Request.USER_ID;
import static com.example.finalproj_beta2.Request.USER_NAME;
import static java.lang.Long.parseLong;

public class pending_requests extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    ListView pending_requests_lv;

    ArrayList<String> my_pending_requests, my_displays;
    ArrayList<String> pending_requests, displays;

    CostumeAdapter customadp;

    Long current_millis;
    String uid, lvl, get_school_id;
    SharedPreferences settings;

    ArrayList<String> my_uris;
    ArrayList<String> uris;

    FirebaseAuth mAuth;
    FirebaseUser user;

    Spinner view_reqs;
    String[] options = {"all requests", "my requests"};

    ValueEventListener requestListener;

    Boolean mine = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);

        pending_requests_lv = findViewById(R.id.requests);
        pending_requests_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        pending_requests_lv.setOnItemClickListener(this);

        pending_requests = new ArrayList<String>();
        displays = new ArrayList<String>();
        my_pending_requests = new ArrayList<String>();
        my_displays = new ArrayList<String>();
        uris = new ArrayList<String>();
        my_uris = new ArrayList<String>();

        view_reqs = findViewById(R.id.view_reqs);

        view_reqs.setOnItemSelectedListener(this);
        ArrayAdapter<String> view_reqs_adpater = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, options);
        view_reqs.setAdapter(view_reqs_adpater);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        lvl = intent.getStringExtra("lvl");

        settings = getSharedPreferences("login_info",MODE_PRIVATE);
        get_school_id = settings.getString("school_id", "999999");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        requestListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dS) {
                pending_requests.clear();
                displays.clear();
                my_pending_requests.clear();
                my_displays.clear();
                uris.clear();
                my_uris.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    if (!(boolean) data.child(APPROVED).getValue() && (boolean) data.child(RELEVANT).getValue()){
                       if (data.child(USER_ID).getValue().toString().equals(user.getUid())) {
                           my_pending_requests.add(data.getKey());
                           if (is_relevant(data.child(DATE_PRINTED).getValue().toString())) {
                               my_displays.add("" + data.child(USER_NAME).getValue().toString() + " , " + data.child(COPIES).getValue().toString() + " copies");
                               my_uris.add(data.child("image_url").getValue().toString());
                           }
                           else {
                               refSchools.child(get_school_id).child("requests").child(data.getKey()).child(RELEVANT).setValue(false);
                           }
                       }

                        pending_requests.add(data.getKey());
                         if (is_relevant(data.child(DATE_PRINTED).getValue().toString())) {
                            displays.add("" + data.child(USER_NAME).getValue().toString() + " , " + data.child(COPIES).getValue().toString() + " copies");
                            uris.add(data.child("image_url").getValue().toString());
                        }
                        else {

                            refSchools.child(get_school_id).child("requests").child(data.getKey()).child(RELEVANT).setValue(false);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        };

        refSchools.child(get_school_id).child("requests").addValueEventListener(requestListener);
    }

    public void home(View view) {
        Intent intent = new Intent(this, home_screen.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent view_request = new Intent(this, com.example.finalproj_beta2.view_request.class);
        if (mine){
            view_request.putExtra("RQID", my_pending_requests.get(position));
        }
        else{
            view_request.putExtra("RQID", pending_requests.get(position));
        }
        view_request.putExtra("uid", uid);
        view_request.putExtra("lvl", lvl);
        startActivity(view_request);
    }

    public boolean is_relevant(String print_millis) {
        current_millis = System.currentTimeMillis();
        if (current_millis > parseLong(print_millis)){
            return false;
        }
        return true;
    }

    public void onPause() {
        super.onPause();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        refSchools.child(get_school_id).child("requests").removeEventListener(requestListener);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 1){
            mine = true;
            customadp = new CostumeAdapter(getApplicationContext(), my_displays, my_uris);
            pending_requests_lv.setAdapter(customadp);
        }
        else if (position == 0){
            if (lvl.equals("1") || lvl.equals("0")){
                mine = false;
                customadp = new CostumeAdapter(getApplicationContext(), displays, uris);
                pending_requests_lv.setAdapter(customadp);
            }
            else{
                Toast.makeText(this, "No permission", Toast.LENGTH_SHORT).show();
                customadp = new CostumeAdapter(getApplicationContext(), new ArrayList<String>(), new ArrayList<String>());
                pending_requests_lv.setAdapter(customadp);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}