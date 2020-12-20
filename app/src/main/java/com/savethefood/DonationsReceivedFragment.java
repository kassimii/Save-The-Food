package com.savethefood;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DonationsReceivedFragment extends Fragment implements RequestDialog.OnInputSelected {
    private Button BNewRequest;
    private String receivedNumberOfPersons, receivedSpecialRequest, timeStamp, todaysRequest;

    private FirebaseAuth fAuth;
    private DatabaseReference databaseRef;
    private String userUID;


    @Override
    public void sendInput(String numberOfPersons, String specialRequest) {
        receivedNumberOfPersons = numberOfPersons;
        receivedSpecialRequest = specialRequest;

        databaseRef.child("Requests").child(timeStamp).child("Number of persons").setValue(receivedNumberOfPersons);
        databaseRef.child("Requests").child(timeStamp).child("Special request").setValue(receivedSpecialRequest);


        Toast.makeText(getActivity(), "Request added", Toast.LENGTH_SHORT).show();
    }

    public DonationsReceivedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donations_received, container, false);

        timeStamp = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

        BNewRequest = (Button) view.findViewById(R.id.BNewRequest);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
        userUID = fAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);

        getTodaysRequest();
        addNewRequest();
    }

    public void getTodaysRequest(){
        DatabaseReference reference = databaseRef;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               todaysRequest = dataSnapshot.child("Requests").child(timeStamp).getValue().toString();

               if(!todaysRequest.equals("")){
                   BNewRequest.setVisibility(View.GONE);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addNewRequest(){
        BNewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestDialog requestDialog = new RequestDialog();
                requestDialog.setTargetFragment(DonationsReceivedFragment.this, 1);
                requestDialog.show(getFragmentManager(), "Request dialog");

            }
        });
    }


}