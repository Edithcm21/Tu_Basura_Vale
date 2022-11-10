package com.example.tu_basura_vale.ui.Basureros;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BasurerosViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public BasurerosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Basureros fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}