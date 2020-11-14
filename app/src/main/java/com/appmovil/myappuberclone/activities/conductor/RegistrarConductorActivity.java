package com.appmovil.myappuberclone.activities.conductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.activities.cliente.RegistrarseActivity;
import com.appmovil.myappuberclone.datos.AuthProvider;
import com.appmovil.myappuberclone.datos.ClienteProvider;
import com.appmovil.myappuberclone.datos.ConductorProvider;
import com.appmovil.myappuberclone.modelos.Cliente;
import com.appmovil.myappuberclone.modelos.Conductor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class RegistrarConductorActivity extends AppCompatActivity {


    AuthProvider mAuthProvider;
    ConductorProvider mConductorProvider;

    //Instanciar vistas
    Button mRegistro;

    TextInputEditText mtxtnombre;
    TextInputEditText mtxtcorreo;
    TextInputEditText mtxtcontraseña;
    TextInputEditText mtxtmarca;
    TextInputEditText mtxtplaca;
    AlertDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_conductor);

        getSupportActionBar().setTitle("Registrar Conductor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //INSTANCIAS DE LOS DATOS
        mAuthProvider=new AuthProvider();
        mConductorProvider=new ConductorProvider();



        //Toast.makeText(this, "El valor que se selecciono fue " + seleccionarusua, Toast.LENGTH_SHORT).show();
        mdialog =new SpotsDialog.Builder().setContext(this).setMessage("Espere un momento").build();
        mRegistro = findViewById(R.id.btnregistro);
        mtxtnombre = findViewById(R.id.txtnombre);
        mtxtcorreo = findViewById(R.id.txtinputcorreoelectr);
        mtxtmarca= findViewById(R.id.txtinputmarca);
        mtxtplaca= findViewById(R.id.txtinputplaca);
        mtxtcontraseña= findViewById(R.id.txtinputcontraseña);

        mRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegistrarUsuario();
            }
        });
    }

    void clickRegistrarUsuario() {
        final String nombre = mtxtnombre.getText().toString();
        final String correo = mtxtcorreo.getText().toString();
        final String marca = mtxtmarca.getText().toString();
        final String placa = mtxtplaca.getText().toString();
        final  String contraseña = mtxtcontraseña.getText().toString();

        if (!nombre.isEmpty() && !correo.isEmpty() && !contraseña.isEmpty() && !placa.isEmpty() && !marca.isEmpty()){
            if (contraseña.length() >=6){
                mdialog.show();
                registrarUsuario(nombre,correo,marca,placa,contraseña);
            }
            else {
                Toast.makeText(this, "La contraseña debe tener almenos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }

    }

    private void registrarUsuario(final String nombre,final String email,final String marca,final String placa, final String password) {
        //REGISTRA LA AUTENTICACION DE USUARIOS
        mAuthProvider.RegistrarAutenticacion(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mdialog.hide();
                if (task.isSuccessful()){
                    String id= FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Conductor conductor=new Conductor(id,nombre,email,marca,placa);
                    registrarConductor(conductor);
                }
                else  {
                    Toast.makeText(RegistrarConductorActivity.this, "No pudo registrarse el usuario ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void registrarConductor(Conductor conductor){
        mConductorProvider.crear(conductor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //Toast.makeText(RegistrarConductorActivity.this, "Registrado exitosamente ", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(),MapConductorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else  {
                    Toast.makeText(RegistrarConductorActivity.this, "No pudo registrarse el cliente ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}