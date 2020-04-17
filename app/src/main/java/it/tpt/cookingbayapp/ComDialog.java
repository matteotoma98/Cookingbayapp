package it.tpt.cookingbayapp;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import it.tpt.cookingbayapp.CommentRecycler.CommentRecyclerAdapter;
import it.tpt.cookingbayapp.recipeObject.Comment;

public class ComDialog extends DialogFragment {

    private Button mBtnPublish, mBtnDiscard;
    private CommentRecyclerAdapter mAdapter;
    private TextInputEditText mTxtInsertComment;
    private String recipeId;

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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final Comment comment = new Comment(user.getUid(), user.getDisplayName(), getmTxtInsertComment(), user.getPhotoUrl().toString());
                if(!user.isAnonymous() && !getmTxtInsertComment().equals("")) { //Se non è anonimo
                    if(!getmTxtInsertComment().equals("")) { //Se il testo non è vuoto
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Recipes").document(recipeId)
                                .update("comments", FieldValue.arrayUnion(comment))
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //Aggiungi in locale il nuovo commento al RecyclerView, viene effettivamente caricato su fireStore in ComDialog.java
                                        mAdapter.addComment(comment);
                                    }
                                });
                        getDialog().dismiss();
                    } else Toast.makeText(getContext(), "EMPTY", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public void setmAdapter(CommentRecyclerAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public String getmTxtInsertComment() {
        return mTxtInsertComment.getText().toString().trim();
    }

}
