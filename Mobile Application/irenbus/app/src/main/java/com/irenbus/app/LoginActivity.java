package com.irenbus.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.irenbus.app.model.User;

public class LoginActivity extends AppCompatActivity{

    Button btnLogin, btnDontHaveAcc;
    EditText etEmail, etPassword;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnDontHaveAcc = findViewById(R.id.btnDontHaveAcc);
        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);

        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String txtEmail = etEmail.getText().toString();
                String txtPassword = etPassword.getText().toString();

                if(TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPassword)){
                    Snackbar snackbar = Snackbar.make(v,"All fields are required",Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }else{
                    auth.signInWithEmailAndPassword(txtEmail, txtPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                if(user.getUserType().equals("Commuter")){
                                                    Intent intent = new Intent(LoginActivity.this, CommuterMainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }else if(user.getUserType().equals("Driver")){
                                                    Intent intent = new Intent(LoginActivity.this, DriverMainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }else{
                                        Snackbar snackbar = Snackbar.make(v,"Authentification failed",Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    }
                                }
                            });
                }
            }
        });

        btnDontHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }
}