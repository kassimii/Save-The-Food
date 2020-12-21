package com.savethefood;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class HomeFragment extends Fragment {
    private FirebaseAuth fAuth;
    private DatabaseReference databaseRef;
    private String userUID;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }

    public void initializeDatabaseConstants(){
        fAuth = FirebaseAuth.getInstance();
        userUID = fAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Donations");

        String uniqueID = UUID.randomUUID().toString();
        databaseRef.child(uniqueID).child("To").setValue("pati");
        databaseRef.child(uniqueID).child("From").setValue("kfc");
        databaseRef.child(uniqueID).child("What").setValue("zupa");
        databaseRef.child(uniqueID).child("When").setValue("23 12 2020");
        databaseRef.child(uniqueID).child("Status").setValue("Not received");

        uniqueID = UUID.randomUUID().toString();
        databaseRef.child(uniqueID).child("To").setValue("pati");
        databaseRef.child(uniqueID).child("From").setValue("kfc");
        databaseRef.child(uniqueID).child("What").setValue("piftele");
        databaseRef.child(uniqueID).child("When").setValue("23 12 2020");
        databaseRef.child(uniqueID).child("Status").setValue("Not received");

        uniqueID = UUID.randomUUID().toString();
        databaseRef.child(uniqueID).child("To").setValue("pati");
        databaseRef.child(uniqueID).child("From").setValue("kfc");
        databaseRef.child(uniqueID).child("What").setValue("sonc");
        databaseRef.child(uniqueID).child("When").setValue("23 12 2020");
        databaseRef.child(uniqueID).child("Status").setValue("Not received");

        uniqueID = UUID.randomUUID().toString();
        databaseRef.child(uniqueID).child("To").setValue("pati");
        databaseRef.child(uniqueID).child("From").setValue("kfc");
        databaseRef.child(uniqueID).child("What").setValue("clisa");
        databaseRef.child(uniqueID).child("When").setValue("23 12 2020");
        databaseRef.child(uniqueID).child("Status").setValue("Not received");

        uniqueID = UUID.randomUUID().toString();
        databaseRef.child(uniqueID).child("To").setValue("pati");
        databaseRef.child(uniqueID).child("From").setValue("kfc");
        databaseRef.child(uniqueID).child("What").setValue("galusce");
        databaseRef.child(uniqueID).child("When").setValue("23 12 2020");
        databaseRef.child(uniqueID).child("Status").setValue("Not received");

        uniqueID = UUID.randomUUID().toString();
        databaseRef.child(uniqueID).child("To").setValue("pati");
        databaseRef.child(uniqueID).child("From").setValue("kfc");
        databaseRef.child(uniqueID).child("What").setValue("borandau");
        databaseRef.child(uniqueID).child("When").setValue("23 12 2020");
        databaseRef.child(uniqueID).child("Status").setValue("Not received");

        uniqueID = UUID.randomUUID().toString();
        databaseRef.child(uniqueID).child("To").setValue("pati");
        databaseRef.child(uniqueID).child("From").setValue("kfc");
        databaseRef.child(uniqueID).child("What").setValue("carnate");
        databaseRef.child(uniqueID).child("When").setValue("23 12 2020");
        databaseRef.child(uniqueID).child("Status").setValue("Not received");

        uniqueID = UUID.randomUUID().toString();
        databaseRef.child(uniqueID).child("To").setValue("pati");
        databaseRef.child(uniqueID).child("From").setValue("kfc");
        databaseRef.child(uniqueID).child("What").setValue("zacusca");
        databaseRef.child(uniqueID).child("When").setValue("23 12 2020");
        databaseRef.child(uniqueID).child("Status").setValue("Not received");

        uniqueID = UUID.randomUUID().toString();
        databaseRef.child(uniqueID).child("To").setValue("pati");
        databaseRef.child(uniqueID).child("From").setValue("kfc");
        databaseRef.child(uniqueID).child("What").setValue("alcoale");
        databaseRef.child(uniqueID).child("When").setValue("23 12 2020");
        databaseRef.child(uniqueID).child("Status").setValue("Not received");
    }
}