package com.savethefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class Register extends AppCompatActivity {
    private EditText EName, EEmail, EPassword;
    private Button BRegister;
    private TextView TLoginBtn;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;
    private Spinner typeOfUserSpinner;

    private ArrayList<String> typeOfUserList;
    private ArrayAdapter<String> typeOfUserAdapter;

    private String typeOfUser = "";

    private String userUID;
    private DatabaseReference databaseRef;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        initializeComponents();

//        if(fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//        }

        setTypeOfUserList();
        chooseTypeOfUser();
        onRegisterButtonClick();
        onLoginTextClick();

    }

    public void initializeComponents(){
        EName = (EditText)findViewById(R.id.ENameRegister);
        EEmail = (EditText)findViewById(R.id.EEmailRegister);
        EPassword = (EditText)findViewById(R.id.EPasswordRegister);
        BRegister = (Button)findViewById(R.id.BRegister);
        TLoginBtn = findViewById(R.id.TLogin);
        progressBar = findViewById(R.id.progressBarRegister);
        typeOfUserSpinner = findViewById(R.id.typeOfUserSpinner);

        fAuth = FirebaseAuth.getInstance();
    }

    public void setTypeOfUserList(){
        typeOfUserList = new ArrayList<String>();
        typeOfUserList.add("--Please select type of user");
        typeOfUserList.add("Restaurant");
        typeOfUserList.add("Charitable organisation");
    }

    public void onRegisterButtonClick(){
        BRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EEmail.getText().toString().trim();
                String password = EPassword.getText().toString().trim();
                final String name = EName.getText().toString();

                if(TextUtils.isEmpty(email)){
                    EEmail.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    EPassword.setError("Password is required");
                    return;
                }

                if(password.length() < 8){
                    EPassword.setError("Password must be at least 8 characters long");
                    return;
                }

                if(typeOfUser.length()==0){
                    Toast.makeText(Register.this, "Please select type of user", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener  <AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            userUID = fAuth.getCurrentUser().getUid();
                            databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
                            databaseRef.child("Name").setValue(name);
                            databaseRef.child("Type").setValue(typeOfUser);
                            Toast.makeText(Register.this, "User created", Toast.LENGTH_SHORT).show();
                            //getCoordinates();
//                            databaseRef.child("Location").child("Latitude").setValue(latitude);
//                            databaseRef.child("Location").child("Longitude").setValue(longitude);

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();

                        }else{
                            Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
    }

    public void chooseTypeOfUser() {
        typeOfUserAdapter = new ArrayAdapter<String>(this, R.layout.style_spinner, typeOfUserList);
        typeOfUserSpinner.setAdapter(typeOfUserAdapter);
        typeOfUserAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        typeOfUserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    typeOfUser = "restaurant";
                }
                if (position == 2) {
                    typeOfUser = "organisation";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    public void onLoginTextClick(){
        TLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }

}