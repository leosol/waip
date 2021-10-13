package com.whatsapp_call_ip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class ViewCaptureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_capture);
        updateText();
    }

    public void updateText() {
        TextInputEditText capture_result1 = (TextInputEditText) findViewById(R.id.capture_result1);
        TextInputEditText capture_result2 = (TextInputEditText) findViewById(R.id.capture_result2);
        StringBuilder text1 = new StringBuilder("First filter:\n");
        Utils.read_text_file("/storage/emulated/0/.capture1.view", text1);
        capture_result1.setText(text1.toString());
        StringBuilder text2 = new StringBuilder("Second filter:\n");
        Utils.read_text_file("/storage/emulated/0/.capture2.view", text2);
        capture_result2.setText(text2.toString());
    }

    public void returnToMain(View view) {
        this.finish();
    }


}