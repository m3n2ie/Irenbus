package com.irenbus.app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsResult;
import com.irenbus.app.model.Bus;
import com.irenbus.app.model.BusInfo;
import com.irenbus.app.model.OnlineBus;
import com.irenbus.app.model.User;
import com.irenbus.app.tools.NearbyListAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class NearbyFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvOnlineBuses;

    private List<BusInfo> buses;

    private GeoApiContext geoApiContext;

    FirebaseUser firebaseUser;

    LatLng currUserLocation;

    String currDistance = "0";
    String currDuration = "0";

    DatabaseReference onlineBusRef = FirebaseDatabase.getInstance().getReference("OnlineBus");
    final DatabaseReference busRef = FirebaseDatabase.getInstance().getReference("Buses");
    final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

    NearbyListAdapter tempAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nearby, container, false);

        recyclerView = view.findViewById(R.id.NearbyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tvOnlineBuses = view.findViewById(R.id.tvOnlineBuses);

        if(geoApiContext == null){
            geoApiContext = new GeoApiContext.Builder().apiKey("AIzaSyA2oDioGs6wGU6A4RFr379CjC4xdMyfjAw").build();
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        initializeData();

        return view;
    }

    private void initializeData(){
        buses = new ArrayList<>();

        onlineBusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                buses.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final OnlineBus onlineBus = snapshot.getValue(OnlineBus.class);

                    busRef.child(onlineBus.getBusCode()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Bus bus = dataSnapshot.getValue(Bus.class);

                            final BusInfo busInfo = new BusInfo(bus);
                            busInfo.setCurrLocation(onlineBus.getCurrLocation());

                            usersRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    User user = dataSnapshot.getValue(User.class);
                                    currUserLocation = new LatLng(Double.parseDouble(user.getCurrLocation().split(",")[0]),
                                            Double.parseDouble(user.getCurrLocation().split(",")[1]));
                                    busInfo.setDestLocation(currUserLocation.latitude+", "+currUserLocation.longitude);

                                    busInfo.update();

                                    while(!busInfo.isUpdateComplete()){ }

                                    buses.add(busInfo);

                                    Set<BusInfo> set = new HashSet<>(buses);
                                    buses.clear();
                                    buses.addAll(set);

                                    tvOnlineBuses.setText(buses.size()+"");
                                    tempAdapter = new NearbyListAdapter(buses);
                                    tempAdapter.notifyDataSetChanged();
                                    recyclerView.swapAdapter(tempAdapter, true);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            //initializeAdapter();
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
    }

    public String getDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));

        return String.format("%.2f", Radius * c)+" km away";
    }

    public void calculateDirections(com.google.maps.model.LatLng origin, com.google.maps.model.LatLng dest){
        Log.d(TAG, "calculateDirections: calculating directions...");

        com.google.maps.model.LatLng destination = dest;
        DirectionsApiRequest directions = new DirectionsApiRequest(geoApiContext);

        directions.alternatives(true);
        directions.origin(origin);
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                currDuration = result.routes[0].legs[0].duration+"";
                currDistance = result.routes[0].legs[0].distance+"";
            }
            @Override
            public void onFailure(Throwable e) {
                currDuration = "0";
                currDistance = "0";
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    private void initializeAdapter(){
        tvOnlineBuses.setText(buses.size()+"");
        tempAdapter = new NearbyListAdapter(buses);
        recyclerView.setAdapter(tempAdapter);
    }

}
