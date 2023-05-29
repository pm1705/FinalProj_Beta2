package com.example.finalproj_beta2;

import android.app.ProgressDialog;
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

    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * the teacher fragment of the my_school activity.
     */

    ListView teachers_lv;
    ArrayList<String> teachers, teachers_displays, levels;
    Boolean can_edit = true;
    CostumeAdapterUser customadp;
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
    Button remove_btn, make_teacher, make_reviewer;
    ProgressDialog progressDialog;

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
        make_reviewer = getView().findViewById(R.id.make_reviewer);

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

        make_reviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                make_reviewer(v);
            }
        });

//        make_admin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                make_admin(v);
//            }
//        });

        teachers = new ArrayList<String>();
        teachers_displays = new ArrayList<String>();
        levels = new ArrayList<String>();

        uris = new ArrayList<String>();
        my_uris = new ArrayList<String>();

        settings = getActivity().getSharedPreferences("login_info",MODE_PRIVATE);
        get_school_id = settings.getString("school_id", "999999");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        selected_teacher_id = "";
        selected_teacher_name = "";

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading Information...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        requestListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dS) {
                teachers.clear();
                teachers_displays.clear();
                uris.clear();
                levels.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    teachers.add(data.getKey());
                    display = "" + data.child(NAME).getValue().toString() + " - ";
                    levels.add(data.child(LEVEL).getValue().toString());
                    if (data.child(LEVEL).getValue().toString().equals("2")){
                        display += "Teacher";
                    }
                    if (data.child(LEVEL).getValue().toString().equals("1")){
                        display += "Reviewer";
                    }
                    if (data.child(LEVEL).getValue().toString().equals("0")){
                        display += "Admin";
                    }
                    teachers_displays.add(display);
                    uris.add(data.child("image_url").getValue().toString());
                }
                customadp = new CostumeAdapterUser(getActivity().getApplicationContext(),
                        teachers_displays, uris);
                teachers_lv.setAdapter(customadp);

                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        };

        refSchools.child(get_school_id).child("users").child("teachers").addValueEventListener(requestListener);
    }

    // When item is clicked change the selected... variables to help promote/demote/remove the teacher.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selected_teacher_id = teachers.get(position);
        selected_teacher_name = teachers_displays.get(position);
        can_edit = !levels.get(position).equals("0");
        teacher_info.setText(selected_teacher_name);
    }

    public void onPause() {
        super.onPause();
        refSchools.child(get_school_id).child("users").child("teachers").removeEventListener(requestListener);
    }

    public void onStop() {
        super.onStop();
        refSchools.child(get_school_id).child("users").child("teachers").removeEventListener(requestListener);
    }

    public void onResume() {
        super.onResume();
        refSchools.child(get_school_id).child("users").child("teachers").addValueEventListener(requestListener);
    }

    // Remove selected teacher from the school
    public void remove_student2(View view){
        if (!selected_teacher_id.equals("")){
            if (can_edit){
                refSchools.child(get_school_id).child("users").child("teachers").child(selected_teacher_id).removeValue();
                selected_teacher_name = "";
                selected_teacher_id = "";
            }
            else{
                Toast.makeText(getContext(), "Can not edit ADMIN.", Toast.LENGTH_SHORT).show();

            }
        }
    }

    // Demote selected teacher to teacher
    public void make_teacher(View view){
        if (!selected_teacher_id.equals("")){
            if (can_edit){
                refSchools.child(get_school_id).child("users").child("teachers").child(selected_teacher_id).child(LEVEL).setValue(2);

            }
            else{
                Toast.makeText(getContext(), "Can not edit ADMIN.", Toast.LENGTH_SHORT).show();

            }        }
    }

    // Promote selected teacher to Reviewer
    public void make_reviewer(View view){
        if (!selected_teacher_id.equals("")){
            if (can_edit){
                refSchools.child(get_school_id).child("users").child("teachers").child(selected_teacher_id).child(LEVEL).setValue(1);

            }
            else{
                Toast.makeText(getContext(), "Can not edit ADMIN.", Toast.LENGTH_SHORT).show();

            }
        }
    }
}