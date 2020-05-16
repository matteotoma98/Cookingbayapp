package it.tpt.cookingbayapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PswDialog extends DialogFragment {

    private Button mBtnDiscardPsw, mBtnConfirmPsw;
    private TextInputEditText mOldPsw, mNewPsw, mConfirmPsw;
    private String oldPass, newPass, confPass;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.psw_dialog, container, false);
        mBtnDiscardPsw = view.findViewById(R.id.btnDiscardPswChange);
        mBtnConfirmPsw = view.findViewById(R.id.btnConfirmPswChange);
        mOldPsw = view.findViewById(R.id.txt_psw_old);
        mNewPsw = view.findViewById(R.id.txt_psw_new);
        mConfirmPsw = view.findViewById(R.id.txt_psw_newConfirm);

        mBtnDiscardPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        mBtnConfirmPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPass = mOldPsw.getText().toString();
                newPass = mNewPsw.getText().toString();
                confPass = mConfirmPsw.getText().toString();
                if (oldPass.equals("")) {
                    Toast.makeText((Context) getActivity(), R.string.password_old_required, Toast.LENGTH_SHORT).show();
                } else {
                    if (newPass.length() < 6) {
                        Toast.makeText((Context) getActivity(), R.string.password_length, Toast.LENGTH_SHORT).show();
                    } else {
                        if (!newPass.equals(confPass)) {
                            Toast.makeText((Context) getActivity(), R.string.password_conf, Toast.LENGTH_SHORT).show();
                        } else {
                            //Per cambiare password bisogna prima riautenticarsi
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(user.getEmail(), oldPass);

                            user.reauthenticate(credential) //Riautentica dunque l'utente
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                user.updatePassword(newPass) //Aggiorna la password
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText((Context) getActivity(), R.string.password_done, Toast.LENGTH_SHORT).show();
                                                                    dismiss();
                                                                }
                                                            }
                                                        });
                                            } else
                                                Toast.makeText((Context) getActivity(), R.string.password_wrong, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }
            }
        });


        return view;
    }
}
