package com.example.howtheymove;

import static com.example.howtheymove.R.id.customListView;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class Definitii extends AppCompatActivity {

    String listIcon[] = {"Kinetic Energy", "Potential Energy"};

    int listImage [] = {R.drawable.kineticenergy, R.drawable.potentialenergy};

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definitii);
        listView = (ListView) findViewById(customListView);
        CustomeBaseAdaptor customeBaseAdaptor = new CustomeBaseAdaptor(getApplicationContext(), listIcon, listImage);
        listView.setAdapter(customeBaseAdaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("CUSTOM_LIST_VIEW", "Ite is clicked @ position  :: " + position);
                if (position == 0){

                    startActivity(new Intent(Definitii.this, act1.class));

                } else if (position == 1) {
                    startActivity(new Intent(Definitii.this, act2.class));
                }else {

                }
            }
        });
    }
}