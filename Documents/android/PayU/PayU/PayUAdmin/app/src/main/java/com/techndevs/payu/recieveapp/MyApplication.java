package com.techndevs.payu.recieveapp;

import android.app.Application;

import com.techndevs.payu.recieveapp.Broadcast.ConnectivityReciever;

public class MyApplication extends Application {
    public static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance=this;
    }

    public static synchronized MyApplication getInstance(){
        return mInstance;
    }


    public void setConnectivtyListner(ConnectivityReciever.ConnectivityRecieverListner listner){
        ConnectivityReciever.connectivityReceiverListener = listner;
    }
}
