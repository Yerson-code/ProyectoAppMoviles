package com.appmovil.myappuberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText mTxtcorreo;
    TextInputEditText mTxtpasword;
    Button mPLogin;

    FirebaseAuth mAuth;
    DatabaseReference mDtabase;

    AlertDialog  mdialog;

    Toolbar mToobar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mTxtcorreo = findViewById(R.id.txtinputcorreo);
        mTxtpasword = findViewById(R.id.txtinputpassword);
        mPLogin = findViewById(R.id.btnPrinLogin);

        mAuth = FirebaseAuth.getInstance();
        mDtabase = FirebaseDatabase.getInstance().getReference();

        mdialog =new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momemnto").build();

        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrincipalLogim();
            }
        });
    }

    private void PrincipalLogim() {
        String email = mTxtcorreo.getText().toString();
        String password = mTxtpasword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()){
            if (password.length() >=6){
                mdialog.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Datos Ingresados correctamente", Toast.LENGTH_SHORT ).show();

                        }
                        else {
                            Toast.makeText(LoginActivity.this, "La contraseña o el password son incorrectos", Toast.LENGTH_SHORT ).show();

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