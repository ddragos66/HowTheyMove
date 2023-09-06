package com.example.howtheymove;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Arrays;
import java.util.List;

public class CameraFragment extends Fragment {

    private FrameLayout cameraFrameLayout;
    private TextureView cameraTextureView;
    private CameraDevice cameraDevice;
    int cameraId = 0;
    private Size imageDimension;
    private CameraCaptureSession CameraCaptureSession;
    private Session arSession;
    private GestureDetector gestureDetector;
    private ArFragment arFragment;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraTextureView = rootView.findViewById(R.id.cameraTextureView);



        startArSession();
        onResume();

        arFragment = (ArFragment) getChildFragmentManager().findFragmentById(R.id.arFragment);
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("Debuggs", "onSingleTapConfirmed");
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d("Debuggs", "on single tap"); // Debug level
                handleTap(e);
                return true;
            }
        });


        cameraTextureView.setOnTouchListener((v, event) -> {
            Log.d("Debuggs", "onTouch");
            return gestureDetector.onTouchEvent(event);
        });

        return rootView;
    }


    private void startArSession() {
        try {
            arSession = new Session(requireContext());
            Config config = new Config(arSession);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            arSession.configure(config);
            Log.d("Debuggs", "Session started"); // Debug level

        } catch (UnavailableException e) {
            Log.d("Debuggs", "Session failed"); // Debug level
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            startCamera();
            Log.d("Debuggs", "Session is running"); // Debug level
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.binding.deffbtn.setEnabled(false);
                mainActivity.binding.arbtn.setEnabled(false);
                Log.d("Debuggs", "buttons deactivated"); // Debug level
            }

            if (arSession == null) {
                startArSession();
                Log.d("Debuggs", "Session starts again?!"); // Debug level
            }

        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Enable buttons
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.binding.deffbtn.setEnabled(true);
            mainActivity.binding.arbtn.setEnabled(true);
            Log.d("Debuggs", "buttons activated"); // Debug level
        }

        if (arSession != null) {
            arSession.pause();
        }

    }

    private void handleTap(MotionEvent tap) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame != null) {
            List<HitResult> hitResults = frame.hitTest(tap);
            if(!hitResults.isEmpty()){
                Log.d("Debuggs", "valit hit received"); // Debug level

                Log.d("Debuggs", "Hit result:" + hitResults); // Debug level
                for (HitResult hitResult : hitResults) {
                    Log.d("Debuggs", "Hit result:" + hitResult); // Debug level
                    Log.d("Debuggs", "Hit results" + hitResults); // Debug level
                    AnchorNode anchorNode = new AnchorNode(hitResult.createAnchor());
                    anchorNode.setParent(arFragment.getArSceneView().getScene());
                    ModelRenderable.builder()
                            .setSource(getContext(), R.raw.model) // 3D model resource
                            .build()
                            .thenAccept(modelRenderable -> {
                                anchorNode.setRenderable(modelRenderable);
                            });
                }
            }else {
                Log.d("Debuggs", "No hits"); // Debug level
            }
        }else {
            Log.d("Debuggs", "Frame is null"); // Debug level
        }

    }



    @SuppressLint("MissingPermission")
    private void startCamera() throws CameraAccessException {
        @SuppressLint("ServiceCast") CameraManager cm = (CameraManager) requireActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            cm.openCamera(String.valueOf(cameraId), new CameraDevice.StateCallback(){

                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    CameraPreviewSession();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    cameraDevice.close();
                    cameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    cameraDevice.close();
                    cameraDevice = null;
                }
            }, null);
        } catch (CameraAccessException e){
            e.printStackTrace();
        }

        CameraCharacteristics characteristics = cm.getCameraCharacteristics(String.valueOf(cameraId));
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        imageDimension = map.getOutputSizes(SurfaceTexture.class)[0]; // Initialize imageDimension


    }

    private void CameraPreviewSession() {
        // Create a camera capture session and set the TextureView as a target for the camera preview
        try {
            SurfaceTexture texture = cameraTextureView.getSurfaceTexture();
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);

            final CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);

            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (cameraDevice == null) {
                        return;
                    }

                    CameraCaptureSession = session;
                    try {
                        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        CameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    // Handle configuration failure
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


}
