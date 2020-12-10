package com.savethefood;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.printservice.PrintService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private View view;
    private EditText EProfileName;
    private Button BUpdateProfile;

    private String nameFromDB, nameEdited;

    private FirebaseAuth fAuth;
    private DatabaseReference databaseRef;
    private String userUID;

    public ProfileFragment() {
        // Required empty public constructor
    }

  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_profile, container, false);

        EProfileName = (EditText) view.findViewById(R.id.profile_name);
        BUpdateProfile = (Button) view.findViewById(R.id.BUpdateProfile);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fAuth = FirebaseAuth.getInstance();
        userUID = fAuth.getCurrentUser().getUid();

        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);

        showProfileInfo();
        onUpdateProfileButtonClick();
    }

    private void showProfileInfo() {
        DatabaseReference reference = databaseRef;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nameFromDB = dataSnapshot.child("Name").getValue().toString();
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

}