package it.tpt.cookingbayapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import it.tpt.cookingbayapp.CommentRecycler.CommentRecyclerAdapter;
import it.tpt.cookingbayapp.recipeObject.Comment;


public class ComFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private CommentRecyclerAdapter mAdapter;
    private FloatingActionButton mFloatingComment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_com, container, false);

        //Ottieni i commenti passati dall'activity ViewRecipe
        Bundle commentBundle = this.getArguments();
        ArrayList<Comment> comments = (ArrayList) commentBundle.getSerializable("comments");
        mRecyclerView = view.findViewById(R.id.comRecycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CommentRecyclerAdapter(comments, getContext());
        mRecyclerView.setAdapter(mAdapter);

        mFloatingComment = view.findViewById(R.id.comNewCommentBtn);

        mFloatingComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComDialog commentDialog = new ComDialog();
                commentDialog.show(getChildFragmentManager(),"custom");
            }
        });
        return view;
    }
}
