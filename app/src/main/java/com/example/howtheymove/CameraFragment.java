package com.example.howtheymove;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.hardware.camera2.CameraManager;


import com.example.howtheymove.databinding.ActivityMainBinding;

import java.util.Arrays;

public class CameraFragment extends Fragment {

    private TextureView cameraTextureView;
    private CameraDevice cameraDevice;
    int cameraId = 0;
    private Size imageDimension;
    private CameraCaptureSession CameraCaptureSession;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        cameraTextureView = view.findViewById(R.id.cameraTextureView);
        //CameraInitialized();
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            startCamera();//Different
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
    }

//    private void CameraInitialized(){
//        //Camera initialization is done here:
//        @SuppressLint("ServiceCast") CameraManager cm = (CameraManager) requireActivity().getSystemService(Context.CAMERA_SERVICE);
//        //String cameraId = 1;
//        try {
//            cameraId = cm.getCameraIdList()[0];//Front and back cameras are selected here
//        }catch (CameraAccessException e){
//            e.printStackTrace();
//        }
//        //Other camera related stuff is done here
//    }

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
