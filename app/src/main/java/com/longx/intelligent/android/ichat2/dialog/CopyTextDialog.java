package com.longx.intelligent.android.ichat2.dialog;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.longx.intelligent.android.ichat2.databinding.DialogCopyTextBinding;

/**
 * Created by LONG on 2024/5/29 at 7:11 PM.
 */
public class CopyTextDialog extends AbstractDialog{
    private String text;
    public CopyTextDialog(AppCompatActivity activity, String text) {
        super(activity);
        this.text = text;
    }

    @Override
    protected AlertDialog create(MaterialAlertDialogBuilder builder) {
        return builder.create();
    }

    @Override
    protected View createView(LayoutInflater layoutInflater) {
        DialogCopyTextBinding binding = DialogCopyTextBinding.inflate(getActivity().getLayoutInflater());
        binding.editText.setText(text);
        return binding.getRoot();
    }
}
