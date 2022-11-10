package com.example.tu_basura_vale.ui.Informacion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InformacionViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public InformacionViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
