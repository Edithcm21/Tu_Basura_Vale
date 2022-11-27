package com.example.tu_basura_vale.ui.Inicio;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.tu_basura_vale.databinding.FragmentInicioBinding;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class InicioFragment extends Fragment {

    int totalPuntos=0;
    TextView txtQR;
    private FragmentInicioBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InicioViewModel inicioViewModel =
                new ViewModelProvider(this).get(InicioViewModel.class);

        binding = FragmentInicioBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        txtQR = binding.txtQR;
        Button btnQR =binding.btnQR;
        //aqui inicia el codigo de escaner

        btnQR.setOnClickListener(v -> {
            IntentIntegrator integrador=new IntentIntegrator(getActivity());
            integrador.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrador.setPrompt("Lector QR");
            integrador.setCameraId(0);
            integrador.setBeepEnabled(true);
            integrador.setBarcodeImageEnabled(true);
            integrador.initiateScan();

        });

        //Aqui termina

        inicioViewModel.getText().observe(getViewLifecycleOwner(), text -> txtQR.setText(text));
        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}