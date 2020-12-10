package com.appmovil.myappuberclone.activities.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.activities.conductor.MapConductorActivity;
import com.appmovil.myappuberclone.datos.AuthProvider;
import com.appmovil.myappuberclone.datos.ClienteProvider;
import com.appmovil.myappuberclone.modelos.Cliente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dmax.dialog.SpotsDialog;

public class RegistrarseActivity extends AppCompatActivity {

    AuthProvider mAuthProvider;
    ClienteProvider mClienteProvider;


    //Instanciar vistas
    Button mRegistro;

    TextInputEditText mtxtnombre;
    TextInputEditText mtxtcorreo;
    TextInputEditText mtxtcontraseña;
    AlertDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        getSupportActionBar().setTitle("Registrar Cliente");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //INSTANCIAS DE LOS DATOS
        mAuthProvider=new AuthProvider();
        mClienteProvider=new ClienteProvider();



      //  Toast.makeText(this, "El valor que se selecciono fue " + seleccionarusua, Toast.LENGTH_SHORT).show();
        mdialog =new SpotsDialog.Builder().setContext(this).setMessage("Espere un momento").build();
        mRegistro = findViewById(R.id.btnregistro);
        mtxtnombre = findViewById(R.id.txtnombre);
        mtxtcorreo = findViewById(R.id.txtinputcorreoelectr);
        mtxtcontraseña= findViewById(R.id.txtinputcontraseña);

        mRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegistrarUsuario();
            }
        });
    }
    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
     void clickRegistrarUsuario() {
        final String nombre = mtxtnombre.getText().toString();
         final String correo = mtxtcorreo.getText().toString();
       final  String contraseña = mtxtcontraseña.getText().toString();

       if (!nombre.isEmpty() && !correo.isEmpty() && !contraseña.isEmpty()){
           if (!validarEmail(mtxtcorreo.getText().toString())){
               mensajeError("Email no válido");
               return;
           }
            if (contraseña.length() >=6){
                mdialog.show();
             registrarUsuario(nombre,correo,contraseña);
            }
            else {
               mensajeError("La contraseña debe tener al menos 6 caracteres");
            }
        }
        else {
           mensajeError("No se permiten campos vacios");
        }

    }

    private void registrarUsuario(final String nombre,final String email, final String password) {
       //REGISTRA LA AUTENTICACION DE USUARIOS
        mAuthProvider.RegistrarAutenticacion(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mdialog.hide();
                if (task.isSuccessful()){
                    String id=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Cliente client=new Cliente(id,nombre,email);
                   registrarCliente(client);
                }
                else  {
                   mensajeError("No se pudo registrar usuario");
                }
            }
        });
    }

    void registrarCliente(Cliente cliente){
        mClienteProvider.crear(cliente).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //Toast.makeText(RegistrarseActivity.this, "Registrado exitosamente ", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getApplicationContext(), MapClienteActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else  {
                   mensajeError("No se pudo registrar cliente");
                }
            }
        });
    }
    /*
    private void guardarUsario(String id, String nombre, String correo) {
        String seleccionar = mPref.getString("user", "");
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        if (seleccionar.equals("driver")){

            mDatabse.child("Usuarios").child("Conductores").child(id).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
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
            mDatabse.child("Usuarios").child("Clientes").child(id).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
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
     */
    private void mensajeError(String mensaje) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText(mensaje)
                .show();
    }

}