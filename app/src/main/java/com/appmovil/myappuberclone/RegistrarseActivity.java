package com.appmovil.myappuberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import modelos.Usuario;

public class RegistrarseActivity extends AppCompatActivity {

    SharedPreferences mPref;
    FirebaseAuth mAuth;
    DatabaseReference mDatabse;

    //Instanciar vistas
    Button mRegistro;

    TextInputEditText mtxtnombre;
    TextInputEditText mtxtcorreo;
    TextInputEditText mtxtcontraseña;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        getSupportActionBar().setTitle("Seleccionar opcion");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mDatabse = FirebaseDatabase.getInstance().getReference();

        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        String  seleccionarusua = mPref.getString("user","");
        Toast.makeText(this, "El valor que se selecciono fue " + seleccionarusua, Toast.LENGTH_SHORT).show();

        mRegistro = findViewById(R.id.btnregisstro);
        mtxtnombre = findViewById(R.id.txtnombre);
        mtxtcorreo = findViewById(R.id.txtinputcorreoelectr);
        mtxtcontraseña= findViewById(R.id.txtinputcontraseña);

        mRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resgistrousuario();
            }
        });
    }

     void resgistrousuario() {
        final String nombre = mtxtnombre.getText().toString();
         final String correo = mtxtcorreo.getText().toString();
       final  String contraseña = mtxtcontraseña.getText().toString();

       if (!nombre.isEmpty() && !correo.isEmpty() && !contraseña.isEmpty()){
            if (contraseña.length() >=6){

                mAuth.createUserWithEmailAndPassword(correo, contraseña).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            guardarusario(nombre, correo);
                        }
                        else  {
                            Toast.makeText(RegistrarseActivity.this, "No pudo registrarse el usuario ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "La contraseña debe tener almenos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void guardarusario(String nombre, String correo) {
        String seleccionar = mPref.getString("user", "");
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        if (seleccionar.equals("driver")){

            mDatabse.child("usuario").child("drivers").push().setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegistrarseActivity.this, "Registro  Exitoso", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(RegistrarseActivity.this, "Fallo el registro", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
        else if (seleccionar.equals("client")){
            mDatabse.child("user").child("clients").push().setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegistrarseActivity.this, "Registro Exitoso", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(RegistrarseActivity.this, "Fallo el registro", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }
}