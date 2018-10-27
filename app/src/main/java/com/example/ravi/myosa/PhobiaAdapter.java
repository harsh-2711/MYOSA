package com.example.ravi.myosa;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PhobiaAdapter extends ArrayAdapter<Phobia> {

    public PhobiaAdapter(Context context, ArrayList<Phobia> phobia) {
        super(context,0,phobia);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Phobia phobia = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.phobia_template, parent, false);
        }

        TextView index = (TextView) convertView.findViewById(R.id.index);
        TextView phobiaName = (TextView) convertView.findViewById(R.id.phobiaName);

        index.setText(String.valueOf(phobia.getIndex() + 1) + ". ");
        phobiaName.setText(phobia.getPhobia());

        return convertView;
    }
}
