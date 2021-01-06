package com.savethefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private EditText EEmail, EPassword;
    private Button BLogin;
    private TextView TRegisterBtn, TForgotPassword;
    private ProgressBar progressBar;

    private FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeComponents();
        onLoginButtonClick();
        onRegisterTextClick();
        onForgotPasswordTextClick();
    }

    public void initializeComponents(){
        EEmail = (EditText)findViewById(R.id.EEmailLogin);
        EPassword = (EditText)findViewById(R.id.EPasswordLogin);
        BLogin = (Button)findViewById(R.id.BLogin);
        TRegisterBtn = findViewById(R.id.TRegister);
        TForgotPassword = findViewById(R.id.TForgotPassword);
        progressBar = findViewById(R.id.progressBarLogin);

        fAuth = FirebaseAuth.getInstance();
    }

    public void onLoginButtonClick(){
        BLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = EEmail.getText().toString().trim();
                String password = EPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    EEmail.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    EPassword.setError("Password is required");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("DestinationFragment", 1);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(Login.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    public void onRegisterTextClick(){
        TRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
                finish();
            }
        });
    }

    public void onForgotPasswordTextClick(){
        TForgotPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               final EditText resetEmail = new EditText(v.getContext());
               final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
               passwordResetDialog.setTitle("Reset password");
               passwordResetDialog.setMessage("Enter your email");
               passwordResetDialog.setView(resetEmail);

               passwordResetDialog.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String email = resetEmail.getText().toString();
                       fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(Login.this, "Reset link sent!", Toast.LENGTH_SHORT).show();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(Login.this, "Error! Could not send reset link." + e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });

                   }
               });

               passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                   }
               });

               passwordResetDialog.create().show();
            }
        });
    }

}