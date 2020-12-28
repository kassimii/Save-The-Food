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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class ProfileFragment extends Fragment implements RequestDialog.OnInputSelected{
    private View view;
    private EditText EProfileName;
    private TextView TTodaysPersons, TTodaysSpecial;
    private Button BUpdateProfile, BChangeTodaysRequest,BChangeLocation;
    private LinearLayout l4, l5, l6, l7;

    private String nameFromDB, personsTodaysRequestFromDB, specialTodaysRequestFromDB, nameEdited, timeStamp, receivedNumberOfPersons, receivedSpecialRequest;

    private FirebaseAuth fAuth; //1
    private DatabaseReference databaseRef;
    private String userUID,typeOfUser;

    @Override
    public void sendInput(String numberOfPersons, String specialRequest) {
        receivedNumberOfPersons = numberOfPersons;
        receivedSpecialRequest = specialRequest;

        databaseRef.child("Requests").child(timeStamp).child("Number of persons").setValue(receivedNumberOfPersons);
        databaseRef.child("Requests").child(timeStamp).child("Special request").setValue(receivedSpecialRequest);


        Toast.makeText(getActivity(), "Request changed", Toast.LENGTH_SHORT).show();

    }

    public ProfileFragment() {
        // Required empty public constructor
    }

  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_profile, container, false);

        timeStamp = new SimpleDateFormat("dd MM yyyy").format(Calendar.getInstance().getTime());

        EProfileName = (EditText) view.findViewById(R.id.profile_name);
        BUpdateProfile = (Button) view.findViewById(R.id.BUpdateProfile);
        TTodaysPersons = (TextView)view.findViewById(R.id.TTodaysPersons);
        TTodaysSpecial = (TextView)view.findViewById(R.id.TTodaysSpecial);
        BChangeTodaysRequest = (Button) view.findViewById(R.id.BChangeTodaysRequest);
        BChangeLocation=(Button) view.findViewById(R.id.changeLocation);

        l4 = (LinearLayout)view.findViewById(R.id.linearLayout4);
        l5 = (LinearLayout)view.findViewById(R.id.linearLayout5);
        l6 = (LinearLayout)view.findViewById(R.id.linearLayout6);
        l7 = (LinearLayout)view.findViewById(R.id.linearLayout7);

        fAuth = FirebaseAuth.getInstance();
        userUID = fAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);

        showProfileInfo();
        onChangeLocationButtonClick();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        onUpdateProfileButtonClick();
    }

    private void showProfileInfo() {
        DatabaseReference reference = databaseRef;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameFromDB = dataSnapshot.child("Name").getValue().toString();//1
                EProfileName.setText(nameFromDB);
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
                if(!nameEdited.equals(nameFromDB)){
                    databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
                    databaseRef.child("Name").setValue(nameEdited);
                    Toast.makeText(getActivity(), "Profile updated!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Already up to date!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

}