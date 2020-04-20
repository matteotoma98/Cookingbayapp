package it.tpt.cookingbayapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import it.tpt.cookingbayapp.ingNamesRecyler.IngNamesAdapter;

public class SearchFragment extends Fragment implements View.OnClickListener{

    private RecyclerView ingRecyclerView;
    private IngNamesAdapter ingAdapter;
    private TextInputEditText ingName;
    private ImageView addIng, delIng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,container,false);

        ingName = view.findViewById(R.id.txtIngNameSearch);
        addIng = view.findViewById(R.id.addIngName);
        delIng = view.findViewById(R.id.delIngName);

        ingRecyclerView = view.findViewById(R.id.ingNameRecycler);
        ingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ingAdapter = new IngNamesAdapter(new ArrayList<String>());
        ingRecyclerView.setAdapter(ingAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addIng.setOnClickListener(this);
        delIng.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addIngName:
                String ing = ingName.getText().toString().trim();
                if(!ing.equals("")) {
                    ingAdapter.addIngredient(ing.toLowerCase());
                }
                break;
            case R.id.delIngName:
                ingAdapter.delIngredient();
                break;
        }
    }
}
