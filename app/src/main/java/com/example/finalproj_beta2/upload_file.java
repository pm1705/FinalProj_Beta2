package com.example.finalproj_beta2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.Nullable;

import java.io.File;

import static com.example.finalproj_beta2.DB_refs.storageReference;

public class upload_file extends AppCompatActivity {

    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * 1/3 of the send request process, here the user (Teacher or Student) uploads a PDF to print.
     */

    Button upload_file_btn, next_page_btn;
    Uri image_uri;
    PDFView pdfView;
    String uid, user_name;
    String student_or_teacher;
    String credits;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        upload_file_btn = findViewById(R.id.upload_file_btn);
        next_page_btn = findViewById(R.id.next_page);
        next_page_btn.setVisibility(View.INVISIBLE);

        pdfView = findViewById(R.id.pdfView);

        Intent home = getIntent();
        uid = home.getStringExtra("uid");
        user_name = home.getStringExtra("user_name");
        student_or_teacher = home.getStringExtra("student_or_teacher");

        if (student_or_teacher.equals("student")){
            credits = home.getStringExtra("credits");
        }
    }

    // Open the local storage file selection activity, restrict to PDF only.
    public void upload_func(View view) {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        // Restrict uploads only to Pdfs.
        galleryIntent.setType("application/pdf");
        startActivityForResult(galleryIntent, 1);
    }

    //get the selected pdf path and display it in the pdfView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            image_uri = data.getData();
            File file = new File(String.valueOf(image_uri));
            file.setReadable(true, true);

            // Show loading Dialog Box
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");
            dialog.show();

            pdfView.fromUri(image_uri)
                    .enableSwipe(true) // allows to block changing pages using swipe
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                    .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                    // spacing between pages in dp. To define spacing color, set view background
                    .spacing(0)
                    .load();

            next_page_btn.setVisibility(View.VISIBLE);
            dialog.dismiss();
        }
    }

    // Continue to the settings activity, different for students and teachers.
    public void next_page_func(View view) {
        if (student_or_teacher.equals("teacher")){
            Intent settings_page_intent = new Intent(this, settings_page.class);
            settings_page_intent.putExtra("uri", image_uri.toString());
            settings_page_intent.putExtra("uid", uid);
            settings_page_intent.putExtra("user_name", user_name);
            settings_page_intent.putExtra("page_count", pdfView.getPageCount());
            startActivity(settings_page_intent);
        }
        else if(student_or_teacher.equals("student")){
            Intent settings_page_intent = new Intent(this, settings_page_student.class);
            settings_page_intent.putExtra("uri", image_uri.toString());
            settings_page_intent.putExtra("uid", uid);
            settings_page_intent.putExtra("user_name", user_name);
            settings_page_intent.putExtra("credits", credits);
            settings_page_intent.putExtra("page_count", pdfView.getPageCount());
            startActivity(settings_page_intent);
        }
    }
}