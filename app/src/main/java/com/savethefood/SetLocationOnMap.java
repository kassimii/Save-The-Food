package com.savethefood;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetLocationOnMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FirebaseAuth fAuth;
    private String userUID;
    private DatabaseReference databaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location_on_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();

                markerOptions.position(latLng);

                markerOptions.title(latLng.latitude + " : " + latLng.longitude); //set title of marker


                fAuth = FirebaseAuth.getInstance();
                userUID = fAuth.getCurrentUser().getUid();
                databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);

                databaseRef.child("Location").child("Latitude").setValue(latLng.latitude);
                databaseRef.child("Location").child("Longitude").setValue(latLng.longitude);
                Toast.makeText(SetLocationOnMap.this, "Location has changed.", Toast.LENGTH_SHORT).show();

                googleMap.clear();//clear the map
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));//zoom marker
                googleMap.addMarker(markerOptions);


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("DestinationFragment",3);
                startActivity(intent);
                finish();


            }
        });
    }
}