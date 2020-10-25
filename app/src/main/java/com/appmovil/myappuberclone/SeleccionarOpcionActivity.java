package com.appmovil.myappuberclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SeleccionarOpcionActivity extends AppCompatActivity {

    Toolbar mToolbar;
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
        Intent intent = new Intent(SeleccionarOpcionActivity.this, RegistrarseActivity.class);
        startActivity(intent);
    }

}