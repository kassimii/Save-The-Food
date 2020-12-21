package com.savethefood;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;


public class DonationsReceivedFragment extends Fragment implements RequestDialog.OnInputSelected {
    private Button BNewRequest;
    private String receivedNumberOfPersons, receivedSpecialRequest, timeStamp, todaysRequest;
    private ArrayList<Donation> entries = new ArrayList<>();

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

        timeStamp = new SimpleDateFormat("dd MM yyyy").format(Calendar.getInstance().getTime());

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

        getDonations();
    }

    public void getTodaysRequest(){
        DatabaseReference reference = databaseRef;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.child("Requests").hasChild(timeStamp)){
                   BNewRequest.setVisibility(View.GONE);
               }else{
                   BNewRequest.setVisibility(View.VISIBLE);
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
        public String to, from, when, what;

        public Donation(String to, String from, String when, String what){
            this.to = to;
            this.from = from;
            this.when = when;
            this.what = what;
        }
    }

    public void getDonations(){
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference().child("Donations");
        donationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                Toast.makeText(getActivity(), "Total donations: " + snapshot.getChildrenCount(), Toast.LENGTH_SHORT).show();

                while(items.hasNext()){
                    DataSnapshot item = items.next();
                    String to, from, when, what;
                    to = item.child("To").getValue().toString();
                    from = item.child("From").getValue().toString();
                    when = item.child("When").getValue().toString();
                    what = item.child("What").getValue().toString();

                    Donation entry = new Donation(to, from, when, what);
                    entries.add(entry);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}