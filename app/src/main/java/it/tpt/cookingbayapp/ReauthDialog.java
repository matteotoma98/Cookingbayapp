package it.tpt.cookingbayapp;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import it.tpt.cookingbayapp.recipeObject.Recipe;


public class ReauthDialog extends DialogFragment {

    private Button mBtnDelete, mBtnDiscard;
    private TextInputEditText password;
    public static final int LOGIN_REQUEST = 101;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.reauth_dialog, container, false);
        mBtnDiscard= view.findViewById(R.id.btnUndo);
        mBtnDelete= view.findViewById(R.id.btnDeleteAccount);
        password = view.findViewById(R.id.txtReauthPass);
        db = FirebaseFirestore.getInstance();

        mBtnDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String pass = password.getText().toString();
                if(pass.equals("")) {
                    Toast.makeText((Context) getActivity(), R.string.password_required, Toast.LENGTH_SHORT).show();
                } else {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), pass);

                    user.reauthenticate(credential) //Riautentica dunque l'utente
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        final String uid = user.getUid();
                                        db.collection("Recipes")
                                                .whereEqualTo("authorId", uid)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                document.getReference().delete();
                                                            }
                                                            String deletePath = "images/" + uid;
                                                            FirebaseStorage storage = FirebaseStorage.getInstance();
                                                            StorageReference listRef = storage.getReference().child(deletePath);
                                                            deleteFolderElements(listRef);
                                                            db.collection("Users").document(uid).delete();
                                                            user.delete()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                dismiss();
                                                                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                                                                getActivity().startActivityForResult(intent, LOGIN_REQUEST);
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    } else Toast.makeText((Context) getActivity(), R.string.password_wrong, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        return view;
    }


    /**
     * Metodo ricorsivo per eliminare le cartelle di Firebase Storage
     * @param folder
     */
    private void deleteFolderElements(StorageReference folder) {
        folder.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            deleteFolderElements(prefix);
                        }

                        for (StorageReference item : listResult.getItems()) {
                            item.delete();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
    }



}