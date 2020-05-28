package com.irenbus.app;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irenbus.app.model.Bus;
import com.irenbus.app.model.OnlineBus;

import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment implements OnMapReadyCallback{

    GoogleMap googleMap;
    MapView mapView;
    View view;

    private Marker commuterMarker;
    private Marker driverMarker;

    Set<Marker> markers = new HashSet<>();

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
    final DatabaseReference onlineBusesRef = FirebaseDatabase.getInstance().getReference("OnlineBus");
    final DatabaseReference busesRef = FirebaseDatabase.getInstance().getReference("Buses");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view_, Bundle savedInstanceState) {
        super.onViewCreated(view_, savedInstanceState);

        mapView = (MapView) view.findViewById(R.id.mapview);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap_) {
        MapsInitializer.initialize(this.getContext());
        googleMap = googleMap_;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this.getContext(), R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        onlineBusesRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(Marker m : markers){
                    if(!m.equals(commuterMarker)){
                        m.remove();
                    }
                }

                markers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final OnlineBus onlineBus = snapshot.getValue(OnlineBus.class);

                    busesRef.child(onlineBus.getBusCode()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            Bus busInfo = dataSnapshot2.getValue(Bus.class);
                            driverMarker = placeMarkerOnLoc(new LatLng(
                                            Double.parseDouble(onlineBus.getCurrLocation().split(",")[0]),
                                            Double.parseDouble(onlineBus.getCurrLocation().split(",")[1])),
                                    busInfo.getBusRoute());
                            markers.add(driverMarker);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userRef.child("currLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String currLocation = dataSnapshot.getValue(String.class);

                    if(commuterMarker != null){
                        commuterMarker.remove();
                    }

                    commuterMarker = placeMarkerOnLoc(new LatLng(
                                    Double.parseDouble(currLocation.split(",")[0]),
                                    Double.parseDouble(currLocation.split(",")[1])),
                            "My Location");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public Marker placeMarkerOnLoc(LatLng location, String name) {
        /*if(marker != null){
            marker.remove();
        }*/

        if(name.equals("My Location")){
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17.0f));
            return googleMap.addMarker(new MarkerOptions().position(location).title(name).icon(BitmapDescriptorFactory.fromResource(R.drawable.user_marker)));
        }

        return googleMap.addMarker(new MarkerOptions().position(location).title(name).icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_marker)));
    }

}
