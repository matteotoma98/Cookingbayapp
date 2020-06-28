package it.tpt.cookingbayapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private Button exit; //Bottone per uscire dall'account
    private Button switchAccount; //Bottone per cambiare account
    private Button changeUsername; //Bottone per modificare l'username dell'utente
    private Button changePassword; // Bottone per modificare la password dell'utente
    private Button deleteAccount;
    private FirebaseAuth mAuth;
    private ImageView profilePic; //Foto di profile
    private ImageView saveUsername;
    private ImageView undoUsername;
    private Uri profileUri;
    private TextView username;
    private EditText editTextUsername;
    private LinearLayout usernameLayout;
    private View layout;
    private final static int PROPIC_REQUEST = 239;
    public static final int LOGIN_REQUEST = 101;
    private String uid;
    private FirebaseFirestore db;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        profilePic = view.findViewById(R.id.userProfilePic);
        changeUsername = view.findViewById(R.id.change_username);
        exit = view.findViewById(R.id.logout);
        switchAccount = view.findViewById(R.id.switch_account);
        username = view.findViewById(R.id.profileUsername);
        layout = view.findViewById(R.id.profileCoordinatorLayout);
        editTextUsername = view.findViewById(R.id.editTextUsername);
        saveUsername = view.findViewById(R.id.saveUsername);
        undoUsername = view.findViewById(R.id.undoUsername);
        usernameLayout = view.findViewById(R.id.modifyusername);
        usernameLayout.setVisibility(View.GONE);
        changePassword = view.findViewById(R.id.profileChangePsw);
        deleteAccount = view.findViewById(R.id.delete_account);
        updateUI(); //Aggiorna l'ui con le informazioni dell'utente

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Inizializzazione dei vari click listener
        saveUsername.setOnClickListener(this);
        undoUsername.setOnClickListener(this);
        profilePic.setOnClickListener(this);
        switchAccount.setOnClickListener(this);
        exit.setOnClickListener(this);
        changeUsername.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        deleteAccount.setOnClickListener(this);

    }

    /**
     * Carica l'immagine di profilo in FirebaseStorage ed aggiorna dunque le info dell'utente con il nuovo url
     */
    private void uploadPicAndUpdateRecipes() {
        try {
            if (profileUri != null) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                uid = user.getUid();

                final String nameOfimage = "profile_pic";
                StorageReference objectStorageReference;
                objectStorageReference = FirebaseStorage.getInstance().getReference("images/" + uid); // Create folder to Firebase Storage
                final StorageReference imageRef = objectStorageReference.child(nameOfimage);

                UploadTask objectUploadTask = imageRef.putFile(profileUri);
                objectUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            final String url = task.getResult().toString();
                            //Aggiorna le info di FirebaseUser
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(url))
                                    .build();
                            user.updateProfile(profileUpdates);
                            Map<String, Object> picUrl = new HashMap<>();
                            picUrl.put("profilePicUrl", url);
                            //Aggiungi l'url nel documento dell'utente per poterlo visualizzare anche nei commenti
                            db.collection("Users").document(uid).set(picUrl, SetOptions.merge());
                            //Aggiungi l'url in tutte le sue ricette per diminuire le call a Firestore nel feed
                            db.collection("Recipes")
                                    .whereEqualTo("authorId", uid)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (final QueryDocumentSnapshot document : task.getResult()) {
                                                    document.getReference().update("profilePicUrl", url);
                                                }
                                            }
                                        }
                                    });
                        } else if (!task.isSuccessful()) {
                            Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PROPIC_REQUEST && resultCode == getActivity().RESULT_OK) {
            if (ImagePickActivity.getPickImageResultUri(getActivity(), intent, "profile") != null) {
                //Prendi l'uri assegnato alla cache
                profileUri = ImagePickActivity.getPickImageResultUri(getActivity(), intent, "profile");
                uploadPicAndUpdateRecipes();
                Glide.with(getContext())
                        .load(profileUri)
                        .centerCrop()
                        .into(profilePic);
            } else {
                Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                Glide.with(getContext())
                        .load(bitmap)
                        .centerCrop()
                        .into(profilePic);
            }
        }
        if (requestCode == LOGIN_REQUEST && resultCode == getActivity().RESULT_OK) {
            updateUI();
        }
    }

    /**
     * Aggiorna l'UI quando viene cambiato l'account e alla creazione del Fragment
     */
    public void updateUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isAnonymous()) {
            username.setText(R.string.user_anonymous);
            Glide.with(getContext())
                    .load(user.getPhotoUrl())
                    .error(R.drawable.missingprofile)
                    .centerCrop()
                    .into(profilePic);
        } else if (user != null && !user.isAnonymous()) {
            username.setText(user.getDisplayName());
            Glide.with(getContext())
                    .load(user.getPhotoUrl())
                    .error(R.drawable.missingprofile)
                    .centerCrop()
                    .into(profilePic);
        }
    }

    @Override
    public void onClick(View v) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        uid = user.getUid();
        switch (v.getId()) {
            case R.id.change_username: //Cambia username
                if (user != null && !user.isAnonymous()) {
                    username.setVisibility(View.GONE);
                    usernameLayout.setVisibility(View.VISIBLE);
                    editTextUsername.setText(username.getText().toString());
                } else
                    Snackbar.make(layout, R.string.username_anonymous, Snackbar.LENGTH_SHORT).show();
                break;

            case R.id.saveUsername: //Conferma il nuovo username
                final String usernameText = editTextUsername.getText().toString().trim();
                if (!usernameText.equals("")) {
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(usernameText)
                            .build();
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        usernameLayout.setVisibility(View.GONE);
                                        username.setText(usernameText);
                                        username.setVisibility(View.VISIBLE);
                                        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(usernameText);
                                    }
                                }
                            });
                    Map<String, Object> name = new HashMap<>();
                    name.put("username", usernameText);
                    //Aggiungi l'username nel documento dell'utente per poterlo visualizzare anche nei commenti
                    db.collection("Users").document(uid).set(name, SetOptions.merge());
                    //Aggiungi l'username a tutte le sue ricette per diminuire le call a Firestore nel feed
                    db.collection("Recipes")
                            .whereEqualTo("authorId", uid)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (final QueryDocumentSnapshot document : task.getResult()) {
                                            document.getReference().update("authorName", usernameText);
                                        }
                                    }
                                }
                            });
                }
                break;

            case R.id.undoUsername: //Annulla la modifica dell'username
                usernameLayout.setVisibility(View.GONE);
                username.setVisibility(View.VISIBLE);
                break;

            case R.id.userProfilePic: //Cambia immagine di profilo
                if (!user.isAnonymous())
                    startActivityForResult(ImagePickActivity.getPickImageChooserIntent(getActivity(), "profile"), PROPIC_REQUEST);
                else
                    Snackbar.make(layout, R.string.profilepic_anonymous, Snackbar.LENGTH_LONG).show();
                break;

            case R.id.switch_account: //Cambia account
                if (user != null && user.isAnonymous()) { //Se si è correntemente connessi come ospite si elimina prima l'account, altrimenti Firebase si riempe di account anonimi
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivityForResult(intent, LOGIN_REQUEST);
                        }
                    });
                } else { //Se con si è connessi come ospite
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivityForResult(intent, LOGIN_REQUEST);
                }
                break;

            case R.id.logout: //Esci e accedi come ospite
                if (user != null && !user.isAnonymous()) { //Evita che si crei un nuovo account anonimo se lo si è già
                    AuthUI.getInstance()
                            .signOut(getActivity())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mAuth.signInAnonymously()
                                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        // Sign in success, update UI with the signed-in user's information
                                                        updateUI();
                                                        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Ospite");
                                                        Snackbar.make(layout, R.string.logged_as_anonymous, Snackbar.LENGTH_SHORT).show();
                                                    } else {
                                                        // If sign in fails, display a message to the user.
                                                        Toast.makeText((Context) getActivity(), R.string.exit_error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                } else
                    Snackbar.make(layout, R.string.already_anonymous, Snackbar.LENGTH_SHORT).show();
                break;

            case R.id.profileChangePsw: //Cambia la password
                if (user != null && !user.isAnonymous()) {
                    PswDialog passwordDialog = new PswDialog(); //Custom dialog per il cambio della password
                    passwordDialog.show(getChildFragmentManager(), "custom");
                } else
                    Snackbar.make(layout, R.string.anonymous, Snackbar.LENGTH_SHORT).show();
                break;

            case R.id.delete_account:
                if (user != null && !user.isAnonymous()) {
                    ReauthDialog deleteDialog = new ReauthDialog();
                    deleteDialog.show(getChildFragmentManager(), "custom");
                } else
                    Snackbar.make(layout, R.string.anonymous, Snackbar.LENGTH_SHORT).show();
                break;

        }
    }
}
