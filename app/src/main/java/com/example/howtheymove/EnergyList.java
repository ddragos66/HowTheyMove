package com.example.howtheymove;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;


import com.example.howtheymove.databinding.ActivityEnergyListBinding;

import com.google.firebase.database.DatabaseReference;


public class EnergyList extends AppCompatActivity {

    ActivityEnergyListBinding binding;
    DatabaseReference referance;
    String listIcon[] = {"Kinetic Energy", "Potential Energy"};
    int listImage [] = {R.drawable.kineticenergy, R.drawable.potentialenergy};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_list);

        binding = ActivityEnergyListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CustomeBaseAdaptor customeBaseAdaptor = new CustomeBaseAdaptor(getApplicationContext(), listIcon, listImage);
        binding.customListView.setAdapter(customeBaseAdaptor);
        binding.customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("CUSTOM_LIST_VIEW", "Ite is clicked @ position  :: " + position);

                //readData(position);
                Intent intent = new Intent(EnergyList.this, MoreInformations.class);

                String des = "some string";
                Bundle bundle = new Bundle();
                bundle.putString("pos", String.valueOf(position));
                bundle.putString("des", des);

                intent.putExtra("myPackage", bundle);

                Log.d("testdataSENDposition", "Value: " + position );
                startActivity(intent);
            }
        });
    }
}