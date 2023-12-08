package com.trust.home.security.ui.registerFaceId;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.trust.home.security.base.BaseCameraFragment;
import com.trust.home.security.database.entity.User;
import com.trust.home.security.databinding.FragmentRegisterFaceIdBinding;
import com.trust.home.security.ui.main.MainActivity;
import com.trust.home.security.utils.AppPrefsManager;
import com.trust.home.security.utils.CameraManager;
import com.trust.home.security.utils.FacialRecognize;

public class RegisterFaceIdFragment extends
        BaseCameraFragment<FragmentRegisterFaceIdBinding, RegisterFaceIdPresenter, RegisterFaceIdView> implements RegisterFaceIdView, CameraManager.CameraListener {
    private static final long DELAY_TIME = 5000;
    private boolean isFinished = false;
    private final long now = System.currentTimeMillis();

    @Override
    protected FragmentRegisterFaceIdBinding binding(LayoutInflater inflater, ViewGroup container) {
        return FragmentRegisterFaceIdBinding.inflate(inflater, container, false);
    }

    @Override
    protected RegisterFaceIdPresenter getPresenter() {
        return new RegisterFaceIdPresenter();
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void initActions() {
        mBinding.imBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    protected RegisterFaceIdView attachView() {
        return this;
    }

    @Override
    public void onDetectFaceSuccess(Bitmap bitmap) {
        if(isFinished) return;
        if (System.currentTimeMillis() - now > DELAY_TIME) {
            isFinished = true;
            User user = AppPrefsManager.getInstance().getUser();
            FacialRecognize.getInstance(requireContext()).training(1, user.getUserName(), bitmap);
            Toast.makeText(requireContext(), "Training model successful", Toast.LENGTH_SHORT).show();
            pushActivityAndFinish(MainActivity.class);
        }
    }

    @Override
    public void onDetectFaceFailure() {

    }

    @Override
    protected void onPermissionGranted() {
        Toast.makeText(requireContext(), "scan your face within region", Toast.LENGTH_SHORT).show();
        CameraManager.initialize(this, mBinding.camera);
        CameraManager.getInstance().setListener(this);
        CameraManager.getInstance().startCamera();
    }
}
