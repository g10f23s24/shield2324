package com.trust.home.security.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraManager {
    private static CameraManager INSTANCE;
    private WeakReference<Fragment> fragment;
    private PreviewView previewView;
    private CameraSelector cameraSelector;
    private ProcessCameraProvider cameraProvider;
    private int lensFacing = CameraSelector.LENS_FACING_FRONT;
    private Preview previewUseCase;
    private ImageAnalysis analysisUseCase;
    private boolean flipX = false;
    private CameraListener listener;

    public void setListener(CameraListener listener) {
        this.listener = listener;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = new WeakReference<>(fragment);
    }

    public void setPreviewView(PreviewView previewView) {
        this.previewView = previewView;
    }

    public static synchronized CameraManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CameraManager();
        }
        return INSTANCE;
    }

    public static synchronized void initialize(Fragment fragment, PreviewView previewView) {
        INSTANCE = new CameraManager();
        INSTANCE.setPreviewView(previewView);
        INSTANCE.setFragment(fragment);
    }

    /**
     * Face detection processor
     */
    @SuppressLint("UnsafeOptInUsageError")
    private void analyze(@NonNull ImageProxy image) {
        if (image.getImage() == null) return;

        InputImage inputImage = InputImage.fromMediaImage(
                image.getImage(),
                image.getImageInfo().getRotationDegrees()
        );

        FaceDetector faceDetector = FaceDetection.getClient();

        faceDetector.process(inputImage)
                .addOnSuccessListener(faces -> onSuccessListener(faces, inputImage))
                .addOnFailureListener(e -> Log.e("TAG", "Barcode process failure", e))
                .addOnCompleteListener(task -> image.close());
    }

    private void onSuccessListener(List<Face> faces, InputImage inputImage) {
        if (faces.size() > 0) {
            Face face = faces.get(0);

            Rect boundingBox = face.getBoundingBox();

            // convert img to bitmap & crop img
            Bitmap bitmap = BitmapUtils.mediaImgToBmp(
                    inputImage.getMediaImage(),
                    inputImage.getRotationDegrees(),
                    boundingBox,
                    flipX
            );

            if (listener != null) {
                listener.onDetectFaceSuccess(bitmap);
            }
        } else if (listener != null) {
            listener.onDetectFaceFailure();
        }
    }


    private void setupCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(getContext());

        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindAllCameraUseCases();
            } catch (ExecutionException | InterruptedException e) {
                Log.e("TAG", "cameraProviderFuture.addListener Error", e);
            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            bindPreviewUseCase();
            bindAnalysisUseCase();
        }
    }

    private void bindPreviewUseCase() {
        if (cameraProvider == null) {
            return;
        }

        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        Preview.Builder builder = new Preview.Builder();
        builder.setTargetAspectRatio(AspectRatio.RATIO_4_3);

        try {
            builder.setTargetRotation(getRotation());

            previewUseCase = builder.build();
            previewUseCase.setSurfaceProvider(previewView.getSurfaceProvider());
            cameraProvider.bindToLifecycle(getLifecycleOwner(), cameraSelector, previewUseCase);
        } catch (Exception e) {
            Log.e("TAG", "Error when bind preview", e);
        }
    }

    private void bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return;
        }

        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }

        Executor cameraExecutor = Executors.newSingleThreadExecutor();

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        builder.setTargetAspectRatio(AspectRatio.RATIO_4_3);

        try {
            builder.setTargetRotation(getRotation());

            analysisUseCase = builder.build();
            analysisUseCase.setAnalyzer(cameraExecutor, this::analyze);
            cameraProvider.bindToLifecycle(getLifecycleOwner(), cameraSelector, analysisUseCase);
        } catch (Exception e) {
            Log.e("TAG", "Error when bind analysis", e);
        }
    }

    protected int getRotation() throws NullPointerException {
        return previewView.getDisplay().getRotation();
    }

    public void switchCamera() {
        if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            lensFacing = CameraSelector.LENS_FACING_FRONT;
            flipX = true;
        } else {
            lensFacing = CameraSelector.LENS_FACING_BACK;
            flipX = false;
        }

        if (cameraProvider != null) cameraProvider.unbindAll();
        startCamera();
    }

    private Context getContext() {
        return fragment.get().getContext();
    }

    private LifecycleOwner getLifecycleOwner() {
        return fragment.get();
    }

    /**
     * Setup camera & use cases
     */
    public void startCamera() {
        setupCamera();
    }

    public interface CameraListener {
        void onDetectFaceSuccess(Bitmap bitmap);

        void onDetectFaceFailure();
    }
}
