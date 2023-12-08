package com.trust.home.security.ui.profile;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.trust.home.security.base.BaseFragment;
import com.trust.home.security.databinding.FragmentProfileViewBinding;
import com.trust.home.security.etx.CollectEtxKt;
import com.trust.home.security.network.data.response.HomeData;
import com.trust.home.security.ui.editProfile.EditProfileFragment;
import com.trust.home.security.ui.login.LoginActivity;
import com.trust.home.security.utils.Gender;
import com.trust.home.security.utils.GlideUtils;
import com.trust.home.security.utils.StringUtils;

import kotlin.Unit;

public class ProfileFragment
        extends BaseFragment<FragmentProfileViewBinding, ProfilePresenter, ProfileView>
        implements ProfileView {
    @Override
    protected FragmentProfileViewBinding binding(LayoutInflater inflater, ViewGroup container) {
        return FragmentProfileViewBinding.inflate(inflater, container, false);
    }

    @Override
    protected ProfilePresenter getPresenter() {
        return new ProfilePresenter();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initViews() {
        CollectEtxKt.collectNotNull(this, mPresenter.userFlow(), user -> {
            String fullName = user.getFullName();
            Gender gender = Gender.getGender(user.getGender());
            String age = user.getAge();
            mBinding.tvName.setText(StringUtils.valid(fullName) ? fullName : user.getUserName());
            mBinding.tvInfo.setText(gender.getText() + ", " + age);
            if(StringUtils.valid(user.getAvatar())) {
                GlideUtils.loadAvatar(mBinding.imAvatar, user.getAvatar());
            }
            return Unit.INSTANCE;
        });
    }

    @Override
    protected void initActions() {
        mBinding.btnLogout.setOnClickListener(v -> {
            mPresenter.logout();
            pushActivityAndFinish(LoginActivity.class);
        });

        mBinding.toolbar.setStartIconListener(this::onBackPressed);
        mBinding.toolbar.setEndIconListener(this::goToEditScreen);
        ProfileAdapter adapter = new ProfileAdapter();
        mBinding.rvHome.setAdapter(adapter);
        adapter.setData(new HomeData("Marvel House", "09:41:16 AM", "14130 Minnesota Ave, Bonner Springs, KS 66012, USA"));
    }

    private void goToEditScreen() {
       // pushFragment(EditProfileFragment.newInstance(EditProfileFragment.class));
    }

    @Override
    protected ProfileView attachView() {
        return this;
    }
}
