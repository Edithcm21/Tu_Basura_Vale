package com.example.tu_basura_vale.ui.Inicio;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tu_basura_vale.User;
import com.example.tu_basura_vale.databinding.FragmentInicioBinding;
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
    private FragmentInicioBinding binding;
    User user=new User();
    String idUser;
    //Variables para la BD
    FirebaseAuth firebaseAuth;
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
        //btnQR.setOnClickListener();

        //Aqui termina


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        idUser=user.getUid();
        System.out.println(idUser+"///////////////***********//////////*******///////****////*****////");
        getUsers();



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
                    System.out.println(user.totalpuntos+"+++++++++++++++++++++++++++++++");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Fallo la lectura: " + databaseError.getCode());
            }
        });
    }





}