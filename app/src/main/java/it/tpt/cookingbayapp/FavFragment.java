package it.tpt.cookingbayapp;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import it.tpt.cookingbayapp.cardRecycler.RecipeCardRecyclerViewAdapter;
import it.tpt.cookingbayapp.recipeObject.Recipe;

public class FavFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeCardRecyclerViewAdapter mAdapter;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.fragment_fav, container, false);
        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.myfavRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        mAdapter = new RecipeCardRecyclerViewAdapter(getActivity(), new ArrayList<Recipe>(), new ArrayList<String>());
        recyclerView.setAdapter(mAdapter);

        db = FirebaseFirestore.getInstance();
        downloadRecipes(); //Scarica le ricette

        return view;
    }

    /**
     * Scarica le ricette preferite (tramite un listener di cambiamenti in tempo reale)
     * Prima però ottiene la lista degli id dal documento personale dell'utente
     */
    private void downloadRecipes() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("Users").document(user.getUid()) //Documento dell'utente
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                ArrayList<String> favourites = (ArrayList<String>) document.get("favourites"); //Ottiene la lista delle ricette preferie (Id)
                                for(String id : favourites) { //Ottieni uno ad uno tali ricette e controlla i cambiamenti in tempo reale
                                    db.collection("Recipes").document(id)
                                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                    if (e != null) {
                                                        Log.w("TAG", "Listen failed.", e);
                                                        return;
                                                    }
                                                    String docId = documentSnapshot.getId();
                                                    if(!mAdapter.getRecipeIds().contains(docId)) mAdapter.addRecipe(documentSnapshot.toObject(Recipe.class), docId);
                                                    else mAdapter.updateRecipe(documentSnapshot.toObject(Recipe.class), docId);
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }

}
