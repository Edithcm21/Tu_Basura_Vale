package com.example.tu_basura_vale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.network.UpdateMetadataNetworkRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;

public class Registro extends AppCompatActivity {
    private FirebaseAuth auth;
    private Snackbar snackbar;
    private ImageView ivProfilePic;
    private EditText edtName, edtLastName, edtAge, edtAddress, edtTelephone;

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference songs;
    private RelativeLayout root;


    private static final int SELECT_IMAGE_REQUEST_CODE = 2001;
    private static final String BASE_STORAGE_REFERENCE = "images";
    private static final String BASE_DATABASE_REFERENCE = "Usuarios";
    User user;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        auth = FirebaseAuth.getInstance ();
        database = FirebaseDatabase.getInstance ();
        storage = FirebaseStorage.getInstance ();

        root = findViewById (R.id.root);
        EditText edtEmail = findViewById (R.id.etEmailRegister);
        EditText edtPassword = findViewById (R.id.etPasswordlRegister);
        Button btnChangePic = findViewById (R.id.btnChangePic);
        btnChangePic.setOnClickListener (view -> selectImage ());



        ivProfilePic = findViewById (R.id.ivProfilePic);

        edtName = findViewById (R.id.edtName);
        edtLastName = findViewById (R.id.edtlastName);
        edtAge = findViewById (R.id.edtAge);
        edtAddress = findViewById (R.id.edtAddress);
        edtTelephone = findViewById (R.id.edtTelephone);

        Button btnRegister = findViewById (R.id.btn_Register);
        btnRegister.setOnClickListener (v -> {
            snackbar = Snackbar.make (root, "Guardando...", Snackbar.LENGTH_INDEFINITE);
            ViewGroup layer = (ViewGroup) snackbar.getView ().findViewById (com.google.android.material.R.id.snackbar_text).getParent ();
            ProgressBar bar = new ProgressBar (getBaseContext ());
            layer.addView (bar);
            snackbar.show ();
            registerUser (edtEmail.getText().toString (), edtPassword.getText().toString ());
        });
    }



    private void registerUser (String email, String password) {
        auth.createUserWithEmailAndPassword (email, password)
                .addOnCompleteListener (task -> {
                    if (task.isSuccessful ()) {
                        //Toast.makeText (this, "Register completed!", Toast.LENGTH_LONG).show ();
                        FirebaseUser userF = auth.getCurrentUser();

                        saveInfo (userF.getUid());


                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(user.nombre+" " + user.apellidos)
                                //.setPhotoUri(dlUrl)
                                .build();
                        userF.updateProfile(profileUpdates);

                        Intent mainActivity = new Intent(Registro.this, MainActivity.class);
                        startActivity(mainActivity);
                        Registro.this.finish();
                    } else {
                        if (task.getException () != null) {
                            Log.e("TYAM", task.getException().getMessage());
                        }

                       Toast.makeText (this, "Register failed!", Toast.LENGTH_LONG).show ();
                    }
                });
    }


    private void saveInfo (String ID) {
        songs = database.getReference (BASE_DATABASE_REFERENCE);

        user       = new User ();
        user.nombre     = edtName.getText().toString ();
        user.apellidos  = edtLastName.getText().toString ();
        user.edad       = Integer.parseInt (edtAge.getText().toString ());
        user.direccion  = edtAddress.getText().toString ();
        user.telefono   = edtTelephone.getText().toString ();
        user.totalpuntos=0;
        user.id=ID;



        Bitmap bitmap = getBitmapFromDrawable (ivProfilePic.getDrawable ());
        ByteArrayOutputStream bos = new ByteArrayOutputStream ();
        bitmap.compress (Bitmap.CompressFormat.JPEG, 100, bos);
        byte [] data = bos.toByteArray ();

        try {
            bos.close();
        } catch (IOException ex) {
            if (ex.getMessage () != null) {
                Log.e ("Tu Basura Vale ", ex.getMessage ());
                return;
            }

            Log.e ("Tu Basura Vale", "Error getting bytearray...", ex);
        }

        //almacena la imagen  en storage
        String fileReferece = String.format(Locale.US, "%s/%s_%s_%d.jpg",
                BASE_STORAGE_REFERENCE, user.nombre, user.apellidos, System.currentTimeMillis());

        StorageReference images = storage.getReference (fileReferece);
        images.putBytes (data)
                .addOnCompleteListener (task -> {
                    if (task.isComplete ()) {
                        Task<Uri> dlUrlTask = images.getDownloadUrl ();

                        dlUrlTask.addOnCompleteListener (task1 -> {
                            Uri dlUrl = task1.getResult();
                            if (dlUrl == null) return;

                            user.foto = dlUrl.toString ();
                            doSave (user);
                        });
                    }
                })
                .addOnFailureListener (e -> {
                    Log.e ("Tu Basura Vale", e.getMessage ());
                });
    }

    private void doSave (User user) {
        String nodeId = user.id;
                //calculateStringHash (user.toString ());
        HashMap<String, Object> entry = new HashMap<> ();
        entry.put (nodeId, user);

        songs.updateChildren (entry)
                .addOnSuccessListener (aVoid -> {
                    snackbar.dismiss ();
                    Snackbar.make (root, "Información almacenada!", Snackbar.LENGTH_LONG).show ();
                })
                .addOnFailureListener (e -> Toast.makeText (getBaseContext (),
                        "Error actualizando la BD: " + e.getMessage (),
                        Toast.LENGTH_LONG).show ());

    }


    private void selectImage () {
        Intent intent = new Intent (Intent.ACTION_PICK);
        intent.setType ("image/*");

        String [] mimeTypes = { "image/jpeg", "image/png" };
        intent.putExtra (Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult (intent, SELECT_IMAGE_REQUEST_CODE);
    }

    /**
     * Obtiene un objeto de mapa de bits a partir del objeto Drawable (canvas) recibido.
     *
     * @param drble Drawable que contiene la imagen deseada.
     * @return objeto de mapa de bits con la estructura de la imagen.
     */
    private Bitmap getBitmapFromDrawable (Drawable drble) {
        // debido a la forma que el sistema dibuja una imagen en un el sistema gráfico
        // es necearios realzar comprobaciones para saber del tipo de objeto Drawable
        // con que se está trabajando.
        //
        // si el objeto recibido es del tipo BitmapDrawable no se requieren más conversiones
        if (drble instanceof BitmapDrawable) {
            return  ((BitmapDrawable) drble).getBitmap ();
        }

        // en caso contrario, se crea un nuevo objeto Bitmap a partir del contenido
        // del objeto Drawable
        Bitmap bitmap = Bitmap.createBitmap (drble.getIntrinsicWidth (), drble.getIntrinsicHeight (), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drble.setBounds (0, 0, canvas.getWidth (), canvas.getHeight ());
        drble.draw (canvas);

        return bitmap;
    }


    @Override
    public void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data == null) return;

            Uri uri = data.getData ();
            ivProfilePic.setImageURI (uri);
        }

        super.onActivityResult (requestCode, resultCode, data);
    }
}
