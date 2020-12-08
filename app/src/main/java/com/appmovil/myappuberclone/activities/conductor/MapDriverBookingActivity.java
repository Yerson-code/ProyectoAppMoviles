package com.appmovil.myappuberclone.activities.conductor;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.Utils.DecodePoints;
import com.appmovil.myappuberclone.datos.AuthProvider;
import com.appmovil.myappuberclone.datos.ClientBookingProvider;
import com.appmovil.myappuberclone.datos.ClienteProvider;
import com.appmovil.myappuberclone.datos.GeoFireProvider;
import com.appmovil.myappuberclone.datos.GoogleApiProvider;
import com.appmovil.myappuberclone.datos.InfoProvider;
import com.appmovil.myappuberclone.datos.NotificationProvider;
import com.appmovil.myappuberclone.datos.TokenProvider;
import com.appmovil.myappuberclone.modelos.FCMBody;
import com.appmovil.myappuberclone.modelos.FCMResponse;
import com.appmovil.myappuberclone.modelos.Info;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapDriverBookingActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;
    private GeoFireProvider mGeofireProvider;
    private ClienteProvider mClientProvider;
    private ClientBookingProvider mClientBookingProvider;
    private TokenProvider mTokenProvider;
    private NotificationProvider mNotificationProvider;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;
    private ImageView mImageViewBooking;
    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;


    private Marker mMarker;
    private LatLng mCurrentLatLng;

    private TextView mTextViewClientBooking;
    private TextView mTextViewEmailClientBooking;
    private TextView mTextViewOriginClientBooking;
    private TextView mTextViewDestinationClientBooking;
    private TextView mTextViewStatusBooking;
    private TextView txtTime;
    private InfoProvider mInfoProvider;
    private Info mInfo;
    private String mExtraClientId;

    private LatLng mOriginLatLng;
    private LatLng mDestinationLatLng;

    private GoogleApiProvider mGoogleApiProvider;
    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;
    private boolean viajeIniciado=false;
    private boolean mIsFirstTime = true;
    private boolean mIsCloseToClient = false;
    private ValueEventListener mListenerStatus;
    private Button mButtonStartBooking;
    private Button mButtonFinishBooking;
    private Location mPreviousLocation=new Location("");
    double mDistanceMeters=1;
    double mMinutes=0;
    double mSeconds=0;
    boolean mSecondsIsOver=false;
    Handler mHandler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            mSeconds++;
            if(!mSecondsIsOver){
                txtTime.setText(mSeconds+" Seg");
            }else{
                txtTime.setText(mMinutes+" Min "+ mSeconds);
            }
          if(mSeconds==59){
                mSeconds=0;
                mMinutes++;
                mSecondsIsOver  =true;
            }
            mHandler.postDelayed(runnable,1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver_booking);
       // getSupportActionBar().setTitle("Viaje Conductor");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeoFireProvider("Conductores_Trabajando");
        mTokenProvider = new TokenProvider();
        mClientProvider = new ClienteProvider();
        mClientBookingProvider = new ClientBookingProvider();
        mNotificationProvider = new NotificationProvider();

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        mInfoProvider=new InfoProvider();
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        mGoogleApiProvider = new GoogleApiProvider(MapDriverBookingActivity.this);
        mTextViewClientBooking = findViewById(R.id.textViewClientBooking);
        mTextViewEmailClientBooking = findViewById(R.id.textViewEmailClientBooking);
        mTextViewOriginClientBooking = findViewById(R.id.textViewOriginClientBooking);
        mTextViewStatusBooking = findViewById(R.id.textViewStatusBooking);
        mTextViewDestinationClientBooking = findViewById(R.id.textViewDestinationClientBooking);
        mButtonStartBooking = findViewById(R.id.btnStartBooking);
        mButtonFinishBooking = findViewById(R.id.btnFinishBooking);
        mExtraClientId = getIntent().getStringExtra("idClient");
        mImageViewBooking = findViewById(R.id.imageViewClientBooking);
        txtTime=findViewById(R.id.textViewTime);
        getClient();
        getInfo();
        mButtonStartBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsCloseToClient) {
                    startBooking();
                }
                else {
                    Toast.makeText(MapDriverBookingActivity.this, "Debes estar mas cerca a la posicion de recogida", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mButtonFinishBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishBooking();
            }
        });



    }

    private void finishBooking() {
        mClientBookingProvider.updateStatus(mExtraClientId, "finish");
        mClientBookingProvider.updateIdHistoryBooking(mExtraClientId);
        sendNotification("Viaje finalizado");
        if (mFusedLocation != null) {
            mFusedLocation.removeLocationUpdates(mLocationCallback);
        }
        mGeofireProvider.eliminarLocalizacion(mAuthProvider.obtenerIdConductor());
        CalcularViaje();
    }

    private void startBooking() {
        mClientBookingProvider.updateStatus(mExtraClientId, "start");
        mButtonStartBooking.setVisibility(View.GONE);
        mButtonFinishBooking.setVisibility(View.VISIBLE);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_blue)));
        drawRoute(mDestinationLatLng);
        sendNotification("Viaje iniciado");
        mHandler.postDelayed(runnable,1000);
        viajeIniciado=true;
    }

    private void sendNotification(final String status) {
        mTokenProvider.getToken(mExtraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String token = dataSnapshot.child("token").getValue().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("title", "ESTADO DE TU VIAJE");
                    map.put("body",
                            "Tu estado del viaje es: " + status
                    );
                    FCMBody fcmBody = new FCMBody(token, "high", "4500s", map);
                    mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                        @Override
                        public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                            if (response.body() != null) {
                                if (response.body().getSuccess() != 1) {
                                    Toast.makeText(MapDriverBookingActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(MapDriverBookingActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMResponse> call, Throwable t) {
                            Log.d("Error", "Error " + t.getMessage());
                        }
                    });
                }
                else {
                    Toast.makeText(MapDriverBookingActivity.this, "No se pudo enviar la notificacion porque el conductor no tiene un token de sesion", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                    if (mMarker != null) {
                        mMarker.remove();
                    }
                    if (viajeIniciado){
                        mDistanceMeters=mDistanceMeters+mPreviousLocation.distanceTo(location);
                            
                    }
                    mPreviousLocation=location;
                    mMarker = mMap.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude())
                            )
                                    .title("Tu posicion")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_cars))
                    );
                    // OBTENER LA LOCALIZACION DEL USUARIO EN TIEMPO REAL
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(16f)
                                    .build()
                    ));
                    actualizarLocalizacion();

                    if (mIsFirstTime) {
                        mIsFirstTime = false;
                        getClientBooking();
                    }



                }
            }
        }
    };
    //OBTENER LA INFORMACION DEL CLIENTE PARA MOSTRARLE AL CONDUCTOR
    private void getClient() {
        mClientProvider.getClient(mExtraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = dataSnapshot.child("email").getValue().toString();
                    String name = dataSnapshot.child("nombre").getValue().toString();
                    String image = "";
                    if (dataSnapshot.hasChild("image")) {
                        image = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(MapDriverBookingActivity.this).load(image).into(mImageViewBooking);
                    }
                    mTextViewClientBooking.setText(name);
                    mTextViewEmailClientBooking.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private double getDistanceBetween(LatLng clientLatLng, LatLng driverLatLng) {
        double distance = 0;
        Location clientLocation = new Location("");
        Location driverLocation = new Location("");
        clientLocation.setLatitude(clientLatLng.latitude);
        clientLocation.setLongitude(clientLatLng.longitude);
        driverLocation.setLatitude(driverLatLng.latitude);
        driverLocation.setLongitude(driverLatLng.longitude);
        distance = clientLocation.distanceTo(driverLocation);
        return distance;
    }

    //===============================================================
    private void getClientBooking() {
        mClientBookingProvider.getClientBooking(mExtraClientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String destination = dataSnapshot.child("destination").getValue().toString();
                    String origin = dataSnapshot.child("origin").getValue().toString();
                    double destinatioLat = Double.parseDouble(dataSnapshot.child("destinationLat").getValue().toString());
                    double destinatioLng= Double.parseDouble(dataSnapshot.child("destinationLng").getValue().toString());

                    double originLat = Double.parseDouble(dataSnapshot.child("originLat").getValue().toString());
                    double originLng= Double.parseDouble(dataSnapshot.child("originLng").getValue().toString());
                    mOriginLatLng = new LatLng(originLat, originLng);
                    mDestinationLatLng = new LatLng(destinatioLat, destinatioLng);
                    mTextViewOriginClientBooking.setText("recoger en: " + origin);
                    mTextViewDestinationClientBooking.setText("destino: " + destination);
                   mMap.addMarker(new MarkerOptions().position(mOriginLatLng).title("Recoger aqui").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_red)));
                    drawRoute(mOriginLatLng);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //===================================================================

    private void drawRoute(LatLng latLng) {
        mGoogleApiProvider.getDirections(mCurrentLatLng, latLng).enqueue(new Callback<String>() {
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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              return;
        }
        mMap.setMyLocationEnabled(false);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        startLocation();


    }
    //METODO QUE ACTUALIZA LA POSICION ACTUAL DEL CONDUCTOR EN LA BASE DE DATOS
    private void actualizarLocalizacion() {
        if(mAuthProvider.existSesion()&& mCurrentLatLng!=null){
            mGeofireProvider.guardarLocalizacion(mAuthProvider.obtenerIdConductor(),mCurrentLatLng);
            if (!mIsCloseToClient) {
                if (mOriginLatLng != null && mCurrentLatLng != null) {
                    double distance = getDistanceBetween(mOriginLatLng, mCurrentLatLng); // METROS
                    if (distance <= 200) {
                        //mButtonStartBooking.setEnabled(true);
                        mIsCloseToClient = true;
                        Toast.makeText(this, "Estas cerca a la posicion de recogida", Toast.LENGTH_SHORT).show();
                    }
                }
           }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    } else {
                        showAlertDialogNOGPS();
                    }
                } else {
                    checkLocationPermissions();
                }
            } else {
                checkLocationPermissions();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                   return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
        else {
            showAlertDialogNOGPS();
        }
    }

    private void showAlertDialogNOGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa tu ubicacion para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }

    private boolean gpsActived() {
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            isActive = true;
        }
        return isActive;
    }
    private void getInfo() {

        mInfoProvider.getInfo().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    mInfo = snapshot.getValue(Info.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void disconnect() {

        if (mFusedLocation != null) {
            mFusedLocation.removeLocationUpdates(mLocationCallback);
            if (mAuthProvider.existSesion()) {
               // mGeofireProvider.removeLocation(mAuthProvider.getId());
            }
        }
        else {
            Toast.makeText(this, "No te puedes desconectar", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()) {

                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }
                else {
                    showAlertDialogNOGPS();
                }
            }
            else {
                checkLocationPermissions();
            }
        } else {
            if (gpsActived()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }
            else {
                showAlertDialogNOGPS();
            }
        }
    }
    private void CalcularViaje(){
       //double PrecioMin =

       /* Intent intent = new Intent(MapDriverBookingActivity.this, CalificacionClienteActivity.class);
        intent.putExtra("idClient", mExtraClientId);
        startActivity(intent);
        finish();*/
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicacion para poder utilizarse")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MapDriverBookingActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(MapDriverBookingActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }
}