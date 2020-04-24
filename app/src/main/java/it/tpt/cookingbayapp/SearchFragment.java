package it.tpt.cookingbayapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;

import it.tpt.cookingbayapp.cardRecycler.RecipeCardRecyclerViewAdapter;
import it.tpt.cookingbayapp.ingNamesRecyler.IngNamesAdapter;
import it.tpt.cookingbayapp.recipeObject.Recipe;

public class SearchFragment extends Fragment implements View.OnClickListener {

    private RecyclerView ingRecyclerView, recipeRecyclerView;
    private RecipeCardRecyclerViewAdapter recipeAdapter;
    private IngNamesAdapter ingAdapter;
    private TextInputEditText ingName, searchText, searchTime;
    private ImageView addIng, delIng;
    private ChipGroup chipGroup;
    private Button searchBtn;
    private FirebaseFirestore db;
    private CollectionReference recipes;
    private String selectedChip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        ingName = view.findViewById(R.id.txtIngNameSearch);
        addIng = view.findViewById(R.id.addIngName);
        delIng = view.findViewById(R.id.delIngName);
        searchText = view.findViewById(R.id.searchTitle);
        searchTime = view.findViewById(R.id.searchTime);
        searchBtn = view.findViewById(R.id.btnSearch);
        chipGroup = view.findViewById(R.id.chipGroupType);
        selectedChip = "";


        recipeRecyclerView = view.findViewById(R.id.searchRecycler);

        ingRecyclerView = view.findViewById(R.id.ingNameRecycler);
        ingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ingAdapter = new IngNamesAdapter(new ArrayList<String>());
        ingRecyclerView.setAdapter(ingAdapter);

        db = FirebaseFirestore.getInstance();
        recipes = db.collection("Recipes");


        return view;
    }

    /**
     * Genera la query a seconda dei dati immessi dall'utente
     * @return query Firebase
     */
    private Query generateQuery() {
        Query query = null;
        String trimmed = searchText.getText().toString().toLowerCase().trim();
        if(!trimmed.equals("") && searchText.isEnabled()) {
            String[] words = trimmed.split(" ");
            query = recipes.whereArrayContainsAny("titleWords", Arrays.asList(words));
        }
        if(!selectedChip.equals("")) {
            if(query!=null) query = query.whereEqualTo("type", selectedChip);
            else query = recipes.whereEqualTo("type", selectedChip);
        }
        if(ingAdapter.getItemCount()!=0) {
            if(query!=null) query = query.whereArrayContainsAny("ingNames", ingAdapter.getIngredients());
            else query = recipes.whereArrayContainsAny("ingNames", ingAdapter.getIngredients());

        }
        if(!TextUtils.isEmpty(searchTime.getText())) {
            int timeToSearch = Integer.parseInt(searchTime.getText().toString());
            if(query!=null) query = query.whereLessThanOrEqualTo("time", timeToSearch);
            else query = recipes.whereLessThanOrEqualTo("time", timeToSearch);
        }

        return query;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addIng.setOnClickListener(this);
        delIng.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, @IdRes int checkedId) {
                switch (group.getCheckedChipId()) {
                    case R.id.chpPrimo:
                        selectedChip="Primo Piatto";
                        break;
                    case R.id.chpSecondo:
                        selectedChip="Secondo Piatto";
                        break;
                    case R.id.chpDessert:
                        selectedChip="Dessert";
                        break;
                    case R.id.chpAntipasto:
                        selectedChip="Antipasto";
                        break;
                    case R.id.chpContorno:
                        selectedChip="Contorno";
                        break;
                    case R.id.chpBevanda:
                        selectedChip="Bevanda";
                        break;
                    case R.id.chpPanino:
                        selectedChip="Panino";
                        break;
                    default:
                        selectedChip = "";
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addIngName:
                String ing = ingName.getText().toString().trim();
                if (!ing.equals("")) {
                    ingAdapter.addIngredient(ing.toLowerCase());
                    searchText.setEnabled(false);
                    searchText.setText(R.string.search_by_text_not_possible);
                }
                break;
            case R.id.delIngName:
                ingAdapter.delIngredient();
                if(ingAdapter.getItemCount()==0) {
                    searchText.setEnabled(true);
                    searchText.setText("");
                }
                break;
            case R.id.btnSearch:
                if(generateQuery()!=null) {
                    generateQuery()
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
                                    recipeRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
                                    recipeAdapter = new RecipeCardRecyclerViewAdapter(getActivity(), recipeList, recipeIds);
                                    recipeRecyclerView.setAdapter(recipeAdapter);
                                    Log.i("FinishLMR", "Recipes downloaded");
                                }
                            });
                } else Toast.makeText(getContext(), R.string.no_recipe, Toast.LENGTH_LONG).show();
                break;
        }
    }
}
