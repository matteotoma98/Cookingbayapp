package it.tpt.cookingbayapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import it.tpt.cookingbayapp.commentRecycler.CommentRecyclerAdapter;
import it.tpt.cookingbayapp.recipeObject.Comment;


public class ComFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private CommentRecyclerAdapter mAdapter;
    private FloatingActionButton mFloatingComment;
    private View layout;
    private String recipeId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_com, container, false);
        layout = view.findViewById(R.id.commentCoordinatorLayout);//Layout per lo SnackBar

        //Ottieni i commenti e l'Id della ricetta passati dall'activity ViewRecipe
        Bundle commentBundle = this.getArguments();
        ArrayList<Comment> comments = (ArrayList) commentBundle.getSerializable("comments");
        recipeId = commentBundle.getString("recipeId");
        String authorId = commentBundle.getString("authorId");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = view.findViewById(R.id.comRecycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        boolean isOwnRecipe = userId.equals(authorId); //Verifica che la ricetta è personale
        mAdapter = new CommentRecyclerAdapter(comments, getContext(), recipeId, userId, isOwnRecipe);
        mRecyclerView.setAdapter(mAdapter);

        mFloatingComment = view.findViewById(R.id.comNewCommentBtn);

        mFloatingComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (!user.isAnonymous()) {
                    ComDialog commentDialog = new ComDialog();
                    commentDialog.setmAdapter(mAdapter); //Per passare l'adapter ed aggiungere il commento in locale
                    commentDialog.setRecipeId(recipeId); //L'id della ricetta per aggiungere il commento su Firestore
                    commentDialog.show(getChildFragmentManager(), "custom");
                } else Snackbar.make(layout, R.string.anonymous, Snackbar.LENGTH_LONG).show();
            }
        });
        return view;
    }
}
