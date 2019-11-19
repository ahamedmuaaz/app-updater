package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    private Button testBtn;
    private TextView testTxt;
    private TextView testTxt1;
    public Context c;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This has been used to forcefully use network operations on main thread (Only to Read File version number)
        //for downloading apk file this is not needed
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //register broadcast receiver for downloading apk file
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


        testBtn = findViewById(R.id.btn_test);
        testTxt = findViewById(R.id.textView);
        testTxt.setText("1");

        //get current version of the app
        final int version = getVersionCode(this);

        //get the context of app
        c = this;

        //Request for access file storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int count = Integer.parseInt(testTxt.getText().toString());
                count++;
                progressDialog = ProgressDialog.show(MainActivity.this,
                        "ProgressDialog",
                        "Please Wait for few seconds");

                // stopService(intent);
                testTxt.setText(String.valueOf(count));

                System.out.println("clicked");
                Double recentVersion=Double.valueOf(downloadText());
                if(version<recentVersion){

                DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse("https://dl.dropbox.com/s/n8qnsinao1p2ymv/app-release.apk?dl=0");

                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle("My File");
                request.setDescription("Downloading");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "app-release.apk");

                downloadmanager.enqueue(request);
                }


              /*  AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute();*/


            }
        });


    }

    //broadcast receiver to execute download via download manager and install apk
    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            //System.out.println(result);
            progressDialog.dismiss();
            File directory = Environment.getExternalStorageDirectory();

            File file = new File(directory, "/Download/app-release.apk"); // assume refers to "sdcard/myapp_folder/myapp.apk"


            Uri fileUri = Uri.fromFile(file); //for Build.VERSION.SDK_INT <= 24

            if (Build.VERSION.SDK_INT >= 24) {

                fileUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
            }
            intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //dont forget add this line
            c.startActivity(intent);
        }
    };

    //method to get current version of the app
    public int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {
        }
        return 0;
    }

     //method to read latest version of the app
    private String downloadText() {
        try {

            URL url = new URL("https://dl.dropboxusercontent.com/s/cflv4ubx601luqb/myversion.txt?dl=0");
            BufferedReader read = new BufferedReader(
                    new InputStreamReader(url.openStream()));
            String i;
            String new_Version = "0";
            while ((i = read.readLine()) != null)
                new_Version = i;
            read.close();
            return new_Version;

        } catch (Exception ex) {
            System.out.println(ex);
            return "0";
        }

    }


}





