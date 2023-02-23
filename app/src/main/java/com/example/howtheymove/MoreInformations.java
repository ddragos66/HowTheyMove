package com.example.howtheymove;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Toast;

import com.example.howtheymove.databinding.ActivityMoreInformationsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MoreInformations extends AppCompatActivity {

    ActivityMoreInformationsBinding binding;
    DatabaseReference referance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy_list);

        binding = ActivityMoreInformationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //start OF ANIMATED BACKGROUND
        AnimationDrawable animationDrawable = (AnimationDrawable) binding.infoLayoutAnimation.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
        //END OF ANIMATED BACKGROUND

        Intent callerIntent = getIntent();
        Bundle packageCaller=callerIntent.getBundleExtra("myPackage");
        String position = packageCaller.getString("pos");
        readData(Integer.parseInt(String.valueOf(position)));
    }

    private void readData(int position) {
        referance = FirebaseDatabase.getInstance().getReference("energy");
        referance.child(String.valueOf(position)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.getResult().exists()){

                    DataSnapshot dataSnapshot = task.getResult();

                    String title = String.valueOf(dataSnapshot.child("title").getValue());
                    binding.title.setText(title);

                    String energyDefinition = String.valueOf(dataSnapshot.child("definition").getValue());
                    binding.definition.setText(energyDefinition);

                }else {
                    Toast.makeText(MoreInformations.this, "This does not exist!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}