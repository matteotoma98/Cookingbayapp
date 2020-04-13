package it.tpt.cookingbayapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.tpt.cookingbayapp.personalCardRecycler.PersonalCardRecyclerViewAdapter;
import it.tpt.cookingbayapp.recipeObject.Recipe;

public class LmrFragment extends Fragment {

    String uid;
    private FloatingActionButton btnCrea;
    private RecyclerView recyclerView;
    PersonalCardRecyclerViewAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private View layout;
    FirebaseFirestore db;
    final static int CREATE_REQUEST = 129;

    public LmrFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lmr, container, false);

        recyclerView = view.findViewById(R.id.myCardRecycler_view);
        recyclerView.setHasFixedSize(true);
        layout = view.findViewById(R.id.lmrCoordinatorLayout);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        uid = currentUser.getUid();

        downloadRecipes();

        return view;
    }

    private void downloadRecipes() {
        db.collection("Recipes")
                .whereEqualTo("authorId", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Recipe> recipeList = new ArrayList<>();
                            ArrayList<String> recipeIds = new ArrayList<>();
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                Recipe recipe = document.toObject(Recipe.class);
                                recipeIds.add(document.getId());
                                recipeList.add(recipe);
                            }
                            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
                            adapter = new PersonalCardRecyclerViewAdapter(getActivity(), recipeList, recipeIds);
                            recyclerView.setAdapter(adapter);
                            Log.i("Redownload", "Recipes downloaded");
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCrea = (FloatingActionButton) getView().findViewById(R.id.floating_action_button);
        btnCrea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!currentUser.isAnonymous()) {
                    Intent i = new Intent(getActivity(), CreateRecipe.class);
                    startActivityForResult(i, CREATE_REQUEST);
                } else Snackbar.make(layout, R.string.anonymous, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_REQUEST) {
            if(resultCode == getActivity().RESULT_OK) {
                downloadRecipes();
            }
        }
    }

}
