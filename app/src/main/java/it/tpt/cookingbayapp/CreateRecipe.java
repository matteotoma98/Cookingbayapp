package it.tpt.cookingbayapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import it.tpt.cookingbayapp.ingredientsRecycler.IngredientsRecyclerViewAdapter;
import it.tpt.cookingbayapp.recipeObject.Ingredient;
import it.tpt.cookingbayapp.recipeObject.Recipe;
import it.tpt.cookingbayapp.recipeObject.Section;
import it.tpt.cookingbayapp.stepRecycler.Step;
import it.tpt.cookingbayapp.stepRecycler.StepAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateRecipe extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private StepAdapter mAdapter;
    CircleImageView imgPreview, imgStep1;
    TextInputEditText title, steptext1;
    boolean isUploading;

    //Questi due oggetti step servono per comodità, solo il metodo setUrl() e getUrl()
    Step main;
    Step firstStep;

    private RecyclerView iRecyclerView;
    private IngredientsRecyclerViewAdapter iAdapter;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int PREVIEW_REQUEST = 234;
    private final static int STEP1_REQUEST = 235;
    private final static int STEP_REQUEST = 236;

    private Recipe mRecipe;
    private Uri previewUri;
    private Uri stepUri;

    FirebaseUser currentUser;
    FirebaseFirestore db;
    String folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        getSupportActionBar().setTitle("Crea nuova ricetta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        isUploading = false;
        main = new Step("0", "", previewUri);
        firstStep = new Step("1", "", stepUri);

        mRecyclerView = findViewById(R.id.step_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StepAdapter(new ArrayList<Step>(), this);
        mRecyclerView.setAdapter(mAdapter);

        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnaskedPermissions(permissions);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        mRecipe = new Recipe();
        mRecipe.setAuthorName(currentUser.getDisplayName());
        mRecipe.setAuthorId(currentUser.getUid());
        mRecipe.setProfilePicUrl("missingprofile");

        iRecyclerView = findViewById(R.id.ingDisplay_recycler);
       // iRecyclerView.setHasFixedSize(true);
        iRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        iAdapter = new IngredientsRecyclerViewAdapter(new ArrayList<Ingredient>());
        iRecyclerView.setAdapter(iAdapter);

        Button btnAddStep = findViewById(R.id.addstep);
        btnAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(CreateRecipe.this, "Premuto", Toast.LENGTH_LONG).show(); //per vedere quando viene premuto il bottone
                mAdapter.addStep(new Step(Integer.toString(mAdapter.getItemCount() + 2), "", Uri.parse("")));
            }
        });

        Button btnAddIng = findViewById(R.id.addingredient);
        btnAddIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iAdapter.addIngredient(new Ingredient("nome","500g"));
                //metodo per aggiungere ingrediente alla recycler view
            }
        });

        Button btnDelIng= findViewById(R.id.deleteIngredient);
        btnDelIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iAdapter.delIngredient();
                //metodo per eliminare
            }
        });


        title = findViewById(R.id.createRecipeTitle);
        steptext1 = findViewById(R.id.steptext1);
        imgPreview = findViewById(R.id.imgAnteprima);
        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                } else {
                    startActivityForResult(ImagePickActivity.getPickImageChooserIntent(CreateRecipe.this, "preview"), PREVIEW_REQUEST);
                }

            }
        });

        imgStep1 = findViewById(R.id.imgStep1);
        imgStep1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                } else {
                    startActivityForResult(ImagePickActivity.getPickImageChooserIntent(CreateRecipe.this, "firstStep"), STEP1_REQUEST);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
            if(previewUri == null || TextUtils.isEmpty(title.getText()) || stepUri == null || TextUtils.isEmpty(steptext1.getText())){
                Toast.makeText(this, R.string.minimum_info_required, Toast.LENGTH_LONG).show();
            } else {
                if(isUploading == false) {
                    folder = currentUser.getUid() + "/" + title.getText();
                    ImagePickActivity.uploadToStorage(this, previewUri, folder, "preview", main);
                    ImagePickActivity.uploadToStorage(this, stepUri, folder, "firstStep", firstStep);
                    for (int i = 0; i < mAdapter.getItemCount(); i++) {
                        ImagePickActivity.uploadToStorage(this, mAdapter.getSteps().get(i).getStepUri(), folder, "step" + i, mAdapter.getSteps().get(i));
                    }
                    isUploading = true;
                }
                boolean finishedUploading = false;
                if(!main.getUrl().equals("")){
                    finishedUploading = true;
                    if(firstStep.getHasPicture() && firstStep.getUrl().equals("")) finishedUploading=false;
                    for (int i = 0; i < mAdapter.getItemCount(); i++) {
                        if(mAdapter.getSteps().get(i).getHasPicture() && mAdapter.getSteps().get(i).getUrl().equals("")) {
                            finishedUploading=false;
                            break;
                        }
                    }
                }
                if(finishedUploading){
                    //Authorid e AuthorName in onCreate()
                    mRecipe.setTitle(title.getText().toString());
                    mRecipe.setType("secondo piatto");
                    mRecipe.setPreviewUrl(main.getUrl());
                    mRecipe.setIngredients(iAdapter.getIngredients());
                    mRecipe.setTime("40 min");
                    ArrayList<Section> sections = new ArrayList<>();
                    sections.add(new Section(steptext1.getText().toString(), firstStep.getUrl(), 0));
                    List<Step> templist = mAdapter.getSteps();
                    for(int i = 0; i < mAdapter.getItemCount(); i++){
                        sections.add(new Section(templist.get(i).getText(), templist.get(i).getUrl(), 0));
                    }
                    mRecipe.setSections(sections);
                    db.collection("Recipes")
                            .add(mRecipe)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("Firestore up", "DocumentSnapshot written with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("Firestore up", "Error adding document", e);
                                }
                            });
                    Toast.makeText(this, R.string.donesharing, Toast.LENGTH_LONG).show();
                    finish();
                }
                else Toast.makeText(this, R.string.uploading, Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(this, "Salva ricetta", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PREVIEW_REQUEST && resultCode == RESULT_OK) {
            isUploading = false;
            if (ImagePickActivity.getPickImageResultUri(this, intent, "preview") != null) {
                //Prendi l'uri assegnato alla cache
                previewUri = ImagePickActivity.getPickImageResultUri(this, intent, "preview");
                Glide.with(this)
                        .load(previewUri)
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .centerCrop()
                        .into(imgPreview);
            } else {
                Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                Glide.with(this)
                        .load(bitmap)
                        .centerCrop()
                        .into(imgPreview);
            }
        }
        if (requestCode == STEP1_REQUEST && resultCode == RESULT_OK) {
            isUploading = false;
            firstStep.setHasPicture(true);
            if (ImagePickActivity.getPickImageResultUri(this, intent, "firstStep") != null) {
                //Prendi l'uri assegnato alla cache
                stepUri = ImagePickActivity.getPickImageResultUri(this, intent, "firsStep");
                Glide.with(this)
                        .load(stepUri)
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .centerCrop()
                        .into(imgStep1);
            } else {
                Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                Glide.with(this)
                        .load(bitmap)
                        .centerCrop()
                        .into(imgStep1);
            }
        }
        if (requestCode == STEP_REQUEST && resultCode == RESULT_OK) {
            isUploading = false;
            List<Step> templist = mAdapter.getSteps();
            int position = mAdapter.getCurrentPicPosition();
            templist.get(position).setHasPicture(true);
            if (ImagePickActivity.getPickImageResultUri(this, intent, "step" + position ) != null) {
                templist.get(position).setStepUri(ImagePickActivity.getPickImageResultUri(this, intent, "step" + position ));
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    private ArrayList findUnaskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!(checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)) {
                result.add(perm);
            }
        }

        return result;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ALL_PERMISSIONS_RESULT) {
            for (String perm : permissionsToRequest) {
                if (!(checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED)) {
                    permissionsRejected.add(perm);
                }
            }
            if (permissionsRejected.size() > 0) {
                if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                    Toast.makeText(this, "Approva tutto", Toast.LENGTH_SHORT).show();
                }
            } else {
                startActivityForResult(ImagePickActivity.getPickImageChooserIntent(this, "preview"), PREVIEW_REQUEST);
            }
        }
    }
}
