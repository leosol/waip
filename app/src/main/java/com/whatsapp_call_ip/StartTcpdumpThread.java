package com.whatsapp_call_ip;

import android.content.DialogInterface;
import android.graphics.Color;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import java.io.DataOutputStream;
import java.io.IOException;

public class StartTcpdumpThread extends Thread {

    private Application app;

    public StartTcpdumpThread(Application app){
        this.app = app;
    }

    public void run() {
        try {
            this.app.setCapturing(true);
            this.app.truncateLog();
            this.app.startTcpdump();
        }catch(IOException e){
            throw new RuntimeException(e);
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        } finally {
            this.app.setCapturing(false);
        }
    }
}
