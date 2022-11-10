package com.example.tu_basura_vale.ui.Configuracion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConfiguracionViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ConfiguracionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Configuracion fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
