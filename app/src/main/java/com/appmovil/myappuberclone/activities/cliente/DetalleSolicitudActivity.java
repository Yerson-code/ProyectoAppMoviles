package com.appmovil.myappuberclone.activities.cliente;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.Utils.DecodePoints;
import com.appmovil.myappuberclone.datos.GoogleApiProvider;
import com.appmovil.myappuberclone.retrofit.IGoogleAPI;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleSolicitudActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private double extraOriginLat;
    private double extraOriginLgn;
    private double extraDestinationLat;
    private double extraDestinationLgn;
    private LatLng origingLatLng;
    private LatLng destinationLatLng;

    private GoogleApiProvider googleApiProvider;

    private List<LatLng> mPolyLineList;
    private PolylineOptions mpolylineOptions;

    TextView txtorigen;
    TextView txtDestino;
    TextView txtTiempo;
    TextView txtDistancia;

    String origen;
    String destino;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_solicitud);
        getSupportActionBar().setTitle("Detalle de viaje");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        extraOriginLat=getIntent().getDoubleExtra("origen_lat",0);
        extraOriginLgn=getIntent().getDoubleExtra("origen_lng",0);
        extraDestinationLat=getIntent().getDoubleExtra("destino_lat",0);
        extraDestinationLgn=getIntent().getDoubleExtra("destino_lng",0);
        origen=getIntent().getStringExtra("origen");
        destino=getIntent().getStringExtra("destino");

        origingLatLng=new LatLng(extraOriginLat,extraOriginLgn);
        destinationLatLng=new LatLng(extraDestinationLat,extraDestinationLgn);
        googleApiProvider=new GoogleApiProvider(DetalleSolicitudActivity.this);
        txtorigen=(TextView)findViewById(R.id.txtViewOrigin);
        txtDestino=(TextView)findViewById(R.id.txtViewDestino);
        txtTiempo=(TextView)findViewById(R.id.txtViewTime);
        txtDistancia=(TextView)findViewById(R.id.txtViewDistancia);
        txtorigen.setText(origen);
        txtDestino.setText(destino);


    }

    private void dibujarRuta(){
        googleApiProvider.getDirections(origingLatLng,destinationLatLng).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    JSONObject jsonObject=new JSONObject(response.body());
                    JSONArray jsonArray= jsonObject.getJSONArray("routes");
                    JSONObject route=jsonArray.getJSONObject(0);
                    JSONObject polyLines=route.getJSONObject("overview_polyline");
                    String points=polyLines.getString("points");
                    mPolyLineList= DecodePoints.decodePoly(points);
                    mpolylineOptions=new PolylineOptions();
                    mpolylineOptions.color(Color.DKGRAY);
                    mpolylineOptions.width(13f);
                    mpolylineOptions.startCap(new SquareCap());
                    mpolylineOptions.jointType(JointType.ROUND);
                    mpolylineOptions.addAll(mPolyLineList);
                    mMap.addPolyline(mpolylineOptions);

                    JSONArray legs=route.getJSONArray("legs");
                    JSONObject leg=legs.getJSONObject(0);
                    JSONObject distance=leg.getJSONObject("distance");
                    JSONObject duration=leg.getJSONObject("duration");
                    String textdistance=distance.getString("text");
                    String textduration=duration.getString("text");
                    txtDistancia.setText(textdistance);
                    txtTiempo.setText(textduration);



                }catch (Exception e){
                    Log.d("Error","Error encontrado"+e.getMessage());
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

        mMap.addMarker(new MarkerOptions().position(origingLatLng).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_red)));
        mMap.addMarker(new MarkerOptions().position(destinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_map_blue)));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                .target(origingLatLng)
                .zoom(14f)
                .build()
        ));
        dibujarRuta();
    }
}