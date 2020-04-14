package it.tpt.cookingbayapp;

import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {

    private Button exit;
    private Button switch_account;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ImageView profilePic;
    private Uri profileUri;
    private TextView username;
    private final static int PROPIC_REQUEST = 239;
    public static final int LOGIN_REQUEST = 101;

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
        profilePic = view.findViewById(R.id.userProfilePic);
        String uid = currentUser.getUid();
        username = view.findViewById(R.id.textUsername);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {

        final View layout = view.findViewById(R.id.profileCoordinatorLayout);

        super.onViewCreated(view, savedInstanceState);
        exit = getView().findViewById(R.id.logout);
        switch_account = getView().findViewById(R.id.cambia_account);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(!user.isAnonymous()) {
                    startActivityForResult(ImagePickActivity.getPickImageChooserIntent(getActivity(), "profile"), PROPIC_REQUEST);
                } else  Snackbar.make(layout, R.string.profilepicanonymous, Snackbar.LENGTH_LONG).show();
            }
        });
        switch_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user.isAnonymous()) {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            getActivity().startActivityForResult(intent, LOGIN_REQUEST);
                        }
                    });
                } else {
                    AuthUI.getInstance()
                            .signOut(getActivity())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    getActivity().startActivityForResult(intent, LOGIN_REQUEST);
                                }
                            });
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (!user.isAnonymous()) {
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
                                                        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Cooking Bay");
                                                        Snackbar.make(layout, R.string.logged_as_anonymous, Snackbar.LENGTH_SHORT).show();
                                                    } else {
                                                        // If sign in fails, display a message to the user.
                                                        Log.w("signinerror", "signInAnonymously:failure", task.getException());
                                                        Toast.makeText((Context) getActivity(), R.string.exiterror, Toast.LENGTH_SHORT).show();
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


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PROPIC_REQUEST && resultCode == getActivity().RESULT_OK) {
            Log.i("PICUPLOADPROFILE", "RESULT OK");
            if (ImagePickActivity.getPickImageResultUri(getActivity(), intent, "profile") != null) {
                //Prendi l'uri assegnato alla cache
                profileUri = ImagePickActivity.getPickImageResultUri(getActivity(), intent, "profile");
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
    }

}
