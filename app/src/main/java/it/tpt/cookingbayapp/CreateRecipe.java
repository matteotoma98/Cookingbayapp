package it.tpt.cookingbayapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import it.tpt.cookingbayapp.stepRecycler.Step;
import it.tpt.cookingbayapp.stepRecycler.StepAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateRecipe extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private StepAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        getSupportActionBar().setTitle("Crea nuova ricetta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.step_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StepAdapter(new ArrayList<Step>());
        mRecyclerView.setAdapter(mAdapter);

        Button btnAddStep = findViewById(R.id.addstep);
        btnAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateRecipe.this, "Premuto", Toast.LENGTH_LONG).show(); //per vedere quando viene premuto il bottone
                mAdapter.addStep(new Step(Integer.toString(mAdapter.getItemCount()+2)));
            }
        });

        CircleImageView imgAnteprima = findViewById(R.id.imgAnteprima);
        imgAnteprima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateRecipe.this, "Selettore img anteprima", Toast.LENGTH_LONG).show(); //per vedere quando viene premuto il bottone
            }
        });

        CircleImageView imgStep = findViewById(R.id.imgStep);
        imgStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //azioni di quando si preme
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
}
