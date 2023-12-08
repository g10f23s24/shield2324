package com.trust.home.security.widgets;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.trust.home.security.base.BaseDialogFragment;
import com.trust.home.security.databinding.LayoutNoticeDialogBinding;

public class DialogMessage extends BaseDialogFragment<LayoutNoticeDialogBinding> {
    private final DialogMessageListener listener;
    private final String message;

    public DialogMessage(@NonNull Context context, DialogMessageListener listener, String message) {
        super(context);
        this.listener = listener;
        this.message = message;
    }

    @Override
    protected LayoutNoticeDialogBinding getBinding(LayoutInflater inflater) {
        return LayoutNoticeDialogBinding.inflate(inflater);
    }

    @Override
    protected void initViews() {
        mBinding.tvMessage.setText(message);
        mBinding.tvClose.setOnClickListener(v -> {
            if(listener != null) {
                listener.onClose();
            }
            dismiss();
        });
    }

    public interface DialogMessageListener {
        void onClose();
    }
}
