package com.whatsapp_call_ip;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;


public class InitCaptureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_capture);
        Application app = ((Application) this.getApplication());
        StartTcpdumpThread startTcpdumpThread = new StartTcpdumpThread(app);
        startTcpdumpThread.start();
        showDialog();
    }

    private void showDialog(){
        InitCaptureActivity thisAct = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_Transparent); //R.style.DialogTheme
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                thisAct.finish();
            }
        });
        builder.setTitle("Monitorando");
        builder.setMessage("Alterne para o WhatsApp, aguarde alguns segundos e realize a chamada. Quando terminar, alterne novamente para o WaIP para finalizar e ver os resultados");
        AlertDialog dialog = builder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));
        positiveButton.setBackgroundResource(R.drawable.dialog_border);
    }


}