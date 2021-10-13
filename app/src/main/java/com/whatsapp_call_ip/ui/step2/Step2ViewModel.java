package com.whatsapp_call_ip.ui.step2;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Step2ViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public Step2ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Step 2");
    }

    public LiveData<String> getText() {
        return mText;
    }
}