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

public class ComDialog extends DialogFragment {

    private Button mBtnPublish, mBtnDiscard;
    private TextInputEditText mTxtInsertComment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.com_dialog, container, false);
        mBtnDiscard= view.findViewById(R.id.btnDiscardComment);
        mBtnPublish= view.findViewById(R.id.btnPublishComment);
        mTxtInsertComment= view.findViewById(R.id.txtInsertComment);


        mBtnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("discard", "Discard Pressed");
                getDialog().dismiss();
            }
        });

        mBtnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("publish", "Publish Pressed");
                getDialog().dismiss();
            }
        });

        return view;
    }
}
