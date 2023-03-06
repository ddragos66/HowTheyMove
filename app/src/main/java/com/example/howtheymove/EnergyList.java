package com.example.howtheymove;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import com.example.howtheymove.databinding.ActivityEnergyListBinding;


public class EnergyList extends AppCompatActivity {

    public ActivityEnergyListBinding binding;
    String listIcon[] = {"Kinetic Energy", "Potential Energy"};
    int listImage [] = {R.drawable.kineticenergy, R.drawable.potentialenergy};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_list);

        binding = ActivityEnergyListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //start OF ANIMATED BACKGROUND
        AnimationDrawable animationDrawable = (AnimationDrawable) binding.energyLayoutAnimation.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
        //END OF ANIMATED BACKGROUND


        CustomeBaseAdaptor customeBaseAdaptor = new CustomeBaseAdaptor(getApplicationContext(), listIcon, listImage);
        binding.customListView.setAdapter(customeBaseAdaptor);
        binding.customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(EnergyList.this, MoreInformations.class);

                Bundle bundle = new Bundle();
                bundle.putString("pos", String.valueOf(position));
                String des = "some string";
                bundle.putString("des", des);

                intent.putExtra("myPackage", bundle);
                startActivity(intent);
            }
        });
    }
}