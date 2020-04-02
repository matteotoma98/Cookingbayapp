package it.tpt.cookingbayapp;

public class PhotoUploader {
    public static final int LOGIN_REQUEST = 101;
    private FirebaseAuth mAuth;

    private ImageView proPic;
    private TextView textNome;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 107;
    private final static int PICK_IMAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        proPic = findViewById(R.id.propic);
        proPic.setClipToOutline(true);
        textNome = findViewById(R.id.text_nome);

        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
        if (preferences.getBoolean("firstrun", true)) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        } else {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            textNome.setText(currentUser.getDisplayName());
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(currentUser.getPhotoUrl())
                        .centerCrop()
                        .into(proPic);
            } else {
                Glide.with(this)
                        .load(getDrawable(R.drawable.placeholder_profile))
                        .centerCrop()
                        .into(proPic);

            }

            proPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    permissions.add(Manifest.permission.CAMERA);
                    permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    permissionsToRequest = findUnaskedPermissions(permissions);
                    if (permissionsToRequest.size() > 0) {
                        requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                    } else {
                        startActivityForResult(getPickImageChooserIntent(), PICK_IMAGE);
                    }
                }
            });

            getSupportActionBar().setTitle(getString(R.string.benvenuto));
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

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {
                String nome = intent.getExtras().getString("nome");
                String cognome = intent.getExtras().getString("cognome");

                getSupportActionBar().setTitle(nome + " " + cognome);

                SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("firstrun", false);
                editor.apply();
            }
        } else if (requestCode == PICK_IMAGE) {
            Bitmap bitmap = null;
            if (resultCode == RESULT_OK) {
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
            }

            Glide.with(this)
                    .load(bitmap)
                    .centerCrop()
                    .into(proPic);
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

        Intent chooserIntent = Intent.createChooser(mainIntent, getString(R.string.selsorgente));

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
                startActivityForResult(getPickImageChooserIntent(), PICK_IMAGE);
            }
        }
    }
}
}
