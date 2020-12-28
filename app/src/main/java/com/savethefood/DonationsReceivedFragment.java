package com.savethefood;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savethefood.model.Donation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class DonationsReceivedFragment extends Fragment implements RequestDialog.OnInputSelected {
    private Button BNewRequest;
    private String receivedNumberOfPersons, receivedSpecialRequest, timeStamp;
    private List<Donation> donations = new ArrayList<>();
    private DonationsAdaptor adapter;
    private ListView LVDonations;

    private FirebaseAuth fAuth;
    private DatabaseReference databaseRef;
    private String userUID;


    @Override
    public void sendInput(String numberOfPersons, String specialRequest) {
        receivedNumberOfPersons = numberOfPersons;
        receivedSpecialRequest = specialRequest;

        databaseRef.child("Requests").child(timeStamp).child("Number of persons").setValue(receivedNumberOfPersons);
        databaseRef.child("Requests").child(timeStamp).child("Special request").setValue(receivedSpecialRequest);

        Toast.makeText(getActivity(), "Request registered.", Toast.LENGTH_SHORT).show();
    }

    public DonationsReceivedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donations_received, container, false);

        timeStamp = new SimpleDateFormat("dd MM yyyy").format(Calendar.getInstance().getTime());

        BNewRequest = (Button) view.findViewById(R.id.BNewRequest);
        LVDonations = (ListView) view.findViewById(R.id.LVDonations);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeDatabaseConstants();
        getTodaysRequest();
        addNewRequest();
        getDonations();
    }

    public void initializeDatabaseConstants(){
        fAuth = FirebaseAuth.getInstance();
        userUID = fAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
    }

    public void getTodaysRequest(){
        DatabaseReference reference = databaseRef;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.child("Requests").hasChild(timeStamp)){
                   BNewRequest.setText("Change request");
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

    public void getDonations(){
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference().child("Donations");
        donationsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Donation donation = snapshot.getValue(Donation.class);
                    donation.donationUID  = snapshot.getKey();
                    String to;

                    to = donation.To;
                    if(to.equals(userUID)){
                        donations.add(donation);
                    }

                    showDonations();
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i = 0; i < donations.size(); i++) {
                    if (donations.get(i).donationUID.equals(snapshot.getKey().toString()))
                        try {
                            Donation updateDonation = snapshot.getValue(Donation.class);

                            donations.set(i, updateDonation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < donations.size(); i++) {
                    if (donations.get(i).donationUID.equals(snapshot.getKey()))
                        donations.remove(i);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void showDonations(){
        adapter = new DonationsAdaptor(getContext(), R.layout.donations_row_data, donations);
        LVDonations.setAdapter(adapter);

        LVDonations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DonationDetails.class);
                intent.putExtra("DonationUID", donations.get(position).donationUID);
                intent.putExtra("Restaurant", donations.get(position).Restaurant);
                intent.putExtra("What", donations.get(position).What);
                intent.putExtra("When", donations.get(position).When);
                intent.putExtra("Status", donations.get(position).Status);

                startActivity(intent);
            }
        });

    }

}