package com.savethefood;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savethefood.model.Donation;

import java.util.List;

public class DonationsAdaptor extends ArrayAdapter<Donation> {

    private Context context;
    private List<Donation> donations;
    private int layoutResID;

    private FirebaseAuth fAuth;
    private DatabaseReference databaseRef;
    private String userUID;
    private String typeOfUser;

    public DonationsAdaptor(Context context, int layoutResourceID, List<Donation> donations) {
        super(context, layoutResourceID, donations);
        this.context = context;
        this.donations = donations;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder itemHolder;
        View view = convertView;

        fAuth = FirebaseAuth.getInstance();
        userUID = fAuth.getCurrentUser().getUid();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                typeOfUser=snapshot.child(userUID).child("Type").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.TVFromRow = (TextView) view.findViewById(R.id.TVFromRow);
            itemHolder.TVWhatRow = (TextView) view.findViewById(R.id.TVWhatRow);
            itemHolder.TVStatus = (TextView) view.findViewById(R.id.TVStatus);
            itemHolder.TVDate = (TextView) view.findViewById(R.id.TVDate);

            view.setTag(itemHolder);

        } else {
            itemHolder = (ItemHolder) view.getTag();
        }

        final Donation dItem = donations.get(position);

        itemHolder.TVFromRow.setText(dItem.Restaurant);
        itemHolder.TVWhatRow.setText(dItem.What);
        itemHolder.TVStatus.setText(dItem.Status);
        itemHolder.TVDate.setText(dItem.When);

        return view;
    }

    private static class ItemHolder {
        TextView TVFromRow;
        TextView TVWhatRow;
        TextView TVStatus;
        TextView TVDate;
    }
}