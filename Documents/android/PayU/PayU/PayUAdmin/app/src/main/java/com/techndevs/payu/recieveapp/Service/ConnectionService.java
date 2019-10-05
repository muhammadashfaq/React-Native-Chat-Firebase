package com.techndevs.payu.recieveapp.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techndevs.payu.recieveapp.Common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionService extends Service {

    public static final int notify = 30000;
    private Handler mHandler=new Handler();
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if(mTimer!=null){
            mTimer.cancel();
        }else{
            mTimer=new Timer();
            mTimer.scheduleAtFixedRate(new TimeDisplay(),0,notify);
        }


    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    startThread();
                }
            });

        }


    }

    private void startThread() {
        Thread mThread= new Thread(){
            public void run(){
                super.run();
                try {
                    Thread.sleep(1000);
                    trimCache(ConnectionService.this);
                    StringRequest mStringRequest = new StringRequest(1, "http://rfbasolutions.com/get_messages_api/get_online_status.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Response",response);
                            Toast.makeText(ConnectionService.this, response, Toast.LENGTH_SHORT).show();
                            if(response.equals("true")){
                                Common.device_online_status="true";
                            }else if(response.equals("false")){
                                Common.device_online_status="false";
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.i("Error",error.toString());
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {


                            HashMap<String,String> params=new HashMap<>();

                            if(Common.device_name!=null){
                                Log.i("device",Common.device_name);
                                params.put("device_name",Common.device_name);




                            }


                            params.put("ping","somthing");

                            return params;

                        }
                    };

                    RequestQueue mRequestQueue = Volley.newRequestQueue(ConnectionService.this);
                    mRequestQueue.add( mStringRequest);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        mThread.start();

    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {

        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

// The directory is now empty so delete it
        return dir.delete();
    }





}
