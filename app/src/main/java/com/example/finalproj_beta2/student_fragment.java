package com.example.finalproj_beta2;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.Student.NAME;
import static java.lang.Long.parseLong;


public class student_fragment extends Fragment implements AdapterView.OnItemClickListener{

    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * the student fragment of the my_school activity.
     */

    ListView students_lv;
    ArrayList<String> students, students_displays;
    CostumeAdapterUser customadp;
    String get_school_id;
    SharedPreferences settings;
    ArrayList<String> my_uris;
    ArrayList<String> uris;
    FirebaseAuth mAuth;
    FirebaseUser user;
    ValueEventListener requestListener;
    String selected_student_id = "", selected_student_name = "";
    TextView student_info;
    Button remove_btn;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_student_fragment, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        students_lv = getView().findViewById(R.id.stu_list);
        students_lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        students_lv.setOnItemClickListener(this);

        student_info = getView().findViewById(R.id.student_info);
        remove_btn = getView().findViewById(R.id.remove_btn2);

        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_student(v);
            }
        });

        students = new ArrayList<String>();
        students_displays = new ArrayList<String>();

        uris = new ArrayList<String>();
        my_uris = new ArrayList<String>();

        settings = getActivity().getSharedPreferences("login_info",MODE_PRIVATE);
        get_school_id = settings.getString("school_id", "999999");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading Information...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        requestListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dS) {
                students.clear();
                students_displays.clear();
                uris.clear();
                for(DataSnapshot data : dS.getChildren()) {
                    students.add(data.getKey());
                    students_displays.add("" + data.child(NAME).getValue().toString());
                    uris.add(data.child("image_url").getValue().toString());
                }
                if (students.size() != 0){
                    selected_student_id = students.get(0);
                    selected_student_name = students_displays.get(0);
                    student_info.setText(selected_student_name);
                }
                customadp = new CostumeAdapterUser(getActivity().getApplicationContext(),
                        students_displays, uris);
                students_lv.setAdapter(customadp);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        };

        refSchools.child(get_school_id).child("users").child("students").addValueEventListener(requestListener);
    }

    // When item is clicked change the selected... variables to help remove the student.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selected_student_id = students.get(position);
        selected_student_name = students_displays.get(position);
        student_info.setText(selected_student_name);
    }

    public void onPause() {
        super.onPause();
        refSchools.child(get_school_id).child("users").child("students").removeEventListener(requestListener);
    }

    public void onStop() {
        super.onStop();
        refSchools.child(get_school_id).child("users").child("students").removeEventListener(requestListener);
    }

    public void onResume() {
        super.onResume();
        refSchools.child(get_school_id).child("users").child("students").addValueEventListener(requestListener);
    }

    // Remove selected teacher from the school
    public void remove_student(View view){
        if (!selected_student_id.equals("")){
            refSchools.child(get_school_id).child("users").child("students").child(selected_student_id).removeValue();
            selected_student_name = "";
            selected_student_id = "";
        }
    }
}