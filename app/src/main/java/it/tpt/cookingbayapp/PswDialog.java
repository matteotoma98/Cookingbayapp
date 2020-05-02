package it.tpt.cookingbayapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

public class PswDialog extends DialogFragment {

    private Button mBtnDiscardPsw, mBtnConfirmPsw;
    private TextInputEditText mOldPsw, mNewPsw, mConfirmPsw;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.psw_dialog, container, false);
            mBtnDiscardPsw= view.findViewById(R.id.btnDiscardPswChange);
            mBtnConfirmPsw= view.findViewById(R.id.btnConfirmPswChange);
            mOldPsw= view.findViewById(R.id.txt_psw_old);
            mNewPsw= view.findViewById(R.id.txt_psw_new);
            mConfirmPsw= view.findViewById(R.id.txt_psw_newConfirm);

        mBtnDiscardPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        mBtnConfirmPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });



       return view;
    }
}
