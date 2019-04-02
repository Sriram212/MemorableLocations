package com.example.sriram.memorablelocations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

   static ArrayList<String> memorablePlaces;
   static ArrayList<Double> Latitude;
    static ArrayList<Double> Longitude;

   SharedPreferences sharedPreferences;

   static ArrayAdapter<String> arrayAdapter;

   public void save(View view){
       try {
           sharedPreferences.edit().putString("Memorable Places", ObjectSerializer.serialize(memorablePlaces)).apply();
           sharedPreferences.edit().putString("Latitude", ObjectSerializer.serialize(Latitude)).apply();
           sharedPreferences.edit().putString("Longitude", ObjectSerializer.serialize(Longitude)).apply();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memorablePlaces = new ArrayList<>();
        Latitude = new ArrayList<>();
        Longitude = new ArrayList<>();
        sharedPreferences  = this.getSharedPreferences("com.example.sriram.memorablelocations", Context.MODE_PRIVATE);
        sharedPreferences  = this.getSharedPreferences("com.example.sriram.memorablelocations", Context.MODE_PRIVATE);

        try {
            memorablePlaces = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Memorable Places", ObjectSerializer.serialize(new ArrayList<String>())));
            Latitude = (ArrayList<Double>) ObjectSerializer.deserialize(sharedPreferences.getString("Latitude", ObjectSerializer.serialize(new ArrayList<Double>())));
            Longitude = (ArrayList<Double>) ObjectSerializer.deserialize(sharedPreferences.getString("Longitude", ObjectSerializer.serialize(new ArrayList<Double>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ListView places = findViewById(R.id.listView);

        if (memorablePlaces.size() == 0 && Latitude.size() == 0 && Longitude.size() == 0) {
            memorablePlaces.add("Add a place... ");
            Latitude.add(0D);
            Longitude.add(0D);
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, memorablePlaces);
        places.setAdapter(arrayAdapter);

        places.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    memorablePlaces.remove(i);
                    Latitude.remove(i);
                    Longitude.remove(i);
                    arrayAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Location Removed", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        places.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                intent.putExtra("placeNumber", i);
                startActivity(intent);
            }
        });
    }


}