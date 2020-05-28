package com.irenbus.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irenbus.app.model.User;
import com.irenbus.app.tools.CodeGenerator;

import java.util.ArrayList;
import java.util.HashMap;

public class StartActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //checks if user has logged in before
        if(firebaseUser!=null){
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if(user.getUserType().equals("Commuter")){
                        Intent intent = new Intent(StartActivity.this, CommuterMainActivity.class);
                        startActivity(intent);
                        finish();
                    }else if(user.getUserType().equals("Driver")){
                        Intent intent = new Intent(StartActivity.this, DriverMainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
