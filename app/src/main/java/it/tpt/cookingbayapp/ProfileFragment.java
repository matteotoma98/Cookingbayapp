package it.tpt.cookingbayapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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


public class ProfileFragment extends Fragment {

    private Button exit; //Bottone per uscire dall'account
    private Button switch_account; //Bottone per cambiare account
    private FirebaseAuth mAuth;
    private ImageView profilePic; //Foto di profile
    private Uri profileUri;
    private TextView username;
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
        profilePic = view.findViewById(R.id.userProfilePic);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        exit = view.findViewById(R.id.logout);
        switch_account = view.findViewById(R.id.switch_account);
        username = view.findViewById(R.id.profileUsername);
        layout = view.findViewById(R.id.profileCoordinatorLayout);

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

        //Click Listener dell'immagine di profilo
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (!user.isAnonymous()) {
                    startActivityForResult(ImagePickActivity.getPickImageChooserIntent(getActivity(), "profile"), PROPIC_REQUEST);
                } else
                    Snackbar.make(layout, R.string.profilepic_anonymous, Snackbar.LENGTH_LONG).show();
            }
        });
        //Click listener per cambiare account
        switch_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
            }
        });
        //Click listener per uscire (e connettersi dunque come ospite)
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                                                        Log.d("signin", "signInAnonymously:success");
                                                        updateUI();
                                                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Cooking Bay");
                                                        Snackbar.make(layout, R.string.logged_as_anonymous, Snackbar.LENGTH_SHORT).show();
                                                    } else {
                                                        // If sign in fails, display a message to the user.
                                                        Log.w("signinerror", "signInAnonymously:failure", task.getException());
                                                        Toast.makeText((Context) getActivity(), R.string.exit_error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                } else
                    Snackbar.make(layout, R.string.already_anonymous, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Carica l'immagine di profilo in FirebaseStorage ed aggiorna dunque le info dell'utente con il nuovo url
     */
    private void uploadPicAndUpdateRecipes() {
        try {
            if (profileUri != null) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                uid = user.getUid();

                final String nameOfimage = "profile_pic." + ImagePickActivity.getExtension(getContext(), profileUri);
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
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("UPDATEPROPIC", "User profile updated.");
                                            }
                                        }
                                    });
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

                                            } else {
                                                Log.d("TAG", "Error getting documents: ", task.getException());
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
            Log.i("PICUPLOADPROFILE", "RESULT OK");
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
    public void updateUI(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null && user.isAnonymous()) {
            username.setText("Utente anonimo");
            Glide.with(getContext())
                    .load(user.getPhotoUrl())
                    .error(R.drawable.missingprofile)
                    .centerCrop()
                    .into(profilePic);
        }
        else if (user!= null && !user.isAnonymous()) {
            username.setText(user.getDisplayName());
            Glide.with(getContext())
                    .load(user.getPhotoUrl())
                    .error(R.drawable.missingprofile)
                    .centerCrop()
                    .into(profilePic);
        }
    }
}
