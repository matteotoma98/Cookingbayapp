package it.tpt.cookingbayapp;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import it.tpt.cookingbayapp.recipeObject.Recipe;
import it.tpt.cookingbayapp.stepRecycler.Step;
import it.tpt.cookingbayapp.stepRecycler.StepAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateRecipe extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private StepAdapter mAdapter;
    CircleImageView imgPreview, imgStep1;
    TextInputEditText title, steptext1;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        getSupportActionBar().setTitle("Crea nuova ricetta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRecyclerView = findViewById(R.id.step_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StepAdapter(new ArrayList<Step>(), this);
        mRecyclerView.setAdapter(mAdapter);

        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsToRequest = findUnaskedPermissions(permissions);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mRecipe = new Recipe();
        mRecipe.setAuthorName(currentUser.getDisplayName());
        mRecipe.setAuthorId(currentUser.getUid());

        Button btnAddStep = findViewById(R.id.addstep);
        btnAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(CreateRecipe.this, "Premuto", Toast.LENGTH_LONG).show(); //per vedere quando viene premuto il bottone
                mAdapter.addStep(new Step(Integer.toString(mAdapter.getItemCount() + 2), "", Uri.parse("")));

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
                    startActivityForResult(ImagePickActivity.getPickImageChooserIntent(CreateRecipe.this, "step1"), PREVIEW_REQUEST);
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

            }
            //Toast.makeText(this, "Salva ricetta", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PREVIEW_REQUEST && resultCode == RESULT_OK) {

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
        if (requestCode == STEP_REQUEST && resultCode == RESULT_OK) {
            List<Step> templist = mAdapter.getSteps();
            int position = mAdapter.getCurrentPicPosition();
            if (ImagePickActivity.getPickImageResultUri(this, intent, "step" + position + 2) != null) {
                templist.get(position).setStepUri(ImagePickActivity.getPickImageResultUri(this, intent, "step" + position + 2));
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
