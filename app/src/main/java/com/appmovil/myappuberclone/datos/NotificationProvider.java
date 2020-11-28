package com.appmovil.myappuberclone.datos;

import com.appmovil.myappuberclone.modelos.FCMBody;
import com.appmovil.myappuberclone.modelos.FCMResponse;
import com.appmovil.myappuberclone.retrofit.IFCMApi;
import com.appmovil.myappuberclone.retrofit.retrofitCliente;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }

    public Call<FCMResponse> sendNotification(FCMBody body) {
        return retrofitCliente.getClientObject(url).create(IFCMApi.class).send(body);
    }
}
