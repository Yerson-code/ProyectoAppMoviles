package com.appmovil.myappuberclone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.activities.cliente.RegistrarseActivity;
import com.appmovil.myappuberclone.activities.conductor.RegistrarConductorActivity;

public class SeleccionarOpcionActivity extends AppCompatActivity {

    Toolbar mToolbar;
    SharedPreferences mPref;
    Button mButtonLogin;
    Button mRegistrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_opcion);

      /*  mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);*/
        getSupportActionBar().setTitle("Seleccionar opcion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);


        mButtonLogin = findViewById(R.id.btnlogin);
        mRegistrar = findViewById(R.id.btnregistrarse);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        mRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resgistrar();
            }
        });

    }

    public void Login() {
        Intent intent = new Intent(SeleccionarOpcionActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    public void resgistrar() {
        String  seleccionarusua = mPref.getString("user","");
        if (seleccionarusua.equals("driver"))
        {
            Intent intent = new Intent(SeleccionarOpcionActivity.this, RegistrarseActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(SeleccionarOpcionActivity.this, RegistrarConductorActivity.class);
            startActivity(intent);
        }

    }

}