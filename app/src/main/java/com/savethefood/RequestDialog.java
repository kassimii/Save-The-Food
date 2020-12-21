package com.savethefood;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class RequestDialog extends DialogFragment {
    private static final String TAG = "RequestDialog";
    private EditText ENumberOfPersons,ESpecialRequest;
    private TextView TCancel, TAdd;

    private String timeStamp, personsTodaysRequestFromDB, specialTodaysRequestFromDB;

    private FirebaseAuth fAuth; //1
    private DatabaseReference databaseRef;
    private String userUID;

    public interface OnInputSelected{
        void sendInput(String numberOfPersons, String specialRequest);
    }

    public OnInputSelected onInputSelected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_request_dialog, container, false);
        TCancel = (TextView)view.findViewById(R.id.TCancel);
        TAdd = (TextView)view.findViewById(R.id.TAdd);
        ENumberOfPersons = (EditText) view.findViewById(R.id.ENumberOfPersons);
        ESpecialRequest = (EditText)view.findViewById(R.id.ESpecialRequest);

        timeStamp = new SimpleDateFormat("dd MM yyyy").format(Calendar.getInstance().getTime());

        fAuth = FirebaseAuth.getInstance();
        userUID = fAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);

        DatabaseReference reference = databaseRef;

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Requests").hasChild(timeStamp)){
                    personsTodaysRequestFromDB = dataSnapshot.child("Requests").child(timeStamp).child("Number of persons").getValue().toString();
                    if (dataSnapshot.child("Requests").child(timeStamp).child("Special request").exists()){
                        specialTodaysRequestFromDB = dataSnapshot.child("Requests").child(timeStamp).child("Special request").getValue().toString();
                    }

                    ENumberOfPersons.setText(personsTodaysRequestFromDB);
                    ESpecialRequest.setText(specialTodaysRequestFromDB);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        TAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberOfPersons = ENumberOfPersons.getText().toString();
                String specialRequest = ESpecialRequest.getText().toString();
                if(!numberOfPersons.equals("")){
                    if(specialRequest.equals("")){
                        specialRequest = "None";
                    }
                    onInputSelected.sendInput(numberOfPersons, specialRequest);
                }

                getDialog().dismiss();
            }
        });


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            onInputSelected = (OnInputSelected) getTargetFragment();
        }catch(ClassCastException e){
            Log.d(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}