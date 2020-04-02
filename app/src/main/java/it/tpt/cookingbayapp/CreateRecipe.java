package it.tpt.cookingbayapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.tpt.cookingbayapp.stepRecycler.Step;
import it.tpt.cookingbayapp.stepRecycler.StepAdapter;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateRecipe extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private StepAdapter mAdapter;
    private final static int IMAGE_REQUEST = 234;
    CircleImageView imgPreview;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;

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
                mAdapter.addStep(new Step(Integer.toString(mAdapter.getItemCount() + 2), ""));

            }
        });

        imgPreview = findViewById(R.id.imgAnteprima);
        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionsToRequest = findUnaskedPermissions(permissions);
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                } else {
                    startActivityForResult(getPickImageChooserIntent(), IMAGE_REQUEST);
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
            Toast.makeText(this, "Salva ricetta", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            if (getPickImageResultUri(intent) != null) {
                Uri picUri = getPickImageResultUri(intent);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                bitmap = (Bitmap) intent.getExtras().get("data");
            }

            Glide.with(this)
                    .load(bitmap)
                    .centerCrop()
                    .into(imgPreview);
        }
    }

    private Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        return isCamera ? getCaptureImageOutputUri() : data.getData();

    }

    private Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, getString(R.string.sorgente));

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    public Uri getCaptureImageOutputUri() { //restituisce l'uri dell immagine che da qualche parte verrà salvata all'interno della cartella di cache
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "propic.png"));
        }
        return outputFileUri;
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
                startActivityForResult(getPickImageChooserIntent(), IMAGE_REQUEST);
            }
        }
    }
}
