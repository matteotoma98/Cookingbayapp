package it.tpt.cookingbayapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.tpt.cookingbayapp.cardRecycler.RecipeCardRecyclerViewAdapter;
import it.tpt.cookingbayapp.recipeObject.Ingredient;
import it.tpt.cookingbayapp.recipeObject.Recipe;
import it.tpt.cookingbayapp.recipeObject.Section;


public class RdgFragment extends Fragment {


    public RdgFragment() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void createRecipeList(){
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        ArrayList<Section> slist = new ArrayList<>();
        slist.add(new Section("mmmh mmmg mmmh gnam gnam gnam nice", "someUrl2", 29));
        ArrayList<Ingredient> ing = new ArrayList<>();
        ing.add(new Ingredient("Pasta","200 g"));
        ing.add(new Ingredient("Pesto", "100 g"));

        //Test per aggiungere una ricetta 'Recipe' nel database
        Recipe test = new Recipe("Pasta al pesto", "url", "url", "25 min", "Primo Piatto", "Luigi Qualcosa", ing, slist );
        String id = "Pasta-al-pesto-id";
        Map<String, Object> name = new HashMap<>();
        name.put("name", "Luigi");
        name.put("surname", "Qualcosa");
        db.collection("Users").document("luigi@gmail.com").set(name);
        db.collection("Users").document("luigi@gmail.com").collection("Recipes").document(id)
                .set(test)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.fragment_rdg, container, false);
        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.cardRecycler_view);
        recyclerView.setHasFixedSize(true);
        //createRecipeList(); //Temporaneo
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();

        db.collectionGroup("Recipes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Recipe> recipeList = new ArrayList<>();
                            String profilePicUrl;
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                Recipe recipe = document.toObject(Recipe.class);
                                recipeList.add(recipe);
                            }
                            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
                            RecipeCardRecyclerViewAdapter adapter = new RecipeCardRecyclerViewAdapter(getActivity(), recipeList);
                            recyclerView.setAdapter(adapter);
                            Log.i("Finish", "Recipes downloaded");
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });



        //int largePadding = getResources().getDimensionPixelSize(R.dimen.shr_product_grid_spacing);
        //int smallPadding = getResources().getDimensionPixelSize(R.dimen.shr_product_grid_spacing_small);
        //recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));

        return view;
    }
}
