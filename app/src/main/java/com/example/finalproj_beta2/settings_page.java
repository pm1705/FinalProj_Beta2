package com.example.finalproj_beta2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.example.finalproj_beta2.DB_refs.refSchools;
import static com.example.finalproj_beta2.DB_refs.storageReference;

public class settings_page extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * 2/3 of the send request process, here you choose the settings for the print you want to request,
     * And choose the date you want to print it in.
     */

    Spinner sides_spinner, color_spinner, annotation_spinner;
    EditText copies_et;
    boolean side = false, color = false, annotation = false;
    String[] sides = {"both sides", "front side only"};
    String[] colors = {"Black and White", "Colorful"};
    String[] annotations = {"Vertical", "Horizontal"};
    Uri image_uri;
    String requestName, amount, uid, user_name;
    Request newRequest;
    String print_millis;
    String get_school_id;
    SharedPreferences settings;
    FirebaseAuth mAuth;
    FirebaseUser user;
    Button date_picker;
    DateFormat obj = new SimpleDateFormat("dd MMM yyyy");
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        Intent pdf_upload = getIntent();
        image_uri = Uri.parse(pdf_upload.getStringExtra("uri"));
        uid = pdf_upload.getStringExtra("uid");
        user_name = pdf_upload.getStringExtra("user_name");


        sides_spinner = findViewById(R.id.sides_spinner);
        color_spinner = findViewById(R.id.color_spinner);
        copies_et = findViewById(R.id.school_name_et);
        annotation_spinner = findViewById(R.id.annotation_spinner);
        copies_et = findViewById(R.id.school_name_et);

        date_picker = findViewById(R.id.log_in);

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

    // Change the boolean parameters based on the selected item in the spinner.
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

    // Check if every input is okay, then upload the file and the settings and proceed to the last step.
    public void next_page_btn(View view) {

        if (!copies_et.getText().toString().equals("")){
            if (print_millis != null) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading");
                progressDialog.setMessage("Uploading Information...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                requestName = "" + System.currentTimeMillis();
                amount = copies_et.getText().toString();
                StorageReference filepath = storageReference.child(requestName + "pdf");
                filepath.putFile(image_uri).continueWithTask(new Continuation() {
                    @Override
                    public Object then(Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(Task<Uri> task) {
                        if (task.isSuccessful()) {
                            newRequest = new Request(false, true, Integer.parseInt(amount), color, annotation, side, requestName, uid, requestName, print_millis, user_name);
                            refSchools.child(get_school_id).child("requests").child("RQ" + requestName).setValue(newRequest);
                            refSchools.child(get_school_id).child("requests").child("RQ" + requestName).child("image_url").setValue(user.getPhotoUrl().toString());
                            progressDialog.dismiss();
                            Intent final_screen = new Intent(settings_page.this, request_print.class);
                            startActivity(final_screen);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(settings_page.this, "Error occurred, please contact admin", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(this, "Choose A date to print.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Invalid Amount Of Copies", Toast.LENGTH_SHORT).show();
        }
    }

    // The date chooser dialog.
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    //listener for the when the date is picked.
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            Date print_on = new Date(arg1 - 1900, arg2, arg3);
            print_millis = String.valueOf(print_on.getTime());
            date_picker.setText("" + obj.format(new Date(Long.parseLong(print_millis))));
        }
    };

    //open the choose date dialog.
    public void print_time_picker(View view) {
        showDialog(999);
    }
}