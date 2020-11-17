package com.appmovil.myappuberclone.activities.cliente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.activities.MainActivity;
import com.appmovil.myappuberclone.activities.conductor.MapConductorActivity;
import com.appmovil.myappuberclone.datos.AuthProvider;
import com.appmovil.myappuberclone.datos.GeoFireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.SphericalUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapClienteActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;
    private GeoFireProvider mGeofireProvider;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;
    private Marker mMarker;
    private LatLng latLng;
    boolean mFirstTime = true;

    private AutocompleteSupportFragment mAutoComplete;
    private AutocompleteSupportFragment mAutoCompleteDestination;
    private PlacesClient mPlaces;

    private String mOrigin;
    private LatLng mOriginLatLng;

    private String mDestination;
    private LatLng mDestinationLatLng;

    Button btnSolicitarConductor;

    private GoogleMap.OnCameraIdleListener cameraIdleListener;
    List<Marker> marcadorConductores = new ArrayList<>();

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    /*
                    if (mMarker != null) {
                        mMarker.remove();
                    }

                     */
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    /*
                    mMarker = mMap.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude())
                            )
                                    .title("Tu posicion")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location))
                    );

                     */
                    //LOCALIZACION DEL USUARIO EN TIEMPO REAL
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(16f)
                                    .build()
                    ));
                    if (mFirstTime) {
                        mFirstTime = false;
                        obtenerConductoresActivos();
                        limiteBusqueda();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_cliente);
        getSupportActionBar().setTitle("Cliente");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeoFireProvider();
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        btnSolicitarConductor=(Button)findViewById(R.id.btnSolicitarConductor);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        mPlaces = Places.createClient(this);
        //METODO DE COMPLETE ORIGEN Y DESTINO
        autoCompleteOrigen();
        autoCompleteDestino();
        onCameraMove();
        btnSolicitarConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solicitarConductor();
            }
        });

    }

    private void solicitarConductor() {
        if(mOriginLatLng!=null && mDestinationLatLng!=null){
            Intent intent=new Intent(MapClienteActivity.this,DetalleSolicitudActivity.class);
            intent.putExtra("origen_lat",mOriginLatLng.latitude);
            intent.putExtra("origen_lng",mOriginLatLng.longitude);
            intent.putExtra("destino_lat",mDestinationLatLng.latitude);
            intent.putExtra("destino_lng",mDestinationLatLng.longitude);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Seleccione origen y destino", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnCameraIdleListener(cameraIdleListener);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        starLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if(gpsActive()){
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        mMap.setMyLocationEnabled(true);
                    }else{
                        showAlertNoGPS();
                    }
                }
                else {
                    checkLocationPermission();
                }
            } else {
                checkLocationPermission();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && gpsActive()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }else if(requestCode == SETTINGS_REQUEST_CODE && !gpsActive()){
            showAlertNoGPS();
        }
    }
    //METODO PARA LIMITAR LAS BUSQUEDAS POR REGION
    private void limiteBusqueda(){
        LatLng northSide= SphericalUtil.computeOffset(latLng,5000,0);
        LatLng southSide= SphericalUtil.computeOffset(latLng,5000,180);
        mAutoComplete.setCountry("PER");
        mAutoCompleteDestination.setCountry("PER");
        mAutoComplete.setLocationBias(RectangularBounds.newInstance(southSide,northSide));
        mAutoCompleteDestination.setLocationBias(RectangularBounds.newInstance(southSide,northSide));

    }
    //METODO PARA DETECTAR EL DESPLAZAMIENTO A TRAVEZ DEL MAPA
    private void onCameraMove(){
        cameraIdleListener=new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    Geocoder geocoder=new Geocoder(MapClienteActivity.this);
                    mOriginLatLng=mMap.getCameraPosition().target;
                    List<Address> addressList=geocoder.getFromLocation(mOriginLatLng.latitude,mOriginLatLng.longitude,1);
                    String ciudad=addressList.get(0).getLocality();
                    String pais=addressList.get(0).getCountryName();
                    String direccion=addressList.get(0).getAddressLine(0);
                    mOrigin=direccion+" "+ciudad;
                    mAutoComplete.setText(direccion+" "+ciudad);

                }catch (Exception e){
                    Log.d("Error ","Mensaje error"+e.getMessage());
                }
            }
        };
    }

    private void autoCompleteOrigen(){
        //AUTOCOMPLETE PARA EL ORIGEN
        mAutoComplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placeAutocompleteOrigin);
        mAutoComplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutoComplete.setHint("Lugar de recogida");
        mAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mOrigin = place.getName();
                mOriginLatLng = place.getLatLng();
                Log.d("PLACE", "Name: " + mOrigin);
                Log.d("PLACE", "Lat: " + mOriginLatLng.latitude);
                Log.d("PLACE", "Lng: " + mOriginLatLng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }
    private void autoCompleteDestino(){
        //AUTOCOMPLETE PARA EL DESTINO
        mAutoCompleteDestination = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placeAutocompleteDestino);
        mAutoCompleteDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutoCompleteDestination.setHint("Lugar de destino");
        mAutoCompleteDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                mDestination = place.getName();
                mDestinationLatLng = place.getLatLng();
                Log.d("PLACE", "Name: " + mDestination);
                Log.d("PLACE", "Lat: " + mDestinationLatLng.latitude);
                Log.d("PLACE", "Lng: " + mDestinationLatLng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }

    private void obtenerConductoresActivos(){
        mGeofireProvider.obtenerConductoresActivos(latLng).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //AÑADIR LOS MARCADORES DE LOS CONDUCTORES QUE SE CONECTA A LA APLICACION
                for (Marker marker:marcadorConductores){
                    if(marker.getTag()!=null){
                        if(marker.getTag().equals(key)){
                            return;
                        }
                    }
                }
                LatLng conductorLtLgn=new LatLng(location.latitude,location.longitude);
                Marker marker=mMap.addMarker(new MarkerOptions().position(conductorLtLgn).title("Conductor disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_cars)));
                marker.setTag(key);
                marcadorConductores.add(marker);
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker marker:marcadorConductores){
                    if(marker.getTag()!=null){
                        if(marker.getTag().equals(key)){
                            marker.remove();
                            marcadorConductores.remove(marker);
                            return;
                        }
                    }
                }

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //ACTUALIZAR LA POSICION DEL CONDUCTOR EL TIEMPO REAL
                for (Marker marker:marcadorConductores){
                    if(marker.getTag()!=null){
                        if(marker.getTag().equals(key)){
                            marker.setPosition(new LatLng(location.longitude,location.latitude));
                        }
                    }
                }

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    //IR A LAS CONFIGURACIONES
    private void showAlertNoGPS(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Por favor active su ubicacion para continuar");
        builder.setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),SETTINGS_REQUEST_CODE);
            }
        }).create().show();
    }

    //METODO PARA VERIFICAR SI TIENE ACTIVO EL GPS
    private boolean gpsActive(){
        boolean isACtive=false;
        LocationManager locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isACtive=true;
        }
        return isACtive;
    }

    private void  starLocation(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                if(gpsActive()){
                    mFusedLocation.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                }else{
                    showAlertNoGPS();
                }

            }else{
                checkLocationPermission();
            }
        }else{
            if(gpsActive()) {
                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
            }else{
                showAlertNoGPS();
            }
        }
    }
    private void checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Concede los permisos para continuar")
                        .setMessage("Esta aplicacion requiere los permisos de ubicacion")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MapClienteActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

                            }
                        })
                        .create()
                        .show();
            }else{
                ActivityCompat.requestPermissions(MapClienteActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cliente_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_lagout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }
    void logout(){
        mAuthProvider.cerrarSesion();
        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}