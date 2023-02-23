package com.example.howtheymove;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.howtheymove.databinding.ActivityMainBinding;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

public class MainActivity extends AppCompatActivity {
    public static final int CAMERA_REQUEST_CODE = 102;
    ActivityMainBinding binding;
    private boolean mUserRequestedInstall = true;
    private int CAMERA_CODE = 1;
    //private Object session = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //start OF ANIMATED BACKGROUND
        AnimationDrawable animationDrawable = (AnimationDrawable) binding.rootLayoutAnimation.getBackground();;
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
        //END OF ANIMATED BACKGROUND

        maybeEnableArButton();

        binding.arbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                } else {
                    requestCameraPermission();
                }

            }
        });

        binding.deffbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openEnergyList();}
        });
    }

    public void openEnergyList(){
        Intent intent = new Intent(this, EnergyList.class);
        startActivity(intent);
    }


    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission Nedded")
                    .setMessage("This permision is needed for ar")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss(); //AICI ERA dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRATED", Toast.LENGTH_SHORT).show();
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void maybeEnableArButton() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            // Continue to query availability at 5Hz while compatibility is checked in the background.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            binding.arbtn.setVisibility(View.VISIBLE);
            binding.arbtn.setEnabled(true);
            Log.d("Success: ", "Ar supported");
        } else { // The device is unsupported or unknown.
            binding.arbtn.setVisibility(View.INVISIBLE);
            binding.arbtn.setEnabled(false);
            Log.d("Success: ", "Ar not supported");
        }
    }

//    public void createSession() throws UnavailableDeviceNotCompatibleException, UnavailableSdkTooOldException, UnavailableArcoreNotInstalledException, UnavailableApkTooOldException {
//        // Create a new ARCore session.
//        Session session;
//        session = new Session(this);s
//        Log.d("DEBUG------->", "NEW SESION STARTED");
//
//        // Create a session config.
//        Config config = new Config((Session) session);
//
//
//        // Do feature-specific operations here, such as enabling depth or turning on
//        // support for Augmented Faces.
//
//        config.setInstantPlacementMode(Config.InstantPlacementMode.LOCAL_Y_UP);
//
//        // Configure the session.
//        session.configure(config);
//    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("DEBUG------->", "1");
        // Ensure that Google Play Services for AR and ARCore device profile data are
        // installed and up to date.
        try {
            Object mSession = null;
            if (mSession == null) {
                switch (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                    case INSTALLED:
                        // Success: Safe to create the AR session.
                        //createSession();
                        Log.d("DEBUG------->", "AR INSTALLED");
                        break;
                    case INSTALL_REQUESTED:
                        // When this method returns `INSTALL_REQUESTED`:
                        // 1. ARCore pauses this activity.
                        // 2. ARCore prompts the user to install or update Google Play
                        //    Services for AR (market://details?id=com.google.ar.core).
                        // 3. ARCore downloads the latest device profile data.
                        // 4. ARCore resumes this activity. The next invocation of
                        //    requestInstall() will either return `INSTALLED` or throw an
                        //    exception if the installation or update did not succeed.
                        mUserRequestedInstall = false;
                        Log.d("DEBUG------->", "AR NOT INSTALLED");
                        return;
                }
            }
        } catch (UnavailableUserDeclinedInstallationException e) {
            // Display an appropriate message to the user and return gracefully.
            Toast.makeText(this, "TODO: handle exception " + e, Toast.LENGTH_LONG)
                    .show();
            return;
        } catch (UnavailableDeviceNotCompatibleException e) {
            throw new RuntimeException(e);
        }


    }



}