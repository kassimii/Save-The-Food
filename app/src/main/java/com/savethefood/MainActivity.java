package com.savethefood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private DatabaseReference databaseRef;
    private String userUID;
    private String typeOfUser;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        showNavbar();
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser fUser = fAuth.getCurrentUser();
        if(fUser != null){
            getTypeOfUserFromDB();
        }else {
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    public void getTypeOfUserFromDB(){
        userUID = fAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                typeOfUser = snapshot.child("Type").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showNavbar(){
        bottomNavigationView = findViewById(R.id.bottom_navbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId())
            {
                case R.id.home:
                    fragment = new HomeFragment();
                    break;
                case R.id.search:
                    if(typeOfUser.equals("restaurant"))
                    {
                        fragment = new SearchFragment();
                    }else if(typeOfUser.equals("organisation")){
                        fragment = new DonationsReceivedFragment();
                    }
                    break;
                case R.id.profile:
                    fragment = new ProfileFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

            return true;
        }
    };

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getBaseContext(), Login.class));
        finish();
    }
}