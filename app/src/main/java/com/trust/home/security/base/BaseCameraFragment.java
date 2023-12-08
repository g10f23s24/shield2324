package com.trust.home.security.base;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;

public abstract class BaseCameraFragment<BD extends ViewBinding, P extends BasePresenter<V>, V extends BaseView>
        extends BaseFragment<BD, P, V> {
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    protected abstract void onPermissionGranted();
    private final ActivityResultLauncher<String> requestPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), (ActivityResultCallback<Boolean>) o -> {
                if(o) onPermissionGranted();
                else {
                    onBackPressed();
                    showToast("This feature requires CAMERA permission");
                }
            });

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!isPermissionGranted()) {
            requestPermission.launch(PERMISSION_CAMERA);
        } else onPermissionGranted();
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                requireContext(),
                PERMISSION_CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }
}
