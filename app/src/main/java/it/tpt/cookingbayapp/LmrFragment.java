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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.tpt.cookingbayapp.personalCardRecycler.PersonalCardRecyclerViewAdapter;
import it.tpt.cookingbayapp.recipeObject.Recipe;

public class LmrFragment extends Fragment {

    String uid;
    private FloatingActionButton btnCrea;
    private RecyclerView recyclerView;
    private PersonalCardRecyclerViewAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private View layout;
    private FirebaseFirestore db;

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
        //Essendo le chiamate a Firebase asincrone c'è il rischio che che l'utente clicchi su questo fragment prima ancora che sia terminato il login
        if (currentUser != null) uid = currentUser.getUid();

        downloadRecipes(); //Scarica le ricette e le assegna al recyclerView

        return view;
    }

    /**
     * Scarica fa Firebase le proprie ricette tramite query whereEqualTo
     * e le inserisce nel RecyclerView
     * Inoltre è presente un eventListener per monitorare i cambiamenti effettuati
     */
    private void downloadRecipes() {
        db.collection("Recipes")
                .whereEqualTo("authorId", uid)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "Listen failed.", e);
                            return;
                        }
                        ArrayList<Recipe> recipeList = new ArrayList<>();
                        ArrayList<String> recipeIds = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Recipe recipe = doc.toObject(Recipe.class);
                            recipeList.add(recipe);
                            recipeIds.add(doc.getId());
                        }
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
                        adapter = new PersonalCardRecyclerViewAdapter(getActivity(), recipeList, recipeIds);
                        recyclerView.setAdapter(adapter);
                        Log.i("FinishLMR", "Recipes downloaded");
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Bottone per creare una ricetta
        btnCrea = (FloatingActionButton) getView().findViewById(R.id.floating_action_button);
        btnCrea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (!user.isAnonymous()) {
                    Intent i = new Intent(getActivity(), CreateRecipe.class);
                    startActivity(i);
                } else Snackbar.make(layout, R.string.anonymous, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}
