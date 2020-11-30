package com.appmovil.myappuberclone.activities.cliente;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.datos.AuthProvider;
import com.appmovil.myappuberclone.datos.ClientBookingProvider;
import com.appmovil.myappuberclone.datos.ConductorProvider;
import com.appmovil.myappuberclone.datos.GeoFireProvider;
import com.appmovil.myappuberclone.datos.GoogleApiProvider;
import com.appmovil.myappuberclone.datos.TokenProvider;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

public class MapClientBookingActivity extends AppCompatActivity {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;

    private GeoFireProvider mGeofireProvider;
    private TokenProvider mTokenProvider;
    private ClientBookingProvider mClientBookingProvider;
    private ConductorProvider mDriverProvider;
    private GoogleApiProvider mGoogleApiProvider;
    private Marker mMarkerDriver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver_booking);
        getSupportActionBar().setTitle("Viaje Cliente");
        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeoFireProvider("drivers_working");
        mTokenProvider = new TokenProvider();
        mClientBookingProvider = new ClientBookingProvider();
        mGoogleApiProvider = new GoogleApiProvider(MapClientBookingActivity.this);
        mDriverProvider = new ConductorProvider();
    }
}