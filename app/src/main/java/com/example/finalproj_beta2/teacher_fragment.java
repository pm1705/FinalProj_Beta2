package com.example.finalproj_beta2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.Request.APPROVED;
import static com.example.finalproj_beta2.Request.COPIES;
import static com.example.finalproj_beta2.Request.DATE_PRINTED;
import static com.example.finalproj_beta2.Request.RELEVANT;
import static com.example.finalproj_beta2.Request.USER_ID;
import static com.example.finalproj_beta2.Request.USER_NAME;
import static com.example.finalproj_beta2.Student.GMAIL;
import static com.example.finalproj_beta2.Student.NAME;
import static com.example.finalproj_beta2.Teacher.LEVEL;
import static java.lang.Long.parseLong;


public class teacher_fragment extends Fragment implements AdapterView.OnItemClickListener{


    ListView teachers_lv;

    ArrayList<String> teachers, teachers_displays;

    CostumeAdapter customadp;

    Long current_millis;
    String get_school_id;
    SharedPreferences settings;

    ArrayList<String> my_uris;
    ArrayList<String> uris;

    FirebaseAuth mAuth;
    FirebaseUser user;

    ValueEventListener requestListener;

    String display;

    String selected_teacher_id, selected_teacher_name = "";

    TextView teacher_info;
    Button remove_btn, make_teacher, make_approver, make_admin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_teacher_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        teachers_lv = getView().findViewById(R.id.stu_list);
        teachers_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        teachers_lv.setOnItemClickListener(this);

        teacher_info = getView().findViewById(R.id.teacher_info);
        remove_btn = getView().findViewById(R.id.remove_btn2);
        make_teacher = getView().findViewById(R.id.make_teacher);
        make_approver = getView().findViewById(R.id.make_approver);
        make_admin = getView().findViewById(R.id.make_admin);

        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_student2(v);
            }
        });

        make_teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_teacher(v);
            }
        });

        make_approver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_approver(v);
            }
        });

        make_admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_admin(v);
            }
        });

        teachers = new ArrayList<String>();
        teachers_displays = new ArrayList<String>();

        uris = new ArrayList<String>();
        my_uris = new ArrayList<String>();

        settings = getActivity().getSharedPreferences("login_info",MODE_PRIVATE);
        get_school_id = settings.getString("school_id", "999999");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        requestListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dS) {
                teachers.clear();
                teachers_displays.clear();
                uris.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    teachers.add(data.getKey());
                    display = "" + data.child(NAME).getValue().toString() + " , ";
                    if (data.child(LEVEL).getValue().toString().equals("2")){
                        display += "Teacher";
                    }
                    if (data.child(LEVEL).getValue().toString().equals("1")){
                        display += "Approver";
                    }
                    if (data.child(LEVEL).getValue().toString().equals("0")){
                        display += "Admin";
                    }
                    teachers_displays.add(display);
                    uris.add(data.child("image_url").getValue().toString());
                }
                customadp = new CostumeAdapter(getActivity().getApplicationContext(),
                        teachers_displays, uris);
                teachers_lv.setAdapter(customadp);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        };

        refSchools.child(get_school_id).child("users").child("teachers").addValueEventListener(requestListener);
        // Inflate the layout for this fragment
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selected_teacher_id = teachers.get(position);
        selected_teacher_name = teachers_displays.get(position);
        teacher_info.setText(selected_teacher_name);
    }

    public void onPause() {
        super.onPause();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        refSchools.child(get_school_id).child("users").child("teachers").removeEventListener(requestListener);
    }

    public void remove_student2(View view){
        if (!selected_teacher_id.equals("")){
            refSchools.child(get_school_id).child("users").child("teachers").child(selected_teacher_id).removeValue();
        }
    }

    public void make_teacher(View view){
        if (!selected_teacher_id.equals("")){
            refSchools.child(get_school_id).child("users").child("teachers").child(selected_teacher_id).child(LEVEL).setValue(2);
        }
    }

    public void make_approver(View view){
        if (!selected_teacher_id.equals("")){
            refSchools.child(get_school_id).child("users").child("teachers").child(selected_teacher_id).child(LEVEL).setValue(1);
        }
    }

    public void make_admin(View view){
        if (!selected_teacher_id.equals("")){
            refSchools.child(get_school_id).child("users").child("teachers").child(selected_teacher_id).child(LEVEL).setValue(0);
        }
    }
}