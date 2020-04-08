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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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

public class CreateRecipe extends AppCompatActivity {

    ImageView imgPreview, imgStep1;
    TextInputEditText title, totalTime, steptext1, ingName, ingQuantity, stepHours1, stepMinutes1;
    TextInputLayout titleLayout;
    boolean isUploading;
    boolean isEditing; //Controlla se l'activity è stata avviata da modifica piuttosto che crea ricetta

    //Questi due oggetti step servono per comodità, solo il metodo setUrl() e getUrl() vengono utilizzati
    Step main;
    Step firstStep;

    private RecyclerView iRecyclerView;
    private IngredientsRecyclerViewAdapter iAdapter;
    private RecyclerView mRecyclerView;
    private StepAdapter mAdapter;

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

        title = findViewById(R.id.createRecipeTitle);
        titleLayout = findViewById(R.id.createRecipeTitleLayout);
        totalTime = findViewById(R.id.recipeTime);
        steptext1 = findViewById(R.id.steptext1);
        stepHours1 = findViewById(R.id.stepHours1);
        stepMinutes1 = findViewById(R.id.stepMinutes1);
        imgPreview = findViewById(R.id.imgAnteprima);
        imgStep1 = findViewById(R.id.imgStep1);
        ingName = findViewById(R.id.txtIngredient);
        ingQuantity = findViewById(R.id.txtQuantity);

        isUploading = false;
        isEditing = getIntent().getBooleanExtra("edit", false);
        title.setEnabled(!isEditing);
        if(isEditing) titleLayout.setHint(getString(R.string.titlenoteditable));
        main = new Step("", previewUri);
        firstStep = new Step("", stepUri);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        mRecyclerView = findViewById(R.id.step_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        iRecyclerView = findViewById(R.id.ingDisplay_recycler);
        iRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent.getBooleanExtra("edit", false)) {
            getSupportActionBar().setTitle("Modifica ricetta");

            Recipe editRecipe = (Recipe) intent.getSerializableExtra("recipeToEdit");
            main.setUrl(editRecipe.getPreviewUrl());
            title.setText(editRecipe.getTitle());
            totalTime.setText(editRecipe.getTime());

            ArrayList<Step> steps = new ArrayList<>();
            ArrayList<Section> sections = editRecipe.getSections();
            Section temp1 = sections.get(0);
            int hours1 = temp1.getTimer() / 3600;
            int minutes1 = (temp1.getTimer() % 3600) / 60;
            steptext1.setText(temp1.getText());
            stepHours1.setText(String.valueOf(hours1));
            stepMinutes1.setText(String.valueOf(minutes1));
            firstStep.setUrl(temp1.getImageUrl());
            //test
            if (!temp1.getImageUrl().equals("")) {
                Glide.with(this)
                        .load(temp1.getImageUrl())
                        .apply(RequestOptions.skipMemoryCacheOf(true))
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .centerCrop()
                        .into(imgStep1);
            }
            for (int i = 1; i < sections.size(); i++) {
                Section temp = sections.get(i);
                int hours = temp.getTimer() / 3600;
                int minutes = (temp.getTimer() % 3600) / 60;
                steps.add(new Step(temp.getText(), temp.getImageUrl(), String.valueOf(hours), String.valueOf(minutes)));
            }
            Glide.with(this)
                    .load(editRecipe.getPreviewUrl())
                    .apply(RequestOptions.skipMemoryCacheOf(true))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .centerCrop()
                    .into(imgPreview);
            mAdapter = new StepAdapter(steps, this);
            mRecyclerView.setAdapter(mAdapter);
            iAdapter = new IngredientsRecyclerViewAdapter(editRecipe.getIngredients());
            iRecyclerView.setAdapter(iAdapter);
            mRecipe = editRecipe;

        } else {
            getSupportActionBar().setTitle("Crea nuova ricetta");
            mAdapter = new StepAdapter(new ArrayList<Step>(), this);
            mRecyclerView.setAdapter(mAdapter);
            mRecipe = new Recipe();
            mRecipe.setAuthorName(currentUser.getDisplayName());
            mRecipe.setAuthorId(currentUser.getUid());
            mRecipe.setProfilePicUrl("missingprofile");

            iAdapter = new IngredientsRecyclerViewAdapter(new ArrayList<Ingredient>());
            iRecyclerView.setAdapter(iAdapter);
        }


        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnaskedPermissions(permissions);


        Button btnAddStep = findViewById(R.id.addstep);
        btnAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(CreateRecipe.this, "Premuto", Toast.LENGTH_LONG).show(); //per vedere quando viene premuto il bottone
                mAdapter.addStep();
            }
        });

        Button btnAddIng = findViewById(R.id.addingredient);
        btnAddIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(ingName.getText()) || TextUtils.isEmpty(ingQuantity.getText())) {
                    Toast.makeText(CreateRecipe.this, R.string.ing_required, Toast.LENGTH_LONG).show();
                } else {
                    iAdapter.addIngredient(new Ingredient(ingName.getText().toString(), ingQuantity.getText().toString()));
                    ingName.setText("");
                    ingQuantity.setText("");
                }
                //metodo per aggiungere ingrediente alla recycler view
            }
        });

        Button btnDelIng = findViewById(R.id.deleteIngredient);
        btnDelIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iAdapter.delIngredient();
                //metodo per eliminare
            }
        });

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
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.exitNoSave) {
            Toast.makeText(this, "Ricetta non salvata", Toast.LENGTH_SHORT).show();
            finish();
            //  startActivity(new Intent(this, LmrFragment.class));
        } else if (id == R.id.exitSave) {
            if (checkInfo()) {
                Toast.makeText(this, R.string.minimum_info_required, Toast.LENGTH_LONG).show();
            } else {
                if (isUploading == false) {
                    folder = currentUser.getUid() + "/" + title.getText();
                    if (previewUri != null) {
                        main.setUrl("");
                        ImagePickActivity.uploadToStorage(this, previewUri, folder, "preview", main);
                    }
                    if (stepUri != null) {
                        firstStep.setUrl("");
                        ImagePickActivity.uploadToStorage(this, stepUri, folder, "firstStep", firstStep);
                    }
                    for (int i = 0; i < mAdapter.getItemCount(); i++) {
                        if (mAdapter.getSteps().get(i).getHasPicture()) {
                            mAdapter.getSteps().get(i).setUrl("");
                            ImagePickActivity.uploadToStorage(this, mAdapter.getSteps().get(i).getStepUri(), folder, "step" + i, mAdapter.getSteps().get(i));
                        }
                    }
                    isUploading = true;
                }
                boolean finishedUploading = false;
                if (!main.getUrl().equals("")) {
                    finishedUploading = true;
                    if (firstStep.getHasPicture() && firstStep.getUrl().equals(""))
                        finishedUploading = false;
                    for (int i = 0; i < mAdapter.getItemCount(); i++) {
                        if (mAdapter.getSteps().get(i).getHasPicture() && mAdapter.getSteps().get(i).getUrl().equals("")) {
                            finishedUploading = false;
                            break;
                        }
                    }
                }
                if (finishedUploading) {
                    //Authorid e AuthorName in onCreate()
                    mRecipe.setTitle(title.getText().toString().trim());
                    mRecipe.setType("secondo piatto");
                    mRecipe.setPreviewUrl(main.getUrl());
                    mRecipe.setIngredients(iAdapter.getIngredients());
                    mRecipe.setTime(totalTime.getText().toString());
                    ArrayList<Section> sections = new ArrayList<>();
                    //Timer primo step
                    int hours1 = (TextUtils.isEmpty(stepHours1.getText())) ? 0 : Integer.parseInt(stepHours1.getText().toString());
                    int minutes1 = (TextUtils.isEmpty(stepMinutes1.getText())) ? 0 : Integer.parseInt(stepMinutes1.getText().toString());
                    int timer1 = hours1 * 3600 + minutes1 * 60;

                    sections.add(new Section(steptext1.getText().toString().trim(), firstStep.getUrl(), timer1));
                    List<Step> templist = mAdapter.getSteps();
                    for (int i = 0; i < mAdapter.getItemCount(); i++) {
                        int hours = (TextUtils.isEmpty(templist.get(i).getHours())) ? 0 : Integer.parseInt(templist.get(i).getHours());
                        int minutes = (TextUtils.isEmpty(templist.get(i).getMinutes())) ? 0 : Integer.parseInt(templist.get(i).getMinutes());
                        int time = hours * 3600 + minutes * 60;
                        sections.add(new Section(templist.get(i).getText().trim(), templist.get(i).getUrl(), time));
                    }
                    mRecipe.setSections(sections);
                    if (isEditing) {
                        //aggiorna la ricetta esistente
                        db.collection("Recipes").document(getIntent().getStringExtra("recipeId"))
                                .set(mRecipe)
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
                        Toast.makeText(this, R.string.doneediting, Toast.LENGTH_LONG).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        //Aggiunge una nuova ricetta
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
                        setResult(RESULT_OK);
                        finish();
                    }
                } else Toast.makeText(this, R.string.uploading, Toast.LENGTH_LONG).show();
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
                stepUri = ImagePickActivity.getPickImageResultUri(this, intent, "firstStep");
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
            if (ImagePickActivity.getPickImageResultUri(this, intent, "step" + position) != null) {
                templist.get(position).setStepUri(ImagePickActivity.getPickImageResultUri(this, intent, "step" + position));
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    //Funzione per controllare che l'utente abbia inserito tutti le informazioni generali
    private boolean checkInfo() {
        boolean preview = (previewUri == null && !isEditing);

        boolean t = TextUtils.isEmpty(title.getText());
        String trimmed;
        if(!t) {
            trimmed = title.getText().toString().trim(); //Controlla che l'utente non abbia inserito solo spazi
            t = t || trimmed.equals("");
        }
        boolean s = TextUtils.isEmpty(steptext1.getText());
        if(!s) {
            trimmed = steptext1.getText().toString().trim(); //Controlla che l'utente non abbia inserito solo spazi
            s = s || trimmed.equals("");
        }

        return preview || t || s || TextUtils.isEmpty(totalTime.getText()) || iAdapter.getItemCount() == 0;
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
