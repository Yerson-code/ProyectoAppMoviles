package com.appmovil.myappuberclone.datos;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InfoProvider {
    DatabaseReference mDatabase;

    public InfoProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("info");
    }

    public DatabaseReference getInfo(){
        return mDatabase;
    }
}
