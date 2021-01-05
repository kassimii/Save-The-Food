package com.savethefood;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.savethefood.model.Donation;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private Button searchButton;
    private ListView listView;

    private FirebaseAuth fAuth;
    private DatabaseReference databaseRef;
    private String userUID;

    private List<Donation> donations = new ArrayList<>();
    private DonationsRestaurantAdaptor adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_search, container, false);

        searchButton= (Button) v.findViewById(R.id.searchOnMap);
        listView=(ListView) v.findViewById(R.id.LVDonations);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(getActivity(),MapActivity.class);
                startActivity(intent);
            }
        });

        initializeDatabaseConstants();

        getDonations();

        return v;
    }

    public void initializeDatabaseConstants(){
        fAuth = FirebaseAuth.getInstance();
        userUID = fAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
    }
    public void showDonations(){
        adapter = new DonationsRestaurantAdaptor(getContext(), R.layout.donations_restaurant_row_data, donations);
        listView.setAdapter(adapter);


    }

    public void getDonations(){
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference().child("Donations");
        donationsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Donation donation = snapshot.getValue(Donation.class);
                    donation.donationUID  = snapshot.getKey();
                    String from;

                    from = donation.From;
                    if(from.equals(userUID)){
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

    
}