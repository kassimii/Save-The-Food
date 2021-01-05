package com.savethefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import java.util.Iterator;
import java.util.UUID;

public class DetailsActivity extends AppCompatActivity {

    TextView markerTitle;
    TextView numberText;
    TextView needsText;
    TextView locationText;
    EditText whatText;
    Button donateButton;

    private FirebaseAuth fAuth;
    private DatabaseReference databaseRef;
    private String userUID;
    private String nameOfUser;
    private String title;
    private String organisationID;

    private String specialRequest;
    private String nrOfPers;
    private String timeStamp;
    private String address;

    private String nameOfRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        fAuth = FirebaseAuth.getInstance();
        userUID = fAuth.getCurrentUser().getUid();

        timeStamp = new SimpleDateFormat("dd MM yyyy").format(Calendar.getInstance().getTime());

        markerTitle=findViewById(R.id.marker);
        numberText=(TextView) findViewById(R.id.number);
        needsText=(TextView) findViewById(R.id.needs);
        locationText=(TextView) findViewById(R.id.location);
        donateButton=(Button)findViewById(R.id.toDonate);
        whatText=(EditText)findViewById(R.id.what);

        title=getIntent().getStringExtra("title");
        organisationID=getIntent().getStringExtra("uid");
        markerTitle.setText(title);

        getDetailsFromFirebase();
        getNameOfRestaurant();
        onDonateButton();


    }

    public void getDetailsFromFirebase(){
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                while (items.hasNext()) {
                    DataSnapshot item = items.next();



                    if (organisationID.equals(item.getKey())) {

                        if(item.child("Requests").hasChild(timeStamp)) {

                            if(item.hasChild("Address")) {

                                address = item.child("Address").getValue().toString();
                                locationText.setText(address);
                            }


                            nrOfPers = item.child("Requests").child(timeStamp).child("Number of persons").getValue().toString();
                            numberText.setText(nrOfPers);

                            specialRequest = item.child("Requests").child(timeStamp).child("Special request").getValue().toString();
                            needsText.setText(specialRequest);
                        }


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void getNameOfRestaurant(){
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameOfRestaurant=snapshot.child(userUID).child("Name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    public void onDonateButton(){
        databaseRef=FirebaseDatabase.getInstance().getReference().child("Donations");
        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String whatWeDonate = whatText.getText().toString();

                if (TextUtils.isEmpty(whatWeDonate)) {
                    whatText.setError("Donation details required");
                    return;
                }

                String uniqueID = UUID.randomUUID().toString();
                databaseRef.child(uniqueID).child("From").setValue(userUID);
                databaseRef.child(uniqueID).child("Restaurant").setValue(nameOfRestaurant);
                databaseRef.child(uniqueID).child("Status").setValue("Not received");
                databaseRef.child(uniqueID).child("To").setValue(organisationID);
                databaseRef.child(uniqueID).child("Organisation").setValue(title);
                databaseRef.child(uniqueID).child("What").setValue(whatWeDonate);
                databaseRef.child(uniqueID).child("When").setValue(timeStamp);

                DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Users");
                reference.child(organisationID).child("Received today").setValue("YES");

                Toast.makeText(DetailsActivity.this, "Donation sent", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }


}