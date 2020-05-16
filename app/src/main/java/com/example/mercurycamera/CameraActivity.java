package com.example.mercurycamera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.internal.ImageCaptureConfigProvider;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.ImageCaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        previewView = findViewById(R.id.preview_view);

        checkCameraPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(allPermissionsGranted()){
            previewView.post(this::startCamera);
        }
    }

    private void checkCameraPermission() {
        if (allPermissionsGranted()) {
            previewView.post(this::startCamera);
        }
        else{
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.CAMERA,
                                  Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        }
    }

    private Boolean allPermissionsGranted(){
        return (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        ImageCapture imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build();

        ImageButton shutterButton = findViewById(R.id.shutter_button);
        shutterButton.setOnClickListener();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

        preview.setSurfaceProvider(previewView.createSurfaceProvider(camera.getCameraInfo()));
    }
}
