package com.techndev.payu.Contants;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BaseUrl {


    public static  String baseUrl = "https://korzystna.com/mob_api/";
    public static String tag_string_req = "string_req";


    public static boolean isConnectedtoInternet(Context context)
    {
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager!=null){
            NetworkInfo[] networkInfo=connectivityManager.getAllNetworkInfo();
            if(networkInfo!=null){
                for (int i=0;i<networkInfo.length;i++){
                    if(networkInfo[i].getState()== NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }

        }
        return false;
    }
}
