package com.appmovil.myappuberclone.datos;

import com.appmovil.myappuberclone.modelos.Cliente;
import com.appmovil.myappuberclone.modelos.Conductor;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ConductorProvider {
    DatabaseReference mDatabse;
    public ConductorProvider(){
            mDatabse = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Conductores");
        }
        //METODOS DE LA API RESTFULL
        public Task<Void> crear(Conductor conductor){
            return  mDatabse.child(conductor.getId()).setValue(conductor);
        }
    public DatabaseReference getDriver(String idDriver) {
        return mDatabse.child(idDriver);
    }
    public Task<Void> actualizar(Conductor driver) {
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", driver.getNombre());
        map.put("image", driver.getImage());
        map.put("marcaVehiculo", driver.getMarcaVehiculo());
        map.put("placaVehiculo", driver.getPlacaVehiculo());
        return mDatabse.child(driver.getId()).updateChildren(map);
    }
}
