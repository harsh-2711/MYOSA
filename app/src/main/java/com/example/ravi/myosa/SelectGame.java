package com.example.ravi.myosa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ravi.myosa.R;

import java.util.ArrayList;

public class SelectGame extends AppCompatActivity {

    ListView listView;
    ArrayList<Phobia> phobias;
    PhobiaAdapter phobiaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);

        listView = (ListView) findViewById(R.id.listView);

        phobias = new ArrayList<>();
        phobias.add(new Phobia("Acrophobia",0));
        phobias.add(new Phobia("Phobophobia",1));
        phobias.add(new Phobia("Claustrophobia",2));
        phobias.add(new Phobia("Cynophobia",3));
        phobias.add(new Phobia("Hemophobia",4));

        phobiaAdapter = new PhobiaAdapter(this, phobias);
        listView.setAdapter(phobiaAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Phobia phobia = (Phobia) adapterView.getItemAtPosition(i);
                int position = phobia.getIndex();
                getSharedPreferences("phobiaIndex",MODE_PRIVATE).edit().putInt("position",position).apply();
                Intent intent = new Intent(SelectGame.this, Dashboard.class);
                startActivity(intent);
            }
        });

    }
}
