package com.savethefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private EditText EName, ELocation, EEmail, EPassword;
    private Button BRegister;
    private TextView TLoginBtn;
    private FirebaseAuth fAuth;
    private ProgressBar progressBar;

    private String userUID;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeComponents();

//        if(fAuth.getCurrentUser() != null){
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//        }

        onRegisterButtonClick();
        onLoginTextClick();
    }

    public void initializeComponents(){
        EName = (EditText)findViewById(R.id.ENameRegister);
        ELocation = (EditText)findViewById(R.id.ELocationRegister);
        EEmail = (EditText)findViewById(R.id.EEmailRegister);
        EPassword = (EditText)findViewById(R.id.EPasswordRegister);
        BRegister = (Button)findViewById(R.id.BRegister);
        TLoginBtn = findViewById(R.id.TLogin);
        progressBar = findViewById(R.id.progressBarRegister);

        fAuth = FirebaseAuth.getInstance();
    }

    public void onRegisterButtonClick(){
        BRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EEmail.getText().toString().trim();
                String password = EPassword.getText().toString().trim();
                final String name = EName.getText().toString();
                final String location = ELocation.getText().toString();

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

                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener  <AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            userUID = fAuth.getCurrentUser().getUid();
                            databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
                            databaseRef.child("Name").setValue(name);
                            databaseRef.child("Location").setValue(location);
                            Toast.makeText(Register.this, "User created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
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