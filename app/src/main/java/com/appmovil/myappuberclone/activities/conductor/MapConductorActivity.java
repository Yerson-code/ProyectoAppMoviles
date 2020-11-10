package com.appmovil.myappuberclone.activities.conductor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.appmovil.myappuberclone.R;

public class MapConductorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_conductor);
        getSupportActionBar().setTitle("Mapa Conductor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}