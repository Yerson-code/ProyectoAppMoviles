package com.appmovil.myappuberclone.datos;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

//CLASE ENCARGADA DE LA AUTENTICACION DE USUARIOS
public class AuthProvider {
    FirebaseAuth mAuth;
    public  AuthProvider() {
        //BASE DE DATOS EN TIEMPO REAL
        mAuth = FirebaseAuth.getInstance();
    }
    public Task<AuthResult> RegistrarAutenticacion(String email, String password){
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> Login(String email, String password){
        return mAuth.signInWithEmailAndPassword(email, password);
    }
    //METODO PARA CERRAR SESION EN FIREBASE
    public void cerrarSesion(){
        mAuth.signOut();
    }
    public String obtenerIdConductor(){
        return mAuth.getInstance().getCurrentUser().getUid();
    }
    public boolean existSesion(){
        boolean existe=false;
        if(mAuth.getCurrentUser()!=null){
           existe=true;
        }
        return existe;
    }
}
