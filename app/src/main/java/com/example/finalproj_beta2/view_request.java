package com.example.finalproj_beta2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.Request.APPROVED;
import static com.example.finalproj_beta2.Request.COLORFUL;
import static com.example.finalproj_beta2.Request.COPIES;
import static com.example.finalproj_beta2.Request.DATE_PRINTED;
import static com.example.finalproj_beta2.Request.DATE_REQUESTED;
import static com.example.finalproj_beta2.Request.DOUBLE_SIDED;
import static com.example.finalproj_beta2.Request.RELEVANT;
import static com.example.finalproj_beta2.Request.USER_NAME;
import static com.example.finalproj_beta2.Request.VERTICAL;
import static com.example.finalproj_beta2.Teacher.GMAIL;
import static com.example.finalproj_beta2.Teacher.LEVEL;
import static com.example.finalproj_beta2.Teacher.NAME;

public class view_request extends AppCompatActivity {

    String RQID, body1txt, body2txt, lvl;
    TextView title1, body1, body2;
    Button accept1, decline1;

    String get_school_id;
    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);

        Intent prev = getIntent();
        RQID = prev.getStringExtra("RQID");
        lvl = prev.getStringExtra("lvl");

        title1 = (TextView) findViewById(R.id.title1);
        body1 = (TextView) findViewById(R.id.body1);
        body2 = (TextView) findViewById(R.id.body2);

        title1.setText(RQID);

        accept1 = findViewById(R.id.accept_btn);
        decline1 = findViewById(R.id.decline_btn);

        if (lvl.equals("2")){
            accept1.setVisibility(View.INVISIBLE);
            decline1.setVisibility(View.INVISIBLE);
        }

        settings = getSharedPreferences("login_info",MODE_PRIVATE);
        get_school_id = settings.getString("school_id", "999999");

        refSchools.child(get_school_id).child("requests").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (data.getKey().equals(RQID)) {
                        title1.setText("Request by " + data.child(USER_NAME).getValue().toString());
                        body1txt = "Copies: " + data.child(COPIES).getValue().toString();
                        if ((boolean) data.child(COLORFUL).getValue()){
                            body1txt += "\nColorful toner";
                        }
                        else{
                            body2txt += "\nBlack and white toner";
                        }

                        if ((boolean) data.child(VERTICAL).getValue()){
                            body1txt += "\nVertical orientation";
                        }
                        else{
                            body2txt += "\nHorizontal orientation";
                        }

                        if ((boolean) data.child(DOUBLE_SIDED).getValue()){
                            body1txt += "\nPrinting on both sides";
                        }
                        else{
                            body1txt += "\nPrinting on one side";
                        }

                        body1.setText(body1txt);

                        body2.setText("Requested on: " + data.child(DATE_REQUESTED).getValue().toString()
                                + "\n Print on: " + data.child(DATE_PRINTED).getValue().toString());

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    public void decline(View view) {
        refSchools.child(get_school_id).child("requests").child(RQID).child(APPROVED).setValue(false);
        refSchools.child(get_school_id).child("requests").child(RQID).child(RELEVANT).setValue(false);
        finish();
    }

    public void accept(View view) {
        refSchools.child(get_school_id).child("requests").child(RQID).child(APPROVED).setValue(true);
        refSchools.child(get_school_id).child("requests").child(RQID).child(RELEVANT).setValue(true);
        finish();
    }
}