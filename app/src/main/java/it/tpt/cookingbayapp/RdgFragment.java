package it.tpt.cookingbayapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Calendar;


import it.tpt.cookingbayapp.cardRecycler.RecipeCardRecyclerViewAdapter;
import it.tpt.cookingbayapp.recipeObject.Recipe;



public class RdgFragment extends Fragment {

    FirebaseFirestore db;

    public RdgFragment() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with the ProductGrid theme
        View view = inflater.inflate(R.layout.fragment_rdg, container, false);
        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.cardRecycler_view);
        recyclerView.setHasFixedSize(true);

        db = FirebaseFirestore.getInstance();

        downloadRecipes(0); //Scarica le ricette

        //int largePadding = getResources().getDimensionPixelSize(R.dimen.shr_product_grid_spacing);
        //int smallPadding = getResources().getDimensionPixelSize(R.dimen.shr_product_grid_spacing_small);
        //recyclerView.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));

        return view;
    }

    /**
     * Scarica le ricette giornaliere da Firestore e le assegna al recyclerView
     * E' una funzione ricorsiva. Se ci sono meno di tre ricette giornaliere scarica tutte le ricette dal giorno precedente e cosi via
     * @param daysBefore quanti giorni indietro bisogna cercare, utilizzato nella query whereGreaterThanOrEqualTo
     */
    private void downloadRecipes(int daysBefore) {
        db.collection("Recipes")
                .whereGreaterThanOrEqualTo("date", getCurrentDayInSeconds() - daysBefore*24*60*60)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<Recipe> recipeList = new ArrayList<>();
                            int count = 0;
                            for (final QueryDocumentSnapshot document : task.getResult()) {
                                Recipe recipe = document.toObject(Recipe.class);
                                recipeList.add(recipe);
                                count++;
                            }
                            if(count<3) downloadRecipes(1);
                            else {
                                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
                                RecipeCardRecyclerViewAdapter adapter = new RecipeCardRecyclerViewAdapter(getActivity(), recipeList);
                                recyclerView.setAdapter(adapter);
                                Log.i("Finish", "Recipes downloaded");
                            }
                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Ottiene i secondi passati dal 1970 alle ore 0:00 del giorno corrente
     * Si utilizzano i secondi perché Timestamp non é serializable mentre Date non é Parceable
     * @return Secondi
     */
    public long getCurrentDayInSeconds() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long now = c.getTimeInMillis();
        long secondsPassed = now / 1000;
        return secondsPassed;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        downloadRecipes(0);
    }
}
