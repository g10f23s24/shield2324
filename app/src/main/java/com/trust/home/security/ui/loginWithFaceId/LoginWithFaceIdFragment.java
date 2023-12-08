package com.trust.home.security.ui.loginWithFaceId;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.trust.home.security.R;
import com.trust.home.security.base.BaseCameraFragment;
import com.trust.home.security.databinding.FragmentLoginWithFaceIdBinding;
import com.trust.home.security.ui.main.MainActivity;
import com.trust.home.security.utils.AppPrefsManager;
import com.trust.home.security.utils.CameraManager;
import com.trust.home.security.utils.FacialRecognize;

public class LoginWithFaceIdFragment
        extends BaseCameraFragment<FragmentLoginWithFaceIdBinding, LoginWithFaceIdPresenter, LoginWithFaceIdView>
        implements LoginWithFaceIdView, CameraManager.CameraListener, FacialRecognize.RecognitionListener {
    private boolean isFinished = false;

    @Override
    protected FragmentLoginWithFaceIdBinding binding(LayoutInflater inflater, ViewGroup container) {
        return FragmentLoginWithFaceIdBinding.inflate(inflater, container, false);
    }

    @Override
    protected LoginWithFaceIdPresenter getPresenter() {
        return new LoginWithFaceIdPresenter();
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void initActions() {
        mBinding.btnLogout.setOnClickListener(v -> {
            mPresenter.logout();
            onBackPressed();
        });
    }

    @Override
    protected LoginWithFaceIdView attachView() {
        return null;
    }

    @Override
    public void onDetectFaceSuccess(Bitmap bitmap) {
        FacialRecognize.getInstance(baseActivity).recognize(
                AppPrefsManager.getInstance().getUser().getUserName(),
                bitmap,
                this
        );
    }

    @Override
    public void onDetectFaceFailure() {

    }

    @Override
    public void recognizeSuccess() {
        if(isFinished) return;
        isFinished = true;
        mBinding.icPin.setImageResource(R.drawable.ic_asset_unlock);
        Toast.makeText(requireContext(), "Login success", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            pushActivityAndFinish(MainActivity.class);
        }, 600);
    }

    @Override
    public void recognizeFailure() {

    }

    @Override
    public void userHasNotRegistration() {

    }

    @Override
    protected void onPermissionGranted() {
        CameraManager.initialize(this, mBinding.previewCamera);
        CameraManager.getInstance().setListener(this);
        CameraManager.getInstance().startCamera();
    }
}
