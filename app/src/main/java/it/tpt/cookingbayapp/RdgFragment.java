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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import it.tpt.cookingbayapp.recipeObject.Recipe;
import it.tpt.cookingbayapp.recipeObject.Section;


public class RdgFragment extends Fragment {


    public RdgFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ArrayList<Recipe> createRecipeList(){
        ArrayList<Recipe> list = new ArrayList<>();
        ArrayList<Section> slist = new ArrayList<>();
        slist.add(new Section("Prova prova prova prova prova", "someUrl", 29));
        list.add(new Recipe("Pasta al ragù", "25 min", "Mario", slist ));

        //Test per aggiungere una ricetta 'Recipe' nel database
        Recipe test = new Recipe("Pasta al ragù", "25 min", "Mario", slist );
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = "Pasta-al-ragù-id";
        db.collection("Users").document("Remy").collection("Recipes").document(id)
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

        return list;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.fragment_rdg, container, false);

        // Set up the RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        //RecipeCardRecyclerViewAdapter adapter = new RecipeCardRecyclerViewAdapter(ProductEntry.initProductEntryList(getResources()));
        /* DOBBIAMO PASSARE AL COSTRUTTORE QUI SOTTO LA LISTA DESIDERATA
           PER ORA E' CREATA MANUALMENTE NELLA CLASSE RecipeCardRecyclerViewAdapter
         */

        ArrayList<Recipe> recipeList = createRecipeList();

        RecipeCardRecyclerViewAdapter adapter = new RecipeCardRecyclerViewAdapter(getActivity(), recipeList);
        recyclerView.setAdapter(adapter);
        //int largePadding = getResources().getDimensionPixelSize(R.dimen.shr_product_grid_spacing);
        //int smallPadding = getResources().getDimensionPixelSize(R.dimen.shr_product_grid_spacing_small);
        //recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));

        return view;
    }
}
