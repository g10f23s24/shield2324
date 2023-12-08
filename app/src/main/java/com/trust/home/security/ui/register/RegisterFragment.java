package com.trust.home.security.ui.register;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.trust.home.security.base.BaseFragment;
import com.trust.home.security.databinding.FragmentRegisterBinding;
import com.trust.home.security.ui.registerFaceId.RegisterFaceIdFragment;
import com.trust.home.security.utils.StringUtils;

public class RegisterFragment extends BaseFragment<FragmentRegisterBinding, RegisterPresenter, RegisterView> implements RegisterView {

    @Override
    protected FragmentRegisterBinding binding(LayoutInflater inflater, ViewGroup container) {
        return FragmentRegisterBinding.inflate(inflater, container, false);
    }

    @Override
    protected RegisterPresenter getPresenter() {
        return new RegisterPresenter();
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void initActions() {
        mBinding.tvCreateAccount.setOnClickListener(v -> {
            String userName = mBinding.edtUserName.getText().toString().trim();
            String password = mBinding.edtPassword.getPassword();
            if(StringUtils.valid(userName) && StringUtils.valid(password)) {
                mPresenter.createUser(userName, password);
            } else {
                Toast.makeText(requireContext(), "You need to enter enough information", Toast.LENGTH_SHORT).show();
            }
        });

        mBinding.appToolbar.setStartIconListener(this::onBackPressed);
    }

    @Override
    protected RegisterView attachView() {
        return this;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onRegisterSuccess() {
        hideKeyboard();
        pushFragment(RegisterFaceIdFragment.newInstance(RegisterFaceIdFragment.class));
    }

    @Override
    public void onRegisterFailure(String message) {
        showToast(message);
    }
}
