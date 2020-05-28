package com.irenbus.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irenbus.app.model.Bus;
import com.irenbus.app.model.OnlineBus;
import com.irenbus.app.model.User;
import com.irenbus.app.tools.AppConstants;
import com.irenbus.app.tools.GpsUtils;

import java.util.HashMap;

public class DriverMainActivity extends AppCompatActivity {

    ImageView profilePic;
    TextView tvUsername, tvOnlineOffline, tvBusRoute, tvBusPlate, tvDrivingStatus;

    ConstraintLayout layoutProgBarDriver;

    ProgressBar progressBarDriver;

    FirebaseUser firebaseUser;
    User user;

    final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
    final DatabaseReference busesRef = FirebaseDatabase.getInstance().getReference("Buses");
    final DatabaseReference onlineBusesRef = FirebaseDatabase.getInstance().getReference("OnlineBus");

    private FusedLocationProviderClient mFusedLocationClient;

    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private double prevLat = 0.0, prevLong = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    boolean isGPS;

    boolean busCodeValid = false;
    String inputBusCode = "";
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);

        profilePic = findViewById(R.id.ivProfilePic);
        tvUsername = findViewById(R.id.lblFullName);

        tvOnlineOffline = findViewById(R.id.tvOnlineOffline);
        tvBusRoute = findViewById(R.id.tvBusRoute);
        tvBusPlate = findViewById(R.id.tvBusPlate);

        layoutProgBarDriver = (ConstraintLayout)findViewById(R.id.layoutProgBarDriver);
        progressBarDriver = findViewById(R.id.progressBarDriver);
        tvDrivingStatus = findViewById(R.id.tvDrivingStatus);

        /////////////////////////////////////////////////////////////////////////////////
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                tvUsername.setText(user.getFullName());
                if(user.getImageUrl().equals("default")){
                    profilePic.setImageResource(R.drawable.ic_profile_pic);
                }else{
                    Glide.with(DriverMainActivity.this).load(user.getImageUrl()).into(profilePic);
                }

                busesRef.child(user.getBusCode()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Bus bus = dataSnapshot.getValue(Bus.class);
                        if(bus!=null){
                            tvBusRoute.setText(bus.getBusRoute());
                            tvBusPlate.setText(bus.getNumberPlate());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /////////////////////////////////////////////////////////////////////////////////

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(4 * 1000); // 4 seconds
        locationRequest.setFastestInterval(3 * 1000); // 3 seconds

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {

                    if (location != null && tvDrivingStatus.getText().equals("Stop")) { //only executes when the bus is online
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();

                        DatabaseReference reference = onlineBusesRef.child(user.getBusCode());
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("busCode", user.getBusCode());
                        hashMap.put("currLocation", wayLatitude+", "+wayLongitude);
                        hashMap.put("currDriverId", user.getId());
                        reference.setValue(hashMap);

                    }
                }

            }
        };

        ////////////////////////////////////////////////////////////////////////////////////////

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(DriverMainActivity.this,  v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.d_menu_logout:
                                logout();
                                return true;
                            case R.id.d_menu_change_bus:
                                changeBus();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.driver_menu);
                popupMenu.show();

            }
        });

        layoutProgBarDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvOnlineOffline.setText(tvDrivingStatus.getText().equals("Start")? "Online" : "Offline");
                tvOnlineOffline.setBackgroundResource(tvDrivingStatus.getText().equals("Start")? R.color.lightgreen : R.color.lightred);
                tvDrivingStatus.setTextColor(tvDrivingStatus.getText().equals("Start")?
                        Color.parseColor("#FF6E40") : Color.parseColor("#B2FF59"));
                tvDrivingStatus.setText(tvDrivingStatus.getText().equals("Start")? "Stop" : "Start");

                progressBarDriver.setProgress(0);

                Drawable circle_start = getResources().getDrawable( R.drawable.circle_start);
                Drawable circle_stop = getResources().getDrawable( R.drawable.circle_stop);

                progressBarDriver.setProgressDrawable(tvDrivingStatus.getText().equals("Stop")?
                        circle_stop : circle_start);

                progressBarDriver.setProgress(100);

                if(tvDrivingStatus.getText().equals("Stop")){ //Moving -> Online
                    getLocation();
                    DatabaseReference reference = onlineBusesRef.child(user.getBusCode());
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("busCode", user.getBusCode());
                    hashMap.put("currLocation", wayLatitude+", "+wayLongitude);
                    hashMap.put("currDriverId", user.getId());
                    reference.setValue(hashMap);

                }else if(tvDrivingStatus.getText().equals("Start")){ //Stopped -> Offline
                    onlineBusesRef.child(user.getBusCode()).removeValue();
                }
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    protected void onResume() {
        super.onResume();

        usersRef.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                onlineBusesRef.child(user.getBusCode()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        OnlineBus onlineBus = dataSnapshot.getValue(OnlineBus.class);

                        progressBarDriver.setProgress(0);

                        if(onlineBus!=null){
                            tvOnlineOffline.setText("Online");
                            tvOnlineOffline.setBackgroundResource(R.color.lightgreen);
                            tvDrivingStatus.setTextColor(Color.parseColor("#FF6E40"));
                            progressBarDriver.setProgressDrawable(getResources().getDrawable( R.drawable.circle_stop));
                            tvDrivingStatus.setText("Stop");
                        }else {
                            tvOnlineOffline.setText("Offline");
                            tvOnlineOffline.setBackgroundResource(R.color.lightred);
                            tvDrivingStatus.setTextColor(Color.parseColor("#B2FF59"));
                            progressBarDriver.setProgressDrawable(getResources().getDrawable( R.drawable.circle_start));
                            tvDrivingStatus.setText("Start");
                        }

                        progressBarDriver.setProgress(100);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void changeBus(){
        String busCode="";
        final EditText taskEditText = new EditText(DriverMainActivity.this);
        AlertDialog dialog = new AlertDialog.Builder(DriverMainActivity.this)
                .setTitle("Change Bus")
                .setMessage("Enter Bus Code")
                .setView(taskEditText)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        inputBusCode = String.valueOf(taskEditText.getText());
                        reference = FirebaseDatabase.getInstance().getReference("Buses").child(inputBusCode);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                busCodeValid = dataSnapshot.exists();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        if(!inputBusCode.equals("") && busCodeValid){
                            DatabaseReference updateRef = usersRef.child(firebaseUser.getUid()).child("busCode");
                            updateRef.setValue(inputBusCode);
                            Toast.makeText(DriverMainActivity.this, "Bus Updated Successfully", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(DriverMainActivity.this, "Invalid Bus Code", Toast.LENGTH_LONG).show();
                        }

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();

        dialog.show();
    }

    void logout(){
        FirebaseAuth.getInstance().signOut();
        //if (mFusedLocationClient != null)
          //  mFusedLocationClient.removeLocationUpdates(locationCallback);
        onlineBusesRef.child(user.getBusCode()).removeValue();
        startActivity(new Intent(DriverMainActivity.this, LoginActivity.class));
        finish();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(DriverMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(DriverMainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DriverMainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

}
