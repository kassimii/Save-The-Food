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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OrganisationProfileFragment extends Fragment implements RequestDialog.OnInputSelected{
    private View view;
    private EditText EProfileName, EProfileAddress;
    private TextView TTodaysPersons, TTodaysSpecial;
    private Button BUpdateProfile, BChangeTodaysRequest,BChangeLocation;

    private String nameFromDB, addressFromDB="", personsTodaysRequestFromDB, specialTodaysRequestFromDB, nameEdited, addressEdited, timeStamp, receivedNumberOfPersons, receivedSpecialRequest;

    private FirebaseAuth fAuth;
    private DatabaseReference databaseRef;
    private String userUID;

    @Override
    public void sendInput(String numberOfPersons, String specialRequest) {
        receivedNumberOfPersons = numberOfPersons;
        receivedSpecialRequest = specialRequest;

        databaseRef.child("Requests").child(timeStamp).child("Number of persons").setValue(receivedNumberOfPersons);
        databaseRef.child("Requests").child(timeStamp).child("Special request").setValue(receivedSpecialRequest);
        databaseRef.child("Requests").child(timeStamp).child("Received today").setValue("NO");

        Toast.makeText(getActivity(), "Request changed", Toast.LENGTH_SHORT).show();

        showTodaysRequest();
    }

    public OrganisationProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_organisation_profile, container, false);

        timeStamp = new SimpleDateFormat("dd MM yyyy").format(Calendar.getInstance().getTime());

        EProfileName = (EditText) view.findViewById(R.id.profile_name);
        EProfileAddress = (EditText) view.findViewById(R.id.profile_address);
        BUpdateProfile = (Button) view.findViewById(R.id.BUpdateProfile);
        BChangeLocation = (Button) view.findViewById(R.id.changeLocation);
        TTodaysPersons = (TextView)view.findViewById(R.id.TTodaysPersons);
        TTodaysSpecial = (TextView)view.findViewById(R.id.TTodaysSpecial);
        BChangeTodaysRequest = (Button) view.findViewById(R.id.BChangeTodaysRequest);

        fAuth = FirebaseAuth.getInstance();
        userUID = fAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);

        showProfileInfo();

        return view;
    }
    public void onChangeLocationButtonClick(){
        BChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SetLocationOnMap.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        onUpdateProfileButtonClick();
        changeTodaysRequest();
        onChangeLocationButtonClick();
    }

    private void showProfileInfo() {
        DatabaseReference reference = databaseRef;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameFromDB = dataSnapshot.child("Name").getValue().toString();
                EProfileName.setText(nameFromDB);
                if(dataSnapshot.hasChild("Address")){
                    addressFromDB = dataSnapshot.child("Address").getValue().toString();
                    EProfileAddress.setText(addressFromDB);
                }


                if(dataSnapshot.child("Requests").hasChild(timeStamp)){
                    personsTodaysRequestFromDB = dataSnapshot.child("Requests").child(timeStamp).child("Number of persons").getValue().toString();
                    if (dataSnapshot.child("Requests").child(timeStamp).child("Special request").exists()){
                        specialTodaysRequestFromDB = dataSnapshot.child("Requests").child(timeStamp).child("Special request").getValue().toString();
                    }

                    showTodaysRequest();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void onUpdateProfileButtonClick(){
        BUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEdited = EProfileName.getText().toString().trim();
                addressEdited = EProfileAddress.getText().toString().trim();
                if(!nameEdited.equals(nameFromDB) || !addressEdited.equals(addressFromDB)){
                    databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
                    databaseRef.child("Name").setValue(nameEdited);
                    databaseRef.child("Address").setValue(addressEdited);
                    Toast.makeText(getActivity(), "Profile updated!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Already up to date!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void changeTodaysRequest(){
        BChangeTodaysRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestDialog requestDialog = new RequestDialog();
                requestDialog.setTargetFragment(OrganisationProfileFragment.this, 1);
                requestDialog.show(getFragmentManager(), "Request dialog");
            }
        });
    }

    public void showTodaysRequest(){
        TTodaysPersons.setText(personsTodaysRequestFromDB);
        TTodaysSpecial.setText(specialTodaysRequestFromDB);
    }

}