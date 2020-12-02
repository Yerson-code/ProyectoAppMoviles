package com.appmovil.myappuberclone.activities.cliente;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.Utils.DecodePoints;
import com.appmovil.myappuberclone.activities.conductor.CalificacionClienteActivity;
import com.appmovil.myappuberclone.activities.conductor.MapDriverBookingActivity;
import com.appmovil.myappuberclone.datos.AuthProvider;
import com.appmovil.myappuberclone.datos.ClientBookingProvider;
import com.appmovil.myappuberclone.datos.ConductorProvider;
import com.appmovil.myappuberclone.datos.GeoFireProvider;
import com.appmovil.myappuberclone.datos.GoogleApiProvider;
import com.appmovil.myappuberclone.datos.TokenProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapClientBookingActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;

    private GeoFireProvider mGeofireProvider;
    private TokenProvider mTokenProvider;
    private ClientBookingProvider mClientBookingProvider;
    private ConductorProvider mDriverProvider;

    private Marker mMarkerDriver;

    private boolean mIsFirstTime = true;

    private String mOrigin;
    private LatLng mOriginLatLng;

    private String mDestination;
    private LatLng mDestinationLatLng;
    private LatLng mDriverLatLng;




    private GoogleApiProvider mGoogleApiProvider;
    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;

    private ValueEventListener mListener;
    private String mIdDriver;
    private ValueEventListener mListenerStatus;
    private TextView mTextViewClientBooking;
    private TextView mTextViewEmailClientBooking;
    private TextView mTextViewOriginClientBooking;
    private TextView mTextViewDestinationClientBooking;
    private TextView mTextViewStatusBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client_booking);
        getSupportActionBar().setTitle("Viaje Cliente");
        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeoFireProvider("Conductores_Trabajando");
        mTokenProvider = new TokenProvider();
        mClientBookingProvider = new ClientBookingProvider();
        mGoogleApiProvider = new GoogleApiProvider(MapClientBookingActivity.this);
        mDriverProvider = new ConductorProvider();

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }

        mTextViewClientBooking = findViewById(R.id.textViewDriverBooking);
        mTextViewEmailClientBooking = findViewById(R.id.textViewEmailDriverBooking);
        mTextViewStatusBooking = findViewById(R.id.textViewStatusBooking);
        mTextViewOriginClientBooking = findViewById(R.id.textViewOrigenDriverBooking);
        mTextViewDestinationClientBooking = findViewById(R.id.textViewDestinoDriverBooking);

        getStatus();
        getClientBooking();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
               return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mListener != null) {
            mGeofireProvider.getDriverLocation(mIdDriver).removeEventListener(mListener);
        }
        if (mListenerStatus != null) {
            mClientBookingProvider.getStatus(mAuthProvider.obtenerIdConductor()).removeEventListener(mListenerStatus);
        }
    }

    private void getStatus() {
        mListenerStatus =  mClientBookingProvider.getStatus(mAuthProvider.obtenerIdConductor()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String status = dataSnapshot.getValue().toString();
                    if (status.equals("accept")) {
                        mTextViewStatusBooking.setText("Estado: Aceptado");
                    }
                    if (status.equals("start")) {
                        mTextViewStatusBooking.setText("Estado: Viaje Iniciado");
                        startBooking();
                    }
                    else if (status.equals("finish")) {
                        mTextViewStatusBooking.setText("Estado: Viaje Finalizado");
                        finishBooking();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void startBooking() {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_blue)));
        drawRoute(mDestinationLatLng);
    }
    private void finishBooking() {
        Intent intent = new Intent(MapClientBookingActivity.this, CalificacionConductorActivity.class);
        startActivity(intent);
        finish();
    }

    private void getDriver(String idDriver) {
        mDriverProvider.getDriver(idDriver).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("nombre").getValue().toString();
                    String email = dataSnapshot.child("correo").getValue().toString();
                    mTextViewClientBooking.setText(name);
                   mTextViewEmailClientBooking.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getClientBooking() {
        mClientBookingProvider.getClientBooking(mAuthProvider.obtenerIdConductor()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String destination = dataSnapshot.child("destination").getValue().toString();
                    String origin = dataSnapshot.child("origin").getValue().toString();
                    String idDriver = dataSnapshot.child("idDriver").getValue().toString();
                    mIdDriver = idDriver;
                    double destinatioLat = Double.parseDouble(dataSnapshot.child("destinationLat").getValue().toString());
                    double destinatioLng= Double.parseDouble(dataSnapshot.child("destinationLng").getValue().toString());

                    double originLat = Double.parseDouble(dataSnapshot.child("originLat").getValue().toString());
                    double originLng= Double.parseDouble(dataSnapshot.child("originLng").getValue().toString());
                    mOriginLatLng = new LatLng(originLat, originLng);
                    mDestinationLatLng = new LatLng(destinatioLat, destinatioLng);
                    mTextViewOriginClientBooking.setText("recoger en: " + origin);
                    mTextViewDestinationClientBooking.setText("destino: " + destination);
                    mMap.addMarker(new MarkerOptions().position(mOriginLatLng).title("Recoger aqui").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_red)));
                    getDriver(idDriver);
                    getDriverLocation(idDriver);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void drawRoute(LatLng latLng) {
        mGoogleApiProvider.getDirections(mDriverLatLng, latLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {

                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    mPolylineList = DecodePoints.decodePoly(points);
                    mPolylineOptions = new PolylineOptions();
                    mPolylineOptions.color(Color.DKGRAY);
                    mPolylineOptions.width(13f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    mMap.addPolyline(mPolylineOptions);

                    JSONArray legs =  route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");


                } catch(Exception e) {
                    Log.d("Error", "Error encontrado " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void getDriverLocation(String idDriver) {
        mListener = mGeofireProvider.getDriverLocation(idDriver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    double lat = Double.parseDouble(dataSnapshot.child("0").getValue().toString());
                    double lng = Double.parseDouble(dataSnapshot.child("1").getValue().toString());
                    mDriverLatLng = new LatLng(lat, lng);
                    if (mMarkerDriver != null) {
                        mMarkerDriver.remove();
                    }
                    mMarkerDriver = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title("Tu conductor")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_cars)));
                    if (mIsFirstTime) {
                        mIsFirstTime = false;
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(mDriverLatLng)
                                        .zoom(15f)
                                        .build()
                        ));
                        drawRoute(mOriginLatLng);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}