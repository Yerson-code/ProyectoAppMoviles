package com.appmovil.myappuberclone.datos;

import com.appmovil.myappuberclone.modelos.Cliente;
import com.appmovil.myappuberclone.modelos.Conductor;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ConductorProvider {
    DatabaseReference mDatabse;
    public ConductorProvider(){
            mDatabse = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Conductores");
        }
        //METODOS DE LA API RESTFULL
        public Task<Void> crear(Conductor conductor){
            return  mDatabse.child(conductor.getId()).setValue(conductor);
        }

}
