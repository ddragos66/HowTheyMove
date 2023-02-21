package com.example.howtheymove;

import static com.example.howtheymove.R.id.customListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.howtheymove.databinding.ActivityDefinitiiBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Definitii extends AppCompatActivity {

    ActivityDefinitiiBinding binding;
    DatabaseReference referance;
    String listIcon[] = {"Kinetic Energy", "Potential Energy"};
    int listImage [] = {R.drawable.kineticenergy, R.drawable.potentialenergy};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definitii);

        binding = ActivityDefinitiiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CustomeBaseAdaptor customeBaseAdaptor = new CustomeBaseAdaptor(getApplicationContext(), listIcon, listImage);
        binding.customListView.setAdapter(customeBaseAdaptor);
        binding.customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("CUSTOM_LIST_VIEW", "Ite is clicked @ position  :: " + position);

                    //readData(position);
                    Intent intent = new Intent(Definitii.this, MoreInformations.class);

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

//    private void readData(int position) {
//        referance = FirebaseDatabase.getInstance().getReference("energy");
//        referance.child(String.valueOf(position)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (task.getResult().exists()){
//
//                    Toast.makeText(Definitii.this, "Energy found.", Toast.LENGTH_SHORT).show();
//                    DataSnapshot dataSnapshot = task.getResult();
//                    String title = String.valueOf(dataSnapshot.child("title").getValue());
//
//                }else {
//                    Toast.makeText(Definitii.this, "This energy does not exist!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//}