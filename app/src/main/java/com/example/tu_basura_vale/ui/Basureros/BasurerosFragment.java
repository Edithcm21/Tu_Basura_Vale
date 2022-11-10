package com.example.tu_basura_vale.ui.Basureros;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.tu_basura_vale.databinding.FragmentBasurerosBinding;
import com.example.tu_basura_vale.databinding.FragmentBasurerosBinding;

public class BasurerosFragment extends Fragment {

    private FragmentBasurerosBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BasurerosViewModel basurerosViewModel =
                new ViewModelProvider(this).get(BasurerosViewModel.class);

        binding = FragmentBasurerosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textBasureros;
        basurerosViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}