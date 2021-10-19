package com.whatsapp_call_ip;

import android.widget.Button;

public class KillTcpdumpThread extends Thread {

    private Application app;
    public KillTcpdumpThread(Application app){
        this.app = app;
    }

    public void run() {
        this.app.setCapturing(false);
        this.app.setCaptured(true);
        this.app.setSavedLog(true);
        this.app.stopTcpdump();
        try {
            this.app.parseWhatsAppLog();
            this.app.parsePcap();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
