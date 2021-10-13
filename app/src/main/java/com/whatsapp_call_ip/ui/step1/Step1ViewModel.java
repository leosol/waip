package com.whatsapp_call_ip.ui.step1;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Step1ViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public Step1ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Step 1");
    }

    public LiveData<String> getText() {
        return mText;
    }
}