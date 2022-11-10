package com.example.tu_basura_vale.fragments;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tu_basura_vale.BuildConfig;
import com.example.tu_basura_vale.MainActivity2;
import com.example.tu_basura_vale.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment {
    public static final String GOOGLE_ID_CLIENT_TOKEN = BuildConfig.GOOGLE_ID_CLIENT_TOKEN;
    public static final int GOOGLE_SIGNIN_REQUEST_CODE = 1001;
    private GoogleSignInClient gmsClient;
    private FirebaseAuth auth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate (R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance ();

        EditText edtEmail = view.findViewById (R.id.etEmailLogin);
        EditText edtPassword = view.findViewById (R.id.etPasswordlLogin);

        Button btnLogin = view.findViewById (R.id.btnLogin);
        btnLogin.setOnClickListener (v -> {
            login (edtEmail.getText ().toString (), edtPassword.getText().toString ());
        });

        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
        builder.requestIdToken(GOOGLE_ID_CLIENT_TOKEN);
        builder.requestEmail();
        GoogleSignInOptions gso = builder
                .build ();

        gmsClient = GoogleSignIn.getClient (getActivity(), gso);


        Button btnSG = view.findViewById (R.id.btn_SingGoogle);
        btnSG.setOnClickListener (v -> {
            showGoogleSignInView ();
        });


    }



    private void login (String email, String password) {
        auth.signInWithEmailAndPassword (email, password)
                .addOnCompleteListener (task -> {
                    if (task.isSuccessful ()) {
                        FirebaseUser user = auth.getCurrentUser ();
                        String name = "Encontrado ";

                        if (user != null) {
                            name = user.getDisplayName ();
                        }

                        Toast.makeText (getActivity (), "Usuario " + name, Toast.LENGTH_LONG).show ();
                        Intent dashboardActivity=new Intent(getActivity(), MainActivity2.class);
                    } else {
                        Toast.makeText (getActivity (), "Usuario y/o contraseña no reconocida!", Toast.LENGTH_LONG).show ();
                    }
                });
    }



    private void showGoogleSignInView () {
        auth = FirebaseAuth.getInstance ();

        Intent intent = gmsClient.getSignInIntent ();
        myActivityResultLauncher.launch (intent);
        //startActivityForResult (intent, GOOGLE_SIGNIN_REQUEST_CODE);
    }

    ActivityResultLauncher<Intent> myActivityResultLauncher = registerForActivityResult (
            new ActivityResultContracts.StartActivityForResult (),
            result -> {
                if (result.getResultCode() == GOOGLE_SIGNIN_REQUEST_CODE) {
                    // if (result.getData() == null) return;
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent (result.getData ());

                    GoogleSignInAccount account = task.getResult();
                    if (account != null) firebaseAuthWithGoogleServices (account);
                }
            }
    );


    private void firebaseAuthWithGoogleServices (GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential (account.getIdToken (), null);
        auth.signInWithCredential (credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful ()) {
                        Toast.makeText (getActivity(), "Google SignIn Successful!", Toast.LENGTH_LONG).show ();
                    } else {
                        Toast.makeText (getActivity(),
                                "SignIn with Google services failed with exception " +
                                        (task.getException () != null ? task.getException().getMessage () : "None"),
                                Toast.LENGTH_LONG).show ();
                    }
                });
    }
}
