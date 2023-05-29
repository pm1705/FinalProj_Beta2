package com.example.finalproj_beta2;

import static com.example.finalproj_beta2.DB_refs.refSchools;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class my_profile extends AppCompatActivity implements View.OnCreateContextMenuListener{

    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * in this activity the user can see certain information about his profile.
     * if the user is an admin he can view his school.
     */

    String get_name, get_school_name, get_url, get_email, get_school_id, lvl, get_school_secret;
    TextView tv_name, tv_school_name, tv_email, tv_school_id, tv_role;
    ImageView pfp, menu_button;
    Button my_school;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile2);

        menu_button = findViewById(R.id.menu_button);
        menu_button.setOnCreateContextMenuListener(this);

        my_school = findViewById(R.id.my_school);

        Intent intent =getIntent();
        get_name =intent.getStringExtra("name");
        get_email =intent.getStringExtra("email");
        get_url =intent.getStringExtra("url");
        get_school_id =intent.getStringExtra("school_code");
        lvl = intent.getStringExtra("lvl");

        if (lvl.equals("0")){
            my_school.setVisibility(View.VISIBLE);
        }
        else{
            my_school.setVisibility(View.INVISIBLE);
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Loading Information...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        refSchools.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if(data.getKey().equals(get_school_id)){
                        get_school_name = data.child("name").getValue().toString();
                        get_school_secret = data.child("secret_code").getValue().toString();
                        tv_school_name.setText(get_school_name);

                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        tv_name = (TextView) findViewById(R.id.name_tv);
        tv_email = (TextView) findViewById(R.id.email_tv);
        pfp = (ImageView) findViewById(R.id.pfp);
        tv_school_name = (TextView) findViewById(R.id.school_name_tv);
        tv_school_id = (TextView) findViewById(R.id.school_code_tv);
        tv_role = (TextView) findViewById(R.id.role_tv);

        tv_name.setText(get_name);
        tv_email.setText(get_email);
        tv_school_id.setText(get_school_id);

        if (lvl.equals("2")) tv_role.setText("Teacher");
        else if (lvl.equals("1")) tv_role.setText("Reviewer");
        else if (lvl.equals("0")) tv_role.setText("Admin");
        else if (lvl.equals("3")) tv_role.setText("Student");

        Picasso.get().load(Uri.parse(get_url)).into(pfp);
    }

    // this transfers the *ADMIN* to the my school activity
    public void my_school(View view) {
        Intent intent = new Intent(this, my_school.class);
        intent.putExtra("school_id", get_school_id);
        intent.putExtra("school_name", get_school_name);
        intent.putExtra("school_secret", get_school_secret);
        startActivity(intent);
    }

    //show the menu
    public void menu_show(View view) {
        this.openContextMenu(view);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Menu");
        menu.add("Home");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        String oper=item.getTitle().toString();
        if (oper.equals("Home")) {
            finish();
        }

        return true;
    }
}