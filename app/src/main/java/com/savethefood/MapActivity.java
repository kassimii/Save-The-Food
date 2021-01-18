package com.savethefood;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private FirebaseAuth fAuth;
    private DatabaseReference databaseRef;
    private String userUID;
    private String typeOfUser;

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private String name;
    private String timeStamp;
    private String organisationUID;


    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fAuth = FirebaseAuth.getInstance();
        timeStamp = new SimpleDateFormat("dd MM yyyy").format(Calendar.getInstance().getTime());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }


    public void getDataFromFirebase() {
        userUID = fAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Iterator<DataSnapshot> items = snapshot.getChildren().iterator();
                while (items.hasNext()) {
                    DataSnapshot item = items.next();

                    typeOfUser = item.child("Type").getValue().toString();

                    if (typeOfUser.equals("organisation")) {

                        if (item.child("Requests").hasChild(timeStamp)) {
                            if (item.child("Requests").child(timeStamp).child("Received today").getValue().toString().equals("NO")) {

                                latitude = item.child("Location").child("Latitude").getValue(Double.class);
                                longitude = item.child("Location").child("Longitude").getValue(Double.class);
                                name = item.child("Name").getValue().toString();
                                organisationUID = item.getKey();


                                LatLng location = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(location).title(String.valueOf(name)).icon(bitmanDescriptorFromVector(getApplicationContext(),R.drawable.ic_baseline_place_24)).snippet(organisationUID));
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private BitmapDescriptor bitmanDescriptorFromVector(Context context,int vectorResId){
        Drawable vectorDrawable=ContextCompat.getDrawable(context,vectorResId);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap=Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

       if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }
        }

        getDataFromFirebase();


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markerTitle = marker.getTitle();

                Intent i = new Intent(MapActivity.this, DetailsActivity.class);

                i.putExtra("title", markerTitle);//passing title to the new Activity
                i.putExtra("uid", marker.getSnippet());

                startActivity(i);

                return false;
            }
        });
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if(requestCode==ACCESS_LOCATION_REQUEST_CODE){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
               enableUserLocation();
            }else{
                Toast.makeText(MapActivity.this, "Permission not granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}