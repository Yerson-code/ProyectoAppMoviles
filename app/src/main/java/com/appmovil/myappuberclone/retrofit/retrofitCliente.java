package com.appmovil.myappuberclone.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class retrofitCliente
{
    private  static Retrofit retrofit;

    public static  Retrofit getClient(String URL){
        if(retrofit==null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

        }
        return  retrofit;
    }
}
