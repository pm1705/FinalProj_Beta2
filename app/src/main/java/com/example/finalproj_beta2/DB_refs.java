package com.example.finalproj_beta2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DB_refs {

    /**
     * @author		Paz Malul <malul.paz@gmail.com>

     * references to the database
     */

    public static FirebaseAuth refAuth=FirebaseAuth.getInstance();

    public static StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance("https://beta-printproj-default-rtdb.europe-west1.firebasedatabase.app");

    public static DatabaseReference refSchools = FBDB.getReference("schools");
}
