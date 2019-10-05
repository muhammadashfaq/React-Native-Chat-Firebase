package com.example.muhammadashfaq.recieveapp.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.example.muhammadashfaq.recieveapp.Common;
import com.example.muhammadashfaq.recieveapp.MyApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ConnectivityReciever extends BroadcastReceiver {

    public static ConnectivityRecieverListner connectivityReceiverListener;
    String msg_from,msgBody;


    @Override
    public void onReceive(final Context context, Intent intent) {

        Log.i("Tag",intent.getAction());
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Log.i("Tag","Message Recieved");
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;

            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        msgBody = msgs[i].getMessageBody();
                    }

                    boolean hasLowerCase=checkLowerCase(msgBody);

                    if(hasLowerCase){
                        Log.i("TAG","Invalid Message " + msgBody);
                    }else{
                        Log.i("TAG","ONLY UPPERCASE" + msgBody);
                         Common.devicesList.add(msgBody);
                         Common.senderNumber=msg_from;
                         Common.MESSAGE_DEVICE_NAME=msgBody;
                         sendDataToFirebase(context);
                    }



                } catch (Exception e) {
                    Log.d("Exception caught",e.getMessage());
                }


            }
        }





        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }

    private void sendDataToFirebase( final Context context) {

                    HashMap hashMap=new HashMap();
                    hashMap.put("phonenumber",msg_from);
                    Common.devicesRef.child(msgBody).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(context, "Device "+ msgBody+ " Registerd", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }

    private boolean checkLowerCase(String msgBody) {
        boolean hasLowerCase= false;

        for(int i=0 ;i<msgBody.length();i++){
            char ch=msgBody.charAt(i);
            if(Character.isLowerCase(ch)){
                hasLowerCase=true;
            }else{
                hasLowerCase= false;
            }
        }
        return  hasLowerCase;
    }

    public static boolean isConnected(){
        ConnectivityManager
                cm = (ConnectivityManager) MyApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    public static interface ConnectivityRecieverListner {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
