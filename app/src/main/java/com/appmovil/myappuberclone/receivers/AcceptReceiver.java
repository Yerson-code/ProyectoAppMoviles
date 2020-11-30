package com.appmovil.myappuberclone.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.appmovil.myappuberclone.activities.conductor.MapDriverBookingActivity;
import com.appmovil.myappuberclone.datos.AuthProvider;
import com.appmovil.myappuberclone.datos.ClientBookingProvider;
import com.appmovil.myappuberclone.datos.GeoFireProvider;


public class AcceptReceiver extends BroadcastReceiver {

    private ClientBookingProvider mClientBookingProvider;
    private GeoFireProvider mGeofireProvider;
    private AuthProvider mAuthProvider;

    @Override
    public void onReceive(Context context, Intent intent) {

        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeoFireProvider("Conductores_Activos");
        mGeofireProvider.eliminarLocalizacion(mAuthProvider.obtenerIdConductor());

        String idClient = intent.getExtras().getString("idClient");
        mClientBookingProvider = new ClientBookingProvider();
        mClientBookingProvider.updateStatus(idClient, "accept");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);

        Intent intent1 = new Intent(context, MapDriverBookingActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("idClient", idClient);
        context.startActivity(intent1);



    }

}
