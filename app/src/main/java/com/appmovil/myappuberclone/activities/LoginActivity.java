package com.appmovil.myappuberclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.activities.cliente.MapClienteActivity;
import com.appmovil.myappuberclone.activities.conductor.MapConductorActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText mTxtcorreo;
    TextInputEditText mTxtpasword;
    Button mPLogin;
    private CircleImageView mCircleImageBack;

    FirebaseAuth mAuth;
    DatabaseReference mDtabase;

    AlertDialog  mdialog;
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTxtcorreo = findViewById(R.id.txtinputcorreo);
        mTxtpasword = findViewById(R.id.txtinputpassword);

        mPLogin = findViewById(R.id.btnPrinLogin);
        mPref = getApplicationContext().getSharedPreferences("typeUser", MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mDtabase = FirebaseDatabase.getInstance().getReference();

        mdialog =new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momento").build();

        mCircleImageBack = findViewById(R.id.circleImageBack);
        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrincipalLogin();
            }
        });
    }

    private void PrincipalLogin() {
        String email = mTxtcorreo.getText().toString();
        String password = mTxtpasword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()){
            if (password.length() >=6){
                mdialog.show();
                //METODO DE FIREBASE PARA ENVIAR EL USUARIO Y LA CLAVE A LA BASE DE DATOS
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Toast.makeText(LoginActivity.this, "Su autenticacion se realizo con exito", Toast.LENGTH_SHORT ).show();
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
                        else {
                            Toast.makeText(LoginActivity.this, "El email y/o el password son incorrectos", Toast.LENGTH_SHORT ).show();

                        }
                        mdialog.dismiss();
                    }
                });

            }
            else{
                Toast.makeText(this, "La contraseña debe tener mas de 6 caracteres", Toast.LENGTH_SHORT ).show();

            }
        }
        else {
            Toast.makeText(this, "La contraseña y el email son obligatorios", Toast.LENGTH_SHORT ).show();

        }
    }
}