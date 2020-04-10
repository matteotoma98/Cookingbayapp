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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

import it.tpt.cookingbayapp.stepRecycler.Step;


public class ProfileFragment extends Fragment {

    private static final int RESULT_OK = 10 ;
    private Button exit;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView imageView;
    private Uri previewUri;
    public static final int LOGIN_REQUEST = 101;
    public static final int RC_SIGN_IN = 105;
    private final static int PREVIEW_REQUEST = 234;
    private boolean isUploading;
    public ProfileFragment(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile,container,false);
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        imageView = view.findViewById(R.id.cardProfilePic);
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

}
