package com.appmovil.myappuberclone.datos;

import com.appmovil.myappuberclone.modelos.Cliente;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class ClienteProvider {
    DatabaseReference mDatabse;
    public ClienteProvider(){
        mDatabse = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Clientes");
    }
    //METODOS DE LA API RESTFULL
    public Task<Void>crear(Cliente cliente){
        Map<String,Object>map=new HashMap<>();
        map.put("nombre",cliente.getNombre());
        map.put("email",cliente.getCorreo());
        return  mDatabse.child(cliente.getId()).setValue(map);
    }
}
