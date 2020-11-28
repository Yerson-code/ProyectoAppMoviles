package com.appmovil.myappuberclone.retrofit;

import com.appmovil.myappuberclone.modelos.FCMBody;
import com.appmovil.myappuberclone.modelos.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAJUtQH4I:APA91bGMtBkEnIgPzvjAsFLMUEWcYckGDoP9NjU1XT-TEeCNVGMJH-8LMLwFla1Lq7M0AIkRRCeYvhbtrcrZuGaB7bZOsVL3dTAsigym1y3GcD1G5pCV0kA60m_3PJiyJSbzy1FmkwPW"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
