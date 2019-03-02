package com.example.optimizedatabase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.optimizedatabase.common.LongThread;
import com.example.optimizedatabase.dataobject.FileDO;
import com.example.optimizedatabase.roomdatabase.DataBaseClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements Handler.Callback{

    private Handler handler;
    TextView tvStatus;
    int curCount = 0;
    ProgressBar progressBar;
    String url1 = "YOUR_IMAGE_URL1";
    String url2 = "YOUR_IMAGE_URL2";
    float totalCount = 26F;
    private List<FileDO> list = new ArrayList<>();

    private Long startTime=0l,endTime=0l;

    private TextView tv_exe_time;

    private List<FileDO> storeDataFromDataBase;

    private Button  reDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = (TextView) findViewById(R.id.tvDownloadCount);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_exe_time =(TextView)findViewById(R.id.tv_exe_time);
        reDownload =  findViewById(R.id.button);
        triggerDownloadWhenInternetAvaiblae();

        reDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                triggerDownloadWhenInternetAvaiblae();
            }
        });


    }

    public boolean isNetworkConnectionAvailable(Context context) {

        boolean isNetworkConnectionAvailable = false;
        // checking the Internet availability
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null)
            isNetworkConnectionAvailable = activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED;

        return isNetworkConnectionAvailable;
    }


    public void triggerDownloadWhenInternetAvaiblae(){


        if(!isNetworkConnectionAvailable(this)){
            Toast.makeText(this,"Please check the internet connectivty",Toast.LENGTH_LONG).show();
            reDownload.setVisibility(View.VISIBLE);
        }else{
            reDownload.setVisibility(View.GONE);
            downloadData();

        }
    }


    public void downloadData(){

        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                NUMBER_OF_CORES * 2,
                NUMBER_OF_CORES * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()
        );



        int count=0;
        for(char c = 'a'; c <= 'z'; ++c){
            count++;
            StringBuilder strUrl = new StringBuilder();
            strUrl.append("http://www.mso.anu.edu.au/~ralph/OPTED/v003/wb1913_").append(c).append(".html");
            FileDO fileDO = new FileDO();
            fileDO.setFileName(c+"");
            fileDO.setFileServerPath(strUrl.toString());
            executor.execute(new LongThread(count, fileDO, new Handler(this)));
        }
        tvStatus.setText("Starting Download...");
    }

    @Override
    public boolean handleMessage(Message msg) {
        curCount++;
        float per = (curCount / totalCount) * 100;
        progressBar.setProgress((int) per);
        list.add((FileDO) msg.obj);
        if (per < 100){
            tvStatus.setText("Downloaded [" + curCount + "/" + (int)totalCount + "]");
        }
        else{
            tvStatus.setText("All Files downloaded.");
            insertintoDataBase();
        }
        return true;
    }


    public void insertintoDataBase(){
        Toast.makeText(this,"Now Insert into table",Toast.LENGTH_LONG).show();
        InsertData st = new InsertData();
        st.execute();
    }

    public class InsertData extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

             startTime = System.currentTimeMillis();
            //adding to database
            DataBaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .fileDao()
                    .insert(list);

           storeDataFromDataBase =  DataBaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .fileDao()
                    .getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            endTime = System.currentTimeMillis();
            Long duration = endTime - startTime;
            double Sec = duration/1000.0;
            tv_exe_time.setText("Exec_time : "+(endTime-startTime)+"milliseonds \n Seconds : "+ Sec);
        }
    }
}
