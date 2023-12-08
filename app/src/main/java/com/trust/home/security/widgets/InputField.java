package com.trust.home.security.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.trust.home.security.R;
import com.trust.home.security.base.BaseCustomView;
import com.trust.home.security.databinding.LayoutInputFieldBinding;

public class InputField extends BaseCustomView<LayoutInputFieldBinding> {
    public InputField(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected LayoutInputFieldBinding getBinding(LayoutInflater inflater, ViewGroup viewGroup) {
        return LayoutInputFieldBinding.inflate(inflater, viewGroup, true);
    }

    @Override
    protected void initViews(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.InputField);
        String title = typedArray.getString(R.styleable.InputField_if_title);
        String hint = typedArray.getString(R.styleable.InputField_if_hint);
        int maxLength = typedArray.getInteger(R.styleable.InputField_if_maxLength, Integer.MAX_VALUE);
        int inputType = typedArray.getInteger(R.styleable.InputField_if_inputType, 0);
        typedArray.recycle();
        mBinding.tvTitle.setText(title);
        mBinding.edtInput.setHint(hint);

        mBinding.edtInput.setInputType(
                inputType == 0 ? InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_NUMBER
        );

        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(maxLength);
        mBinding.edtInput.setFilters(filterArray);
    }

    public void setText(String text) {
        mBinding.edtInput.setText(text);
    }

    public String getText() {
        return mBinding.edtInput.getText().toString().trim();
    }

    @Override
    protected void initActions() {

    }
}
