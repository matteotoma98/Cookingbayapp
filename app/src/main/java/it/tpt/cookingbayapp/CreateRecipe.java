package it.tpt.cookingbayapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import it.tpt.cookingbayapp.stepRecycler.Step;
import it.tpt.cookingbayapp.stepRecycler.StepAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateRecipe extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private StepAdapter mAdapter;
    private final static int IMAGE_REQUEST = 234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        getSupportActionBar().setTitle("Crea nuova ricetta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRecyclerView = findViewById(R.id.step_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StepAdapter(new ArrayList<Step>());
        mRecyclerView.setAdapter(mAdapter);

        Button btnAddStep = findViewById(R.id.addstep);
        btnAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(CreateRecipe.this, "Premuto", Toast.LENGTH_LONG).show(); //per vedere quando viene premuto il bottone
                mAdapter.addStep(new Step(Integer.toString(mAdapter.getItemCount()+2), "") );

            }
        });

        CircleImageView imgAnteprima = findViewById(R.id.imgAnteprima);
        imgAnteprima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateRecipe.this, "Selettore img anteprima", Toast.LENGTH_LONG).show(); //per vedere quando viene premuto il bottone
                try {
                    Intent objectIntent = new Intent();
                    objectIntent.setType("image/*");

                    objectIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(objectIntent,IMAGE_REQUEST);

                } catch (Exception e){
                    Toast.makeText(CreateRecipe.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        CircleImageView imgStep1 = findViewById(R.id.imgStep1);
        imgStep1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
   /*     MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true; */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.exitNoSave) {
            Toast.makeText(this, "Ricetta non salvata", Toast.LENGTH_SHORT).show();
            finish();
          //  startActivity(new Intent(this, LmrFragment.class));
        } else if (id == R.id.exitSave) {
            Toast.makeText(this, "Salva ricetta", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
