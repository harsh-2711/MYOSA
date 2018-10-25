package com.example.ravi.myosa;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

/**
 * Created by ravi on 05-04-2018.
 */

public class DialogRecyclerAdapter extends RecyclerView.Adapter<DialogRecyclerAdapter.ViewHolder> {

    private LayoutInflater inflator;
    private Context context;

    public DialogRecyclerAdapter(Context context) {
        this.context = context;
        inflator = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.dialog_recycler, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.cSensor.setText(sensorDetails.SensorAttributes.get(position).getAttName());

        if (!sensorDetails.SensorAttributes.get(position).isNum()) {
            holder.onlyString();
        } else {
            holder.stringOnly.setVisibility(View.GONE);
            holder.valueOnly.setVisibility(View.VISIBLE);
            holder.rg.setVisibility(View.VISIBLE);
        }
        holder.cSensor.setOnCheckedChangeListener(null);
        holder.rg.setOnCheckedChangeListener(null);
        holder.cSensor.setChecked(sensorDetails.eventAttributes.get(sensorDetails.evntCreated)[position].selected);
        if (sensorDetails.eventAttributes.get(sensorDetails.evntCreated)[position].selected) {
            holder.LinearGone.setVisibility(View.VISIBLE);
        } else {
            holder.LinearGone.setVisibility(View.GONE);
        }
        //if condition for radio button selection.
        if (sensorDetails.eventAttributes.get(sensorDetails.evntCreated)[position].inclusive) {
            holder.rg.check(holder.inc.getId());
        } else {
            holder.rg.check(holder.exc.getId());
        }
        holder.cSensor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sensorDetails.eventAttributes.get(sensorDetails.evntCreated)[position].selected = true;
                    holder.LinearGone.setVisibility(View.VISIBLE);
                } else {
                    sensorDetails.eventAttributes.get(sensorDetails.evntCreated)[position].selected = false;
                    holder.LinearGone.setVisibility(View.GONE);
                }

            }
        });
        holder.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                holder.rbId = checkedId;
                if (holder.exc.getId() == holder.rbId) {
                    sensorDetails.eventAttributes.get(sensorDetails.evntCreated)[position].inclusive = false;
                } else {

                    sensorDetails.eventAttributes.get(sensorDetails.evntCreated)[position].inclusive = true;
                }
            }
        });
        holder.bsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.emax.getText().toString().trim().equals("")) {
                    sensorDetails.eventAttributes.get(sensorDetails.evntCreated)[position].max = 100000;
                } else {
                    sensorDetails.eventAttributes.get(sensorDetails.evntCreated)[position].max = Double.parseDouble(holder.emax.getText().toString());
                }
                if (holder.emin.getText().toString().trim().equals("")) {
                    sensorDetails.eventAttributes.get(sensorDetails.evntCreated)[position].min = -1000000;
                } else {
                    sensorDetails.eventAttributes.get(sensorDetails.evntCreated)[position].min = Double.parseDouble(holder.emin.getText().toString());
                }
                sensorDetails.eventAttributes.get(sensorDetails.evntCreated)[position].s = holder.estring.getText().toString();
            }
        });


    }

    @Override
    public int getItemCount() {
        return sensorDetails.TOTAL_VALUES;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout LinearGone;
        CheckBox cSensor;
        EditText emax;
        EditText emin;
        EditText estring;
        int rbId;
        RadioButton inc;
        RadioButton exc;
        RadioGroup rg;
        Button bsave;
        View stringOnly;
        View valueOnly;

        public ViewHolder(View itemView) {
            super(itemView);
            valueOnly = itemView.findViewById(R.id.valueonly);
            stringOnly = itemView.findViewById(R.id.stringOnly);
            bsave = itemView.findViewById(R.id.bsave);
            inc = itemView.findViewById(R.id.cInclusive);
            exc = itemView.findViewById(R.id.cExclusive);
            estring = itemView.findViewById(R.id.estring);
            emin = itemView.findViewById(R.id.emin);
            emax = itemView.findViewById(R.id.emax);
            LinearGone = itemView.findViewById(R.id.LinearGone);
            cSensor = itemView.findViewById(R.id.cSensor);
            rg = itemView.findViewById(R.id.Rg);
        }

        public void onlyString() {
            stringOnly.setVisibility(View.VISIBLE);
            valueOnly.setVisibility(View.GONE);
            rg.setVisibility(View.GONE);
        }
    }
}
