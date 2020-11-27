package com.appmovil.myappuberclone.datos;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeoFireProvider {
    private DatabaseReference mDatabase;
    private GeoFire mGeofire;

    public GeoFireProvider(String reference){
        mDatabase= FirebaseDatabase.getInstance().getReference().child(reference);
        mGeofire=new GeoFire(mDatabase);
    }

    public void guardarLocalizacion(String idConductor, LatLng latLng){
        mGeofire.setLocation(idConductor,new GeoLocation( latLng.latitude,latLng.longitude));
    }
    public void eliminarLocalizacion(String idConductor){
        mGeofire.removeLocation(idConductor);
    }

    public GeoQuery obtenerConductoresActivos(LatLng latLng,double radius){
        GeoQuery geoQuery=mGeofire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude),5);
        geoQuery.removeAllListeners();
        return  geoQuery;
    }
}
