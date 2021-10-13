package com.whatsapp_call_ip;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

public class StopCaptureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_capture);
        Application app = ((Application) this.getApplication());
        Button init_capture = (Button) findViewById(R.id.init_capture);

        KillTcpdumpThread killTcpdumpThread = new KillTcpdumpThread(app);
        killTcpdumpThread.start();
        showDialog();
    }

    private void showDialog(){
        StopCaptureActivity thisAct = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_Transparent);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                thisAct.finish();
            }
        });
        builder.setTitle("Finished monitoring");
        builder.setMessage("Click on View Results at Main Screen");
        AlertDialog dialog = builder.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#FF0B8B42"));
        positiveButton.setBackgroundResource(R.drawable.dialog_border);
    }
}