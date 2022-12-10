package com.example.tu_basura_vale.ui.Inicio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tu_basura_vale.Canje;
import com.example.tu_basura_vale.MainActivity;
import com.example.tu_basura_vale.User;
import com.example.tu_basura_vale.databinding.FragmentInicioBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class InicioFragment extends Fragment {

    TextView txtQR;
    EditText pCanjear;
    Button aceptar;
    private FragmentInicioBinding binding;
    User user=new User();
    String idUser;
    //Variables para la BD
    FirebaseAuth firebaseAuth;
    Canje canjeC;
    int puntosCanjear;
    private DatabaseReference songs;
    private static final String BASE_DATABASE_REFERENCE="Usuarios";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        InicioViewModel inicioViewModel = new ViewModelProvider(this).get(InicioViewModel.class);


        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        txtQR = binding.txtQR;



        //aqui inicia el codigo de escaner

        binding.fab.setOnClickListener(v -> {
            IntentIntegrator integrador=new IntentIntegrator(getActivity());
            integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrador.setPrompt("Lector QR");
            integrador.setCameraId(0);
            integrador.setBeepEnabled(true);
            integrador.setBarcodeImageEnabled(true);
            integrador.initiateScan();


        });
        //Aqui termina


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser userFB = firebaseAuth.getCurrentUser();
        idUser=userFB.getUid();
        getUsers();

        Button canje=binding.btnCanje;
        pCanjear=binding.puntoscanje;

        LinearLayout Canje=binding.myContainer;
        canje.setOnClickListener(v ->{
                Canje.setVisibility(View.VISIBLE);

                pCanjear.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(s.length()!=0){

                            puntosCanjear= Integer.parseInt(s.toString());
                            System.out.println("Se cambkiaram "+puntosCanjear+" puntos  afterTextChanged////////////*/******////////////****//*//*/**/*/*//*/*/*/**/");
                            if(verificarPuntos(puntosCanjear))
                            {
                                aceptar.setEnabled(true);

                            }
                            else
                                aceptar.setEnabled(false);

                        }else{

                            puntosCanjear=0;

                        }



                    }
                });

        });

        aceptar=binding.btnAceptar;
        LinearLayout c_aceptar=binding.caceptar;
        aceptar.setOnClickListener(v->{
            user.totalpuntos=user.totalpuntos-puntosCanjear;
            System.out.println("Total de puntos a canjear "+puntosCanjear+"Total de puntos restantes"+user.totalpuntos);
            ActualizarUsuario(user);
            c_aceptar.setVisibility(View.VISIBLE);
        });


        inicioViewModel.getText().observe(getViewLifecycleOwner(), text -> txtQR.setText(text));
        return root;

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    //Obtiene los valores de la bd
    private void getUsers () {
        DatabaseReference ref;
        ref = FirebaseDatabase.getInstance().getReference();
        // Agregamos un listener a la referencia
        ref.child("Usuarios").child(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    user =dataSnapshot.getValue(User.class);
                    txtQR.setText(String.valueOf(user.totalpuntos));
                    pCanjear.setText(String.valueOf(user.totalpuntos));
                    puntosCanjear=user.totalpuntos;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }

    public Boolean verificarPuntos(int puntos){
        int puntosD=user.totalpuntos;
        if(puntosD>=puntos){
            return true;
        }
        return false;

    }

    private void ActualizarUsuario(User user) {

        HashMap<String, Object> entry = new HashMap<> ();
        entry.put(user.id,user);


        songs=FirebaseDatabase.getInstance().getReference();
        songs.child("Usuarios").updateChildren(entry).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(),"Se han actualizado correctamente los datos",Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Se ha producido un error al actualizar la BD",Toast.LENGTH_LONG).show();
            }
        });


    }





}