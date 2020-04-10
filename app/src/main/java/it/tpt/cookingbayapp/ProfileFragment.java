package it.tpt.cookingbayapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;


public class ProfileFragment extends Fragment {

    private Button exit;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView imagePreview;
    private Uri previewUri;
    public static final int LOGIN_REQUEST = 101;
    public static final int RC_SIGN_IN = 105;
    private final static int PROPIC_REQUEST = 239;
    private boolean isUploading;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        imagePreview = view.findViewById(R.id.userProfilePic);
        String uid = currentUser.getUid();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Glide.with(this)
                .load("http://i.imgur.com/DvpvklR.png")
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .centerCrop()
                .into(imageView); */

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        // imageView.setImageURI(Uri.parse(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl("http://i.imgur.com/DvpvklR.png")));
        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(ImagePickActivity.getPickImageChooserIntent(getActivity(), "profile"), PROPIC_REQUEST);
            }
        });
        super.onViewCreated(view, savedInstanceState);
        exit = getView().findViewById(R.id.logout);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences("login", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("notSignedIn", true);
                editor.apply();

                AuthUI.getInstance()
                        .signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                List<AuthUI.IdpConfig> providers = Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build(),
                                        new AuthUI.IdpConfig.AnonymousBuilder().build());

                                // Create and launch sign-in intent
                                getActivity().startActivityForResult(
                                        AuthUI.getInstance()
                                                .createSignInIntentBuilder()
                                                .setIsSmartLockEnabled(false)
                                                .setAvailableProviders(providers)
                                                .build(),
                                        RC_SIGN_IN);
                            }
                        });


                /*
                Toast.makeText(getContext(), "Utente disconnesso!", Toast.LENGTH_SHORT).show();
                Intent i= new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivityForResult(i, LOGIN_REQUEST);
                */
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.i("PICUPLOADPROFILE", "RESULT MAYBE");
        if (requestCode == PROPIC_REQUEST && resultCode == getActivity().RESULT_OK) {
            Log.i("PICUPLOADPROFILE", "RESULT OK");
            if (ImagePickActivity.getPickImageResultUri(getActivity(), intent, "profile") != null) {
                //Prendi l'uri assegnato alla cache
                previewUri = ImagePickActivity.getPickImageResultUri(getActivity(), intent, "profile");
                Glide.with(getContext())
                        .load(previewUri)
                        .centerCrop()
                        .into(imagePreview);
            } else {
                Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
                Glide.with(getContext())
                        .load(bitmap)
                        .centerCrop()
                        .into(imagePreview);
            }
        }
    }

}
