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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


public class DonationsReceivedFragment extends Fragment implements RequestDialog.OnInputSelected {
    private Button BNewRequest;
    private String receivedNumberOfPersons, receivedSpecialRequest, timeStamp;
    private ArrayList<Donation> donations = new ArrayList<>();
    private ListView LVDonations;

    private FirebaseAuth fAuth;
    private DatabaseReference databaseRef;
    private String userUID, userName;


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
        getUserName();
        getTodaysRequest();
        addNewRequest();
        getDonations();
    }

    public void initializeDatabaseConstants(){
        fAuth = FirebaseAuth.getInstance();
        userUID = fAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
    }

    public void getUserName(){
        DatabaseReference reference = databaseRef;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("Name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    private static class Donation{
        public String donationUID, to, from, when, what, status;

        public Donation(String donationUID, String to, String from, String when, String what, String status){
            this.donationUID = donationUID;
            this.to = to;
            this.from = from;
            this.when = when;
            this.what = what;
            this.status = status;
        }
    }

    public void getDonations(){
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference().child("Donations");
        donationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterator<DataSnapshot> items = snapshot.getChildren().iterator();

                while(items.hasNext()){
                    DataSnapshot item = items.next();
                    String donationUID, to, from, when, what, status;
                    to = item.child("To").getValue().toString();
                    if(to.equals(userName)){
                        donationUID = item.getKey().toString();
                        from = item.child("From").getValue().toString();
                        when = item.child("When").getValue().toString();
                        what = item.child("What").getValue().toString();
                        status = item.child("Status").getValue().toString();
                    }else {
                        continue;
                    }
                    Donation donation = new Donation(donationUID, to, from, when, what, status);
                    donations.add(donation);
                }

                showDonations();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void showDonations(){

        CustomAdapter customAdapter = new CustomAdapter();
        LVDonations.setAdapter(customAdapter);

        LVDonations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DonationDetails.class);
                intent.putExtra("DonationUID", donations.get(position).donationUID);
                intent.putExtra("From", donations.get(position).from);
                intent.putExtra("What", donations.get(position).what);
                intent.putExtra("When", donations.get(position).when);
                intent.putExtra("Status", donations.get(position).status);

                startActivity(intent);
            }
        });

    }

    private class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return donations.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View donationRowView = getLayoutInflater().inflate(R.layout.donations_row_data, null);

            TextView TVFromRow = (TextView) donationRowView.findViewById(R.id.TVFromRow);
            TextView TVWhatRow = (TextView) donationRowView.findViewById(R.id.TVWhatRow);
            TextView TVStatus = (TextView) donationRowView.findViewById(R.id.TVStatus);

            TVFromRow.setText(donations.get(position).from);
            TVWhatRow.setText(donations.get(position).what);
            TVStatus.setText(donations.get(position).status);

            return donationRowView;
        }
    }

}