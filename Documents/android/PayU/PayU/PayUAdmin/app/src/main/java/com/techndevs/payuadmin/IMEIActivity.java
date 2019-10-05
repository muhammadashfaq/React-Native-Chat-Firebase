package com.example.muhammadashfaq.recieveapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.muhammadashfaq.recieveapp.Adapter.CartAdapter;
import com.example.muhammadashfaq.recieveapp.Adapter.IMEIAdapter;
import com.example.muhammadashfaq.recieveapp.Broadcast.ConnectivityReciever;
import com.example.muhammadashfaq.recieveapp.Constants.BaseUrl;
import com.example.muhammadashfaq.recieveapp.Model.IMEINModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IMEIActivity extends AppCompatActivity implements ConnectivityReciever.ConnectivityRecieverListner {



    LinearLayout linearLayoutNetworkStatus;
    TextView txtVuHeadingNetwork, txtVuCountry;
    IMEIAdapter imeiAdapter;
    ProgressDialog progressDialog;

    ArrayList<IMEINModel> listJson;

    RecyclerView recyclerViewIMEI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imei);

        recyclerViewIMEI=findViewById(R.id.recyler_view);
        linearLayoutNetworkStatus=findViewById(R.id.linearNetworkStatus);
        txtVuHeadingNetwork=findViewById(R.id.tv_heading);
        txtVuCountry=findViewById(R.id.tv_country);
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait for a litte while");
        progressDialog.setCancelable(false);



        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewIMEI.setLayoutManager(linearLayoutManager) ;

        registerForContextMenu(recyclerViewIMEI);
        startThread();


    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Toast.makeText(this, "ahye", Toast.LENGTH_SHORT).show();
        if(item.getTitle().equals(Common.DELETE)){
                deleteMobile();
        }
        return true;
    }

    private void deleteMobile()
    {
        Thread mThread= new Thread(){
            public void run(){
                super.run();
                try {
                    Thread.sleep(1000);
                    trimCache(IMEIActivity.this);
                    StringRequest mStringRequest = new StringRequest(Request.Method.POST, "http://rfbasolutions.com/get_messages_api/delete_mobile_data.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if(response.equals("true")){
                                Toast.makeText(IMEIActivity.this, "Mobile Deleleted Successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(IMEIActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            HashMap<String,String> params=new HashMap<>();
                            Log.i("device",Common.device_name);
                            params.put("device_name",Common.device_name);

                            return params;

                        }
                    };

                    RequestQueue mRequestQueue = Volley.newRequestQueue(IMEIActivity.this);
                    mRequestQueue.add( mStringRequest);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        mThread.start();

    }

    private void startThread() {
        progressDialog.show();
        Thread mThread= new Thread(){
            public void run(){
                super.run();
                try {
                    Thread.sleep(1000);
                    trimCache(IMEIActivity.this);
                    String url = BaseUrl.baseUrl + getResources().getString(R.string.get_mobile_info);
                    StringRequest mStringRequest = new StringRequest(1, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            Log.i("Response",response);
                            try {
                                listJson=new ArrayList<IMEINModel>();
                                JSONObject jsonObject=new JSONObject(response);
                                JSONArray array=jsonObject.getJSONArray("result");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    IMEINModel imeinModel = new IMEINModel();
                                    imeinModel.setLog_device_name(object.getString("log_device_name"));
                                    listJson.add(imeinModel);
                                }

                                imeiAdapter= new IMEIAdapter(IMEIActivity.this,listJson,R.layout.recyler_imei_item_desing);
                                recyclerViewIMEI.setLayoutManager(new LinearLayoutManager(IMEIActivity.this));

                                recyclerViewIMEI.setAdapter(imeiAdapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            progressDialog.dismiss();
                            Log.i("Error",error.toString());
                            Toast.makeText(IMEIActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {


                            return null;

                        }
                    };

                    RequestQueue mRequestQueue = Volley.newRequestQueue(IMEIActivity.this);
                    mRequestQueue.add( mStringRequest);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        mThread.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivtyListner(this);
        boolean isConnected=ConnectivityReciever.isConnected();
        checkConnection(isConnected);
    }

    private void checkConnection(boolean isConnected) {
        if(isConnected){
            linearLayoutNetworkStatus.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            txtVuHeadingNetwork.setText("Connected");
        }else{
            linearLayoutNetworkStatus.setBackgroundColor(getResources().getColor(R.color.colorRed));
            txtVuHeadingNetwork.setText("Disconnected");
        }
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

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkConnection(isConnected);
    }
}
