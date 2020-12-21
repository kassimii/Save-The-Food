package com.savethefood;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class DonationDetails extends AppCompatActivity {
    private TextView TVFrom, TVWhat, TVWhen;
    private Button BConfirmDonationReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_details);

        initializeComponents();
        setDonationDetails();
    }

    public void initializeComponents(){
        TVFrom = (TextView)findViewById(R.id.TVFrom);
        TVWhat = (TextView)findViewById(R.id.TVWhat);
        TVWhen = (TextView)findViewById(R.id.TVWhen);
        BConfirmDonationReceived = (Button)findViewById(R.id.BConfirmDonationReceived);
    }

    public void setDonationDetails(){
        Intent intent = getIntent();

        TVFrom.setText(intent.getStringExtra("From"));
        TVWhat.setText(intent.getStringExtra("What"));
        TVWhen.setText(intent.getStringExtra("When"));
    }
}