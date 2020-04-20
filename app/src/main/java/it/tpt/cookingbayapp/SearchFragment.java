package it.tpt.cookingbayapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.tpt.cookingbayapp.ingNamesRecyler.IngNamesAdapter;

public class SearchFragment extends Fragment {

    private RecyclerView ingRecyclerView;
    private IngNamesAdapter ingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,container,false);

        ingRecyclerView = view.findViewById();
        ingRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ingAdapter = new IngNamesAdapter(new ArrayList<String>());

        return view;
    }
}
