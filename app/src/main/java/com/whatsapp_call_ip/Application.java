package com.whatsapp_call_ip;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Application extends android.app.Application {

    private Boolean capturing = false;
    private Boolean captured = false;
    private Boolean savedLog = false;
    private Boolean missingResources = false;
    private Process tcpdump;
    private DataOutputStream tcpdump_out;
    private Process truncate_log;
    private DataOutputStream truncate_log_out;
    private Process increment_log;
    private DataOutputStream increment_log_out;
    private Process tshark;
    private DataOutputStream tshark_out;
    private Process forensic_pack;
    private DataOutputStream forensic_pack_out;
    private Integer maxCallId;
    public String TSHARK_BIN = "tshark";
    public String TCPDUMP_BIN = "tcpdump";
    public String BUSYBOX_BIN = "busybox";
    public String CHMOD_BIN = "chmod";
    public String SU_BIN = "su";
    public String FINAL_CAPTURE = "/sdcard/capture.pcap";
    public String RUN_LOG = "/sdcard/.log.txt";
    public String evidenceName = null;

    private String getTsharkBin() {
        String dataDir = getApplicationInfo().dataDir;
        String tshark_arm = dataDir + "/"+TSHARK_BIN;
        return tshark_arm;
    }

    private String getTcpdumpBin(){
        String dataDir = getApplicationInfo().dataDir;
        String tcpdump_arm = dataDir + "/" + TCPDUMP_BIN;
        return tcpdump_arm;
    }

    private String getBusyboxBin(){
        String dataDir = getApplicationInfo().dataDir;
        String busybox_arm = dataDir + "/" + BUSYBOX_BIN;
        return busybox_arm;
    }

    public void truncateLog() throws IOException, InterruptedException {
        truncate_log = Runtime.getRuntime().exec("su");
        truncate_log_out = new DataOutputStream(truncate_log.getOutputStream());
        truncate_log_out.writeBytes("truncate -s 0 /data/data/com.whatsapp/files/Logs/whatsapp.log\n");
        truncate_log_out.flush();
        truncate_log_out.writeBytes("exit\n");
        truncate_log_out.flush();
        this.truncate_log.waitFor();
    }

    public void parseWhatsAppLog() throws IOException, InterruptedException {
        increment_log = Runtime.getRuntime().exec("su");
        increment_log_out = new DataOutputStream(increment_log.getOutputStream());
        increment_log_out.writeBytes("grep -Eio 'Local:(.*), Remote:(.*), priority: 0x102' /data/data/com.whatsapp/files/Logs/whatsapp.log > /sdcard/.capture1.view\n");
        increment_log_out.flush();
        increment_log_out.writeBytes("grep -Eio 'Peer (.*) network medium type updated: (.*)' /data/data/com.whatsapp/files/Logs/whatsapp.log >> /sdcard/.capture1.view\n");
        increment_log_out.flush();
        increment_log_out.writeBytes("exit\n");
        increment_log_out.flush();
        this.increment_log.waitFor();
    }

    public void startTcpdump() throws IOException, InterruptedException {
        tcpdump = Runtime.getRuntime().exec("su");
        tcpdump_out = new DataOutputStream(tcpdump.getOutputStream());
        String tcpdump = getTcpdumpBin();
        tcpdump_out.writeBytes(CHMOD_BIN+" +x " + tcpdump + "\n");
        tcpdump_out.flush();
        tcpdump_out.writeBytes("ls -l " + tcpdump + " >> "+RUN_LOG+"\n");
        tcpdump_out.flush();
        tcpdump_out.writeBytes("echo " + tcpdump + " >> "+RUN_LOG+"\n");
        tcpdump_out.flush();
        tcpdump_out.writeBytes("rm "+FINAL_CAPTURE+" &\n");
        tcpdump_out.flush();
        tcpdump_out.writeBytes(tcpdump + " -i any -s 0 -w "+FINAL_CAPTURE+" &\n");
        tcpdump_out.flush();
        this.tcpdump.waitFor();
    }

    public void stopTcpdump() {
        try {
            tcpdump_out.close();
            tcpdump.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void parsePcap() throws IOException, InterruptedException {
        tshark = Runtime.getRuntime().exec("su");
        tshark_out = new DataOutputStream(tshark.getOutputStream());
        String tshark = getTsharkBin();
        tshark_out.writeBytes("ls -l " + tshark + " >> "+RUN_LOG+"\n");
        tshark_out.flush();
        tshark_out.writeBytes("echo " + tshark + " >> "+RUN_LOG+"\n");
        tshark_out.flush();
        tshark_out.writeBytes(CHMOD_BIN+" +x " + tshark + "\n");
        tshark_out.flush();
        tshark_out.writeBytes("echo " + tshark + " -r "+FINAL_CAPTURE+" stun TO /sdcard/.tmp1.txt >> "+RUN_LOG+"\n");
        tshark_out.flush();
        tshark_out.writeBytes(tshark + " -r "+FINAL_CAPTURE+" stun > /sdcard/.tmp1.txt\n");
        tshark_out.flush();
        tshark_out.writeBytes("grep 'Binding' /sdcard/.tmp1.txt > /sdcard/.tmp2.txt\n");
        tshark_out.flush();
        tshark_out.writeBytes("tr -s ' ' < /sdcard/.tmp2.txt > /sdcard/.tmp3.txt\n");
        tshark_out.flush();
        tshark_out.writeBytes("cut -d ' ' -f 3-5 < /sdcard/.tmp3.txt > /sdcard/.tmp4.txt\n");
        tshark_out.flush();
        tshark_out.writeBytes("sort -u < /sdcard/.tmp4.txt >> /sdcard/.capture1.view\n");
        tshark_out.flush();
        tshark_out.writeBytes(CHMOD_BIN+" 666 /sdcard/.capture1.view\n");
        tshark_out.flush();
        String query = makeTsharkQuery();
        String tshark_args = " -r "+FINAL_CAPTURE+" -o gui.column.format:\"Source\",\"%us\",\"source port\",\"%uS\",\"Destination\",\"%ud\",\"dest port\",\"%uD\" '"+query+"' ";
        tshark_out.writeBytes("echo " + tshark + tshark_args + " >> "+RUN_LOG+" \n");
        tshark_out.flush();
        tshark_out.writeBytes(tshark + tshark_args + " > /sdcard/.tmp5.txt \n");
        tshark_out.flush();
        tshark_out.writeBytes("sort -u < /sdcard/.tmp5.txt > /sdcard/.tmp6.txt\n");
        tshark_out.flush();
        String busybox_arm = getBusyboxBin();
        tshark_out.writeBytes("ls -l " + busybox_arm + " >> "+RUN_LOG+"\n");
        tshark_out.flush();
        tshark_out.writeBytes("echo " + busybox_arm + " >> "+RUN_LOG+"\n");
        tshark_out.flush();
        tshark_out.writeBytes(CHMOD_BIN+" +x " + busybox_arm + "\n");
        tshark_out.flush();
        String busybox_awk_args = " awk 'BEGIN{OFS=\"\"}{print $1,\":\",$2,\"  ->  \",$3,\":\",$4;}' < /sdcard/.tmp6.txt > /sdcard/.capture2.view";
        tshark_out.writeBytes(busybox_arm + busybox_awk_args + " \n");
        tshark_out.flush();
        tshark_out.writeBytes("rm /sdcard/.tmp1.txt /sdcard/.tmp2.txt /sdcard/.tmp3.txt /sdcard/.tmp4.txt /sdcard/.tmp5.txt /sdcard/.tmp6.txt\n");
        tshark_out.flush();

        tshark_out.writeBytes(CHMOD_BIN+" 666 /sdcard/.capture2.view\n");
        tshark_out.flush();
        tshark_out.writeBytes("exit\n");
        tcpdump_out.flush();
        this.tshark.waitFor();
    }



    private String makeTsharkQuery(){
        Set<String> ips = findIps();
        if(ips.size()==0){
            return "udp.port==3478||tcp.port==3478";
        }
        System.out.println(ips.toString());
        String myIp = Utils.getIPAddress(true);
        ips.remove(myIp);
        StringBuilder query = new StringBuilder();
        int pos = 0;
        for (String ipDest:ips) {
            String currQuery = getSrcDestQuery(myIp, ipDest, pos);
            query.append(currQuery);
            pos++;
        }
        return query.toString();
    }
    private String getSrcDestQuery(String src, String dst, int pos){
        if(pos==0) {
            return "(ip.src==" + src + "&&ip.dst==" + dst + ")";
        } else {
            return "||(ip.src==" + src + "&&ip.dst==" + dst + ")";
        }
    }

    public Set<String> findIps() {
        Set<String> ips = new TreeSet<>();
        String filePath = "/storage/emulated/0/.capture1.view";
        File file = new File(filePath);
        if(!file.exists()){
            return ips;
        }
        Utils.getIPAddress(true);
        try {
            if(file.canRead()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.indexOf("->") > 0) {
                        String[] line_parts = line.split("->");
                        for (int i = 0; i < line_parts.length; i++) {
                            String ip = line_parts[i].trim();
                            ips.add(ip);
                        }
                    }
                }
                br.close();
            }else{
                try {
                    File testFile = new File("/storage/emulated/0/.waip.test");
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(testFile));
                    outputStreamWriter.write("test test test");
                    outputStreamWriter.close();
                }
                catch (IOException e) {
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ips;
    }

    public Boolean getCapturing() {
        return capturing;
    }

    public void setCapturing(Boolean capturing) {
        this.capturing = capturing;
    }

    public Boolean getCaptured() {
        return captured;
    }

    public void setCaptured(Boolean captured) {
        this.captured = captured;
    }

    public Boolean getSavedLog() {
        return savedLog;
    }

    public void setSavedLog(Boolean savedLog) {
        this.savedLog = savedLog;
    }

    public Boolean getMissingResources() {
        return missingResources;
    }

    public void setMissingResources(Boolean missingResources) {
        this.missingResources = missingResources;
    }

    public String getEvidenceName(){
        return evidenceName;
    }

    public void clearEvidenceName(){
        this.evidenceName = null;
    }

    public boolean hasEvidenceName(){
        if(this.evidenceName!=null){
            return true;
        }else{
            return false;
        }
    }
}
