package com.appmovil.myappuberclone.activities.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.datos.GeoFireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;

public class SolicitarConductorActivity extends AppCompatActivity {
    private LottieAnimationView mAnimation;
    private TextView mTextViewLookingFor;
    private Button mButtonCancelRequest;
    private GeoFireProvider mGeofireProvider;

    private String mExtraOrigin;
    private String mExtraDestination;
    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;
    private LatLng mOriginLatLng;
    private LatLng mDestinationLatLng;
    private double mRadius = 0.1;

    private boolean mDriverFound = false;
    private String  mIdDriverFound = "";
    private LatLng mDriverFoundLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_conductor);
        getSupportActionBar().setTitle("Solicitar conductor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAnimation = findViewById(R.id.animation);
        mTextViewLookingFor = findViewById(R.id.textViewLookingFor);
        mButtonCancelRequest = findViewById(R.id.btnCancelRequest);
        mAnimation.playAnimation();

        mExtraOrigin = getIntent().getStringExtra("origin");
        mExtraDestination = getIntent().getStringExtra("destination");
        mExtraOriginLat = getIntent().getDoubleExtra("origin_lat", 0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lng", 0);
        mExtraDestinationLat = getIntent().getDoubleExtra("destination_lat", 0);
        mExtraDestinationLng = getIntent().getDoubleExtra("destination_lng", 0);
        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mDestinationLatLng= new LatLng(mExtraDestinationLat, mExtraDestinationLng);

        mGeofireProvider = new GeoFireProvider("Conductores_Activos");
        getClosestDriver();
    }
    private void getClosestDriver() {
        mGeofireProvider.obtenerConductoresActivos(mOriginLatLng, mRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if (!mDriverFound) {
                    mDriverFound = true;
                    mIdDriverFound = key;
                    mDriverFoundLatLng = new LatLng(location.latitude, location.longitude);
                    mTextViewLookingFor.setText("CONDUCTOR ENCONTRADO\nESPERANDO RESPUESTA...");
                    Log.d("DRIVER", "ID: " + mIdDriverFound);
                }

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                // INGRESA CUANDO TERMINA LA BUSQUEDA DEL CONDUCTOR EN UN RADIO DE 0.1 KM
                if (!mDriverFound) {
                    mRadius = mRadius + 0.1f;
                    // NO ENCONTRO NINGUN CONDUCTOR
                    if (mRadius > 5) {
                        mTextViewLookingFor.setText("NO SE ENCONTRO UN CONDUCTOR");
                        Toast.makeText(SolicitarConductorActivity.this, "NO SE ENCONTRO UN CONDUCTOR", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        getClosestDriver();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
}