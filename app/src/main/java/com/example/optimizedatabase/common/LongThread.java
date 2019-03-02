package com.example.optimizedatabase.common;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.optimizedatabase.dataobject.FileDO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class LongThread implements Runnable {

    private int threadNo;
    private Handler handler;
    private FileDO fileDO;

    public static final String TAG = "LongThread";

    public LongThread() {

    }

    public LongThread(int threadNo, FileDO fileDO, Handler handler) {
        this.threadNo = threadNo;
        this.handler = handler;
        this.fileDO = fileDO;
    }

    @Override
    public void run() {
        Log.i(TAG, "Starting Thread : " + threadNo);
        FileDO returnFileDO  = getHtmlFile(fileDO);
        sendMessage(threadNo, returnFileDO);
        Log.i(TAG, "Thread Completed " + threadNo);
    }

    public void sendMessage(int what, FileDO fileDO) {
        Message message = handler.obtainMessage(what, fileDO);
        message.sendToTarget();
    }

    private FileDO getHtmlFile(FileDO fileDO) {

        try {
            URL url = new URL(fileDO.getFileServerPath());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.connect();
            int statusCode = connection.getResponseCode();


            if (statusCode == 200) {
                InputStream input = connection.getInputStream();
                fileDO.setData(convertStreamToString(input));
                return fileDO;
            }else{
                return fileDO;
            }

        } catch (Exception e) {
            return fileDO;
        }

    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();

    }
}
