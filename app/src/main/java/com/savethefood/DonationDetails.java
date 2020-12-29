package com.savethefood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DonationDetails extends AppCompatActivity {
    private TextView TVFrom, TVWhat, TVWhen;
    private Button BConfirmDonationReceived;
    private String donationStatus;

    private FirebaseAuth fAuth;
    private DatabaseReference databaseRef,receivedRef;
    private String userUID, donationUID;
    private String timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_details);

        timeStamp = new SimpleDateFormat("dd MM yyyy").format(Calendar.getInstance().getTime());

        initializeDatabaseConstants();
        initializeComponents();
        setDonationDetails();
        onConfirmDonationButtonClick();
    }

    public void initializeDatabaseConstants(){
        fAuth = FirebaseAuth.getInstance();
        userUID = fAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Donations");
        receivedRef=FirebaseDatabase.getInstance().getReference().child("Users").child(userUID).child("Requests").child(timeStamp);
    }

    public void initializeComponents(){
        TVFrom = (TextView)findViewById(R.id.TVFrom);
        TVWhat = (TextView)findViewById(R.id.TVWhat);
        TVWhen = (TextView)findViewById(R.id.TVWhen);
        BConfirmDonationReceived = (Button)findViewById(R.id.BConfirmDonationReceived);
    }

    public void setDonationDetails(){
        Intent intent = getIntent();
        TVFrom.setText(intent.getStringExtra("Restaurant"));
        TVWhat.setText(intent.getStringExtra("What"));
        TVWhen.setText(intent.getStringExtra("When"));
        donationUID = intent.getStringExtra("DonationUID");
        donationStatus = intent.getStringExtra("Status");

        if(donationStatus.equals("Received")){
            BConfirmDonationReceived.setText("Donation already received");
            BConfirmDonationReceived.setEnabled(false);
        }
    }

    public void onConfirmDonationButtonClick(){
        BConfirmDonationReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseRef.child(donationUID).child("Status").setValue("Received");
                receivedRef.child("Received today").setValue("YES");
                Toast.makeText(DonationDetails.this, "Changed donation status.", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("DestinationFragment", 2);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 1) {
            //no fragments left
            finish();
        } else {
            super.onBackPressed();
        }

    }
}