package com.appmovil.myappuberclone.activities.conductor;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.adapters.HistorialViajeConductorAdapter;
import com.appmovil.myappuberclone.datos.AuthProvider;
import com.appmovil.myappuberclone.modelos.HistoryBooking;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class HistorialViajesConductorActivity extends AppCompatActivity {
    private RecyclerView mReciclerView;
    private HistorialViajeConductorAdapter mAdapter;
    private AuthProvider mAuthProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial_viajes_conductor);
        getSupportActionBar().setTitle("Historial de viajes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mReciclerView = findViewById(R.id.recyclerViewHistoryBooking);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mReciclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuthProvider = new AuthProvider();
        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Historial_Viaje")
                .orderByChild("idDriver")
                .equalTo(mAuthProvider.obtenerIdConductor());
        FirebaseRecyclerOptions<HistoryBooking> options = new FirebaseRecyclerOptions.Builder<HistoryBooking>()
                .setQuery(query, HistoryBooking.class)
                .build();
        mAdapter = new HistorialViajeConductorAdapter(options, HistorialViajesConductorActivity.this);

        mReciclerView.setAdapter(mAdapter);
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}