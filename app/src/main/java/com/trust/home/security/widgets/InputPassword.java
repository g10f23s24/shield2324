package com.trust.home.security.widgets;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.trust.home.security.R;
import com.trust.home.security.base.BaseCustomView;
import com.trust.home.security.databinding.LayoutInputPasswordBinding;

public class InputPassword extends BaseCustomView<LayoutInputPasswordBinding> {
    private boolean isShow = false;

    public InputPassword(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected LayoutInputPasswordBinding getBinding(LayoutInflater inflater, ViewGroup viewGroup) {
        return LayoutInputPasswordBinding.inflate(inflater, viewGroup, true);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs) {
        displayPassword(isShow);
    }

    @Override
    protected void initActions() {
        mBinding.imEye.setOnClickListener(v -> {
            isShow = !isShow;
            displayPassword(isShow);
        });
    }

    private void displayPassword(boolean isShow) {
        if(isShow) {
            mBinding.imEye.setImageResource(R.drawable.ic_eye_op);
            mBinding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            mBinding.imEye.setImageResource(R.drawable.ic_eye_cls);
            mBinding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        String password = getPassword();
        if(password != null) {
            mBinding.edtPassword.setSelection(password.length());
        }
    }

    public String getPassword() {
        Editable editable = mBinding.edtPassword.getText();
        if(editable != null) {
            return editable.toString().trim();
        }

        return "";
    }
}
