package com.example.finalproj_beta2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.DB_refs.storageReference;

public class settings_page_student extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Spinner sides_spinner, color_spinner, annotation_spinner;
    EditText copies_et;
    boolean side = true, color = false, annotation = true;

    String[] sides = {"both sides", "front side only"};
    String[] colors = {"Black and White", "Colorful"};
    String[] annotations = {"Vertical", "Horizontal"};

    Uri image_uri;

    String requestName, amount, uid, user_name;
    Student_print newStudent_print;

    String print_millis;
    String get_school_id;
    SharedPreferences settings;

    FirebaseAuth mAuth;
    FirebaseUser user;

    int page_count;
    double cost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page_student);

        Intent pdf_upload = getIntent();
        image_uri = Uri.parse(pdf_upload.getStringExtra("uri"));
        uid = pdf_upload.getStringExtra("uid");
        user_name = pdf_upload.getStringExtra("user_name");
        page_count = pdf_upload.getIntExtra("page_count",1);


        sides_spinner = findViewById(R.id.sides_spinner);
        color_spinner = findViewById(R.id.color_spinner);
        copies_et = findViewById(R.id.copies_et);
        annotation_spinner = findViewById(R.id.annotation_spinner);
        copies_et = findViewById(R.id.copies_et);

        sides_spinner.setOnItemSelectedListener(this);
        color_spinner.setOnItemSelectedListener(this);
        annotation_spinner.setOnItemSelectedListener(this);

        settings = getSharedPreferences("login_info",MODE_PRIVATE);
        get_school_id = settings.getString("school_id", "999999");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        ArrayAdapter<String> sides_adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, sides);
        sides_spinner.setAdapter(sides_adapter);

        ArrayAdapter<String> colors_adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, colors);
        color_spinner.setAdapter(colors_adapter);

        ArrayAdapter<String> annotations_adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, annotations);
        annotation_spinner.setAdapter(annotations_adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long rowid) {
        if (parent.getId() == R.id.sides_spinner){
            if (pos == 0) side = true;
            else side = false;
        }
        else if (parent.getId() == R.id.color_spinner){
            if (pos == 0) color = false;
            else color = true;
        }
        else if (parent.getId() == R.id.annotation_spinner){
            if (pos == 0) annotation = true;
            else annotation = false;
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void next_page_btn(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("this print will cost - " + calculate_cost() + " credits, are you sure you want to print?");

        // Set the positive button
        builder.setPositiveButton("Print", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestName = "" + System.currentTimeMillis();
                Date res = new Date(System.currentTimeMillis());
                System.out.println("------- " + res.getHours() + ":" + res.getMinutes());
                amount = copies_et.getText().toString();

                StorageReference filepath = storageReference.child(requestName + "pdf");
                filepath.putFile(image_uri).continueWithTask(new Continuation() {
                    @Override
                    public Object then(Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            System.out.println("here");
                            throw task.getException();
                        }
                        System.out.println("here2");
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(Task<Uri> task) {
                        if (task.isSuccessful()) {
                            newStudent_print = new Student_print(false, Integer.parseInt(amount), color, annotation, side, requestName, uid, requestName, user_name);
                            refSchools.child(get_school_id).child("student_prints").child("SP" + requestName).setValue(newStudent_print);
                            refSchools.child(get_school_id).child("student_prints").child("SP" + requestName).child("image_url").setValue(user.getPhotoUrl().toString());
                            Intent final_screen = new Intent(settings_page_student.this, confirm_student_print.class);
                            startActivity(final_screen);
                        } else {
                            Toast.makeText(settings_page_student.this,"Error occurred, please contact admin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Set the negative button
        builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public double calculate_cost(){
        cost = page_count;
        if (color) cost = cost*5;
        if (side) cost = cost*0.5;
        cost = cost*Integer.parseInt(copies_et.getText().toString());
        System.out.println("cost " + cost);
        System.out.println(side + " " + color + " " + copies_et.getText().toString() + " " + page_count);
        return cost;
    }
}
