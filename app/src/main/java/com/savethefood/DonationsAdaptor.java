package com.savethefood;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.savethefood.model.Donation;

import java.util.List;

public class DonationsAdaptor extends ArrayAdapter<Donation> {

    private Context context;
    private List<Donation> donations;
    private int layoutResID;

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

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            itemHolder = new ItemHolder();

            view = inflater.inflate(layoutResID, parent, false);
            itemHolder.TVFromRow = (TextView) view.findViewById(R.id.TVFromRow);
            itemHolder.TVWhatRow = (TextView) view.findViewById(R.id.TVWhatRow);
            itemHolder.TVStatus = (TextView) view.findViewById(R.id.TVStatus);

            view.setTag(itemHolder);

        } else {
            itemHolder = (ItemHolder) view.getTag();
        }

        final Donation dItem = donations.get(position);

        itemHolder.TVFromRow.setText(dItem.Restaurant);
        itemHolder.TVWhatRow.setText(dItem.What);
        itemHolder.TVStatus.setText(dItem.Status);

        return view;
    }

    private static class ItemHolder {
        TextView TVFromRow;
        TextView TVWhatRow;
        TextView TVStatus;
    }
}