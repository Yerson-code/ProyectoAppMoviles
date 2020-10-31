package com.appmovil.myappuberclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.appmovil.myappuberclone.R;

import static android.content.SharedPreferences.*;

public class MainActivity extends AppCompatActivity {

    Button mButtuonconductor;
    Button getmButtuoncliente;

    SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Pedido de Taxi");

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        final Editor editor= mPref.edit();

        mButtuonconductor = findViewById(R.id.btnconductor);
        getmButtuoncliente = findViewById(R.id.btncliente);

        mButtuonconductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToselectAuth();
                editor.putString("user","driver");
                editor.apply();
            }
        });

        getmButtuoncliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToselectAuth();
                editor.putString("user","client");
                editor.apply();

            }
        });
    }

    private void goToselectAuth() {
        Intent intent = new Intent(MainActivity.this, SeleccionarOpcionActivity.class);
        startActivity(intent);
    }
}