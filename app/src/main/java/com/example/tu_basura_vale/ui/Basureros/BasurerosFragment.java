package com.example.tu_basura_vale.ui.Basureros;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tu_basura_vale.R;
import com.example.tu_basura_vale.databinding.FragmentBasurerosBinding;
import com.example.tu_basura_vale.databinding.FragmentBasurerosBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class BasurerosFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;

    private FragmentBasurerosBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BasurerosViewModel basurerosViewModel =
                new ViewModelProvider(this).get(BasurerosViewModel.class);

        binding = FragmentBasurerosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng BV_ARC = new LatLng(19.166347, -96.113695);
        mMap.addMarker(new MarkerOptions().position(BV_ARC).title("Tu Basura Vale"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(BV_ARC));

        LatLng CJPII = new LatLng(19.159666, -96.111182);
        mMap.addMarker(new MarkerOptions().position(CJPII).title("Tu Basura Vale"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CJPII));

    }
}