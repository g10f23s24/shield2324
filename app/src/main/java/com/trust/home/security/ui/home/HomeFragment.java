package com.trust.home.security.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.trust.home.security.base.BaseFragment;
import com.trust.home.security.databinding.FragmentHomeBinding;
import com.trust.home.security.etx.CollectEtxKt;
import com.trust.home.security.network.data.response.HomeData;
import com.trust.home.security.ui.profile.ProfileFragment;
import com.trust.home.security.widgets.UserToolBar;

import kotlin.Unit;

public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomePresenter, HomeView> implements HomeView {
    private HomeAdapter mAdapter;

    @Override
    protected FragmentHomeBinding binding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }

    @Override
    protected HomePresenter getPresenter() {
        return new HomePresenter();
    }

    @Override
    protected HomeView attachView() {
        return this;
    }

    @Override
    protected void initViews() {
        mAdapter = new HomeAdapter();
        mBinding.rvHomes.setAdapter(mAdapter);
        mAdapter.setData(
                new HomeData("Marvel House"),
                new HomeData("Add a new house")
        );
        CollectEtxKt.collectNotNull(this, mPresenter.userFlow(), user -> {
            mBinding.toolbar.updateAvatar(user.getAvatar());
            return Unit.INSTANCE;
        });
    }

    @Override
    protected void initActions() {
        mBinding.toolbar.setListener(new UserToolBar.ToolBarListener() {
            @Override
            public void onOpenProfile() {
                pushFragment(ProfileFragment.newInstance(ProfileFragment.class));
            }
        });
    }
}
