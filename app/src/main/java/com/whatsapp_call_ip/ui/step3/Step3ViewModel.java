package com.whatsapp_call_ip.ui.step3;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Step3ViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public Step3ViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Step 3");
    }

    public LiveData<String> getText() {
        return mText;
    }
}