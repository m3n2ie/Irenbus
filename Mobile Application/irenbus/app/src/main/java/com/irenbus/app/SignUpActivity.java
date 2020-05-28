package com.irenbus.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText etFullname, etEmail, etPassword;
    Button btnSignUp, btnAlreadyHaveAcc;
    CheckBox cbImABusDriver;

    FirebaseAuth auth;
    DatabaseReference reference;

    final int COMMUTER = 1;
    final int DRIVER = 2;

    String busCode = "";
    boolean busCodeValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etFullname = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmailSignUp);
        etPassword = findViewById(R.id.etPasswordSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnAlreadyHaveAcc = findViewById(R.id.btnAleardyHaveAcc);
        cbImABusDriver = findViewById(R.id.cbImABusDriver);

        auth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String txtFullname = etFullname.getText().toString();
                final String txtEmail = etEmail.getText().toString();
                final String txtPassword = etPassword.getText().toString();

                if(TextUtils.isEmpty(txtFullname)||TextUtils.isEmpty(txtEmail)||TextUtils.isEmpty(txtPassword)){
                    Toast.makeText(SignUpActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();

                }else if(txtPassword.length()<6){
                    Toast.makeText(SignUpActivity.this, "Password must be atleast 6 characters", Toast.LENGTH_SHORT).show();

                }else{
                    if(cbImABusDriver.isChecked()){
                        final EditText taskEditText = new EditText(SignUpActivity.this);
                        AlertDialog dialog = new AlertDialog.Builder(SignUpActivity.this)
                                .setTitle("Select Bus")
                                .setMessage("Enter Bus Code")
                                .setView(taskEditText)
                                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        busCode = String.valueOf(taskEditText.getText());
                                        reference = FirebaseDatabase.getInstance().getReference("Buses").child(busCode);
                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                busCodeValid = dataSnapshot.exists();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        if(!busCode.equals("") && busCodeValid){
                                            SignUp(txtFullname,txtEmail, txtPassword, DRIVER);
                                        }else{
                                            System.out.print(busCode.equals("")+", "+busCodeValid);
                                            Snackbar snackbar = Snackbar.make(v,"Invalid Bus Code",Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                        }

                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create();

                        dialog.show();

                    }else{
                        SignUp(txtFullname,txtEmail, txtPassword, COMMUTER);
                    }

                }

            }
        });

        btnAlreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void SignUp(final String fullname, String email, String password, final int userType){

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("fullName", fullname);
                            hashMap.put("imageURL", "default");
                            hashMap.put("userType", userType == 1? "Commuter" : "Driver");
                            if(userType == DRIVER){
                                hashMap.put("busCode", busCode);
                                busCode = "";
                            }else if(userType == COMMUTER){
                                hashMap.put("currLocation", "0, 0");
                            }

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = null;

                                        if(userType == COMMUTER){
                                            intent = new Intent(SignUpActivity.this, CommuterMainActivity.class);
                                        }else if(userType == DRIVER){
                                            intent = new Intent(SignUpActivity.this, DriverMainActivity.class);
                                        }

                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(SignUpActivity.this, "You can't register with this email or password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
