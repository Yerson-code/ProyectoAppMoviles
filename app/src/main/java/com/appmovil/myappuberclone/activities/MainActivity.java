package com.appmovil.myappuberclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.activities.cliente.MapClienteActivity;
import com.appmovil.myappuberclone.activities.conductor.MapConductorActivity;
import com.google.firebase.auth.FirebaseAuth;

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
    //METODO DEL CICLO DE VIDA DE UN ACTITITY

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            String seleccionUsuario=mPref.getString("user","");
            if(seleccionUsuario.equals("client")){
                Intent intent=new Intent(getApplicationContext(), MapClienteActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else{
                Intent intent=new Intent(getApplicationContext(), MapConductorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }
}