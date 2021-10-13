package com.whatsapp_call_ip;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    public static final String INIT_CAPTURE = "com.whatsapp_call_ip.INIT_CAPTURE";
    public static final String STOP_CAPTURE = "com.whatsapp_call_ip.STOP_CAPTURE";
    public static final String VIEW_CAPTURE = "com.whatsapp_call_ip.VIEW_CAPTURE";
    public static final int PERMISSION_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            String dataDir = getApplicationInfo().dataDir;
            install_resource(dataDir, R.raw.busybox, "busybox");
            install_resource(dataDir, R.raw.tcpdump, "tcpdump");
            install_resource(dataDir, R.raw.tshark, "tshark");
            DataOutputStream intall_proc_out;
            if(true) {
                Process install_proc = Runtime.getRuntime().exec("su");
                intall_proc_out = new DataOutputStream(install_proc.getOutputStream());
                String file_name = dataDir + "/busybox";
                intall_proc_out.writeBytes("cp -n " + file_name + " /sbin/\n");
                intall_proc_out.flush();
                intall_proc_out.writeBytes("chmod +x /sbin/busybox\n");
                intall_proc_out.flush();
                intall_proc_out.writeBytes("chmod o+r -R /data/data/com.whatsapp\n");
                intall_proc_out.flush();
                intall_proc_out.writeBytes("exit\n");
                intall_proc_out.flush();
                install_proc.waitFor();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Application app = ((Application) this.getApplication());
            app.setMissingResources(true);
        }

        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE},
                0);
    }

    public void install_resource(String location, int res, String dest) throws FileNotFoundException, IOException {
        String file_name = location + "/" + dest;
        File file = new File(file_name);
        if (file.exists()) {
            return;
        }
        InputStream in = getResources().openRawResource(res);
        FileOutputStream out = new FileOutputStream(file_name);
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
            out.close();
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        this.updateButtons();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.updateButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.updateButtons();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.updateButtons();
    }

    public void updateButtons() {
        Application app = ((Application) this.getApplication());
        Button init_capture = (Button) findViewById(R.id.init_capture);
        Button stop_capture = (Button) findViewById(R.id.stop_capture);
        Button view_capture = (Button) findViewById(R.id.view_capture);
        if (!app.getCapturing()) {
            if (app.getCaptured()) {
                init_capture.setText("Capture again?");
            } else {
                init_capture.setText("Start capture");
            }
        } else {
            init_capture.setText("Capturing...");
        }
        if (app.getCapturing()) {
            stop_capture.setEnabled(true);
            stop_capture.setBackgroundResource(R.color.red_btn);
        } else {
            stop_capture.setEnabled(false);
            stop_capture.setBackgroundResource(R.color.disabled_btn);
        }
        if (app.getSavedLog()) {
            view_capture.setEnabled(true);
            view_capture.setBackgroundResource(R.color.blue_btn);
        } else {
            view_capture.setEnabled(false);
            view_capture.setBackgroundResource(R.color.disabled_btn);
        }
    }

    public void initCapture(View view) {
        Intent intent = new Intent(this, InitCaptureActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void stopCapture(View view) {
        Intent intent = new Intent(this, StopCaptureActivity.class);
        startActivity(intent);
    }

    public void viewCapture(View view) {
        Intent intent = new Intent(this, ViewCaptureActivity.class);
        startActivity(intent);
    }

    public boolean viewAuthor(MenuItem item) {
        Intent intent = new Intent(this, AuthorActivity.class);
        startActivity(intent);
        return true;
    }

    public void viewInstructions(View view) {
        Intent intent = new Intent(this, InstructionsWizardActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.imenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ibutton) {
            // do something here
        }
        return super.onOptionsItemSelected(item);
    }
}