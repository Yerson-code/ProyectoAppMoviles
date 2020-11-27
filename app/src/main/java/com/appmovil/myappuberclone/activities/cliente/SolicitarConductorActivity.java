package com.appmovil.myappuberclone.activities.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.appmovil.myappuberclone.R;

public class SolicitarConductorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_conductor);
        getSupportActionBar().setTitle("Solicitar conductor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}