package com.appmovil.myappuberclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appmovil.myappuberclone.R;
import com.appmovil.myappuberclone.activities.conductor.DetalleHistorialViajeConductorActivity;
import com.appmovil.myappuberclone.datos.ClienteProvider;
import com.appmovil.myappuberclone.modelos.HistoryBooking;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HistorialViajeConductorAdapter extends FirebaseRecyclerAdapter<HistoryBooking, HistorialViajeConductorAdapter.ViewHolder> {

    private ClienteProvider mClientProvider;
    private Context mContext;

    public HistorialViajeConductorAdapter(FirebaseRecyclerOptions<HistoryBooking> options, Context context) {
        super(options);
        mClientProvider = new ClienteProvider();
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull final HistorialViajeConductorAdapter.ViewHolder holder, int position, @NonNull HistoryBooking historyBooking) {

        final String id = getRef(position).getKey();


        holder.textViewOrigin.setText(historyBooking.getOrigin());
        holder.textViewDestination.setText(historyBooking.getDestination());
        holder.textViewCalification.setText(String.valueOf(historyBooking.getCalificationDriver()));

        mClientProvider.getClient(historyBooking.getIdClient()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("nombre").getValue().toString();
                    holder.textViewName.setText(name);
                    if (dataSnapshot.hasChild("image")) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.with(mContext).load(image).into(holder.imageViewHistoryBooking);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetalleHistorialViajeConductorActivity.class);
                intent.putExtra("idHistoryBooking", id);
               mContext.startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public HistorialViajeConductorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_booking, parent, false);
        return new HistorialViajeConductorAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewName;
        private TextView textViewOrigin;
        private TextView textViewDestination;
        private TextView textViewCalification;
        private ImageView imageViewHistoryBooking;
        private View mView;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            textViewName = view.findViewById(R.id.textViewName);
            textViewOrigin = view.findViewById(R.id.textViewOrigin);
            textViewDestination = view.findViewById(R.id.textViewDestination);
            textViewCalification = view.findViewById(R.id.textViewCalification);
            imageViewHistoryBooking = view.findViewById(R.id.imageViewHistoryBooking);
        }

    }
}