package com.example.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;


public class MyService extends Service {

    public int counter = 0;
    public int version;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        System.out.println("myservice started");
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("myservice started");
        version=getVersionCode(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("myservice started");
        startTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();


        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 10000, 10000); //
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {
                System.out.println(downloadText());
                //  Log.i("in timer", "in timer ++++  "+ (counter++));
                if(Double.parseDouble(downloadText())>version){

                  /*  Intent updateIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://some-public-url/deploy/MyApplication.apk"));
                    startActivity(updateIntent);*/
                    System.out.println("Version is Greater");
                }
            }
        };
    }

    private String downloadText() {
       /* try {
            String line;
            URL url = new URL("https://www.w3.org/TR/PNG/iso_8859-1.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(in.readLine());
            }
            in.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }*/
       // System.out.println("done");
        int BUFFER_SIZE = 20000000;
        InputStream in = null;
        try {
            in = openHttpConnection();
        } catch (IOException e1) {
            return e1.getMessage()+"hghh";
        }

        String str = "";
        if (in != null) {
            InputStreamReader isr = new InputStreamReader(in);
            int charRead;
            char[] inputBuffer = new char[BUFFER_SIZE];
            try {
                while ((charRead = isr.read(inputBuffer)) > 0) {
                    // ---convert the chars to a String---
                    String readString = String.copyValueOf(inputBuffer, 0, charRead);
                    str += readString;
                    inputBuffer = new char[BUFFER_SIZE];
                }
                in.close();
            } catch (IOException e) {
                return "wronge";
            }
        }
        return str;
    }

    private InputStream openHttpConnection() throws IOException {
        InputStream in = null;
        int response = -1;

        URL url = new URL("https://dl.dropboxusercontent.com/s/cflv4ubx601luqb/myversion.txt?dl=0");
        URLConnection conn = url.openConnection();

        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");

        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();

            }
        } catch (Exception ex) {
            throw new IOException();
        }
        System.out.println("Done");
        return in;
    }
    public int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }


}
