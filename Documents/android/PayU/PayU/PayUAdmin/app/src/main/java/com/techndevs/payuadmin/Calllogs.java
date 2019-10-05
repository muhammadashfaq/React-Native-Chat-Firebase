package com.example.muhammadashfaq.recieveapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.muhammadashfaq.recieveapp.Adapter.CartAdapter;
import com.example.muhammadashfaq.recieveapp.Constants.BaseUrl;
import com.github.ybq.android.spinkit.SpinKitView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Calllogs extends AppCompatActivity {

    RecyclerView recyclerView;
    SpinKitView spinKitView;
    String data,device_name;
    ArrayList list;
    CartAdapter cartAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calllogs);
        Toolbar toolbar=findViewById(R.id.toolbar_new_call);
        recyclerView=findViewById(R.id.recyler_view_calllogs);
        spinKitView=findViewById(R.id.spin_kit_chatss_activity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Call Logs");

        if(getIntent()!=null){
           device_name=getIntent().getStringExtra("device_name");
        }


       list=new ArrayList();

        spinKitView.setVisibility(View.VISIBLE);
        startThread();


    }

    private ArrayList startThread() {
        Thread mThread= new Thread(){
            public void run(){
                super.run();
                try {
                    Thread.sleep(1000);
                    trimCache(Calllogs.this);
                    String url = BaseUrl.baseUrl + getResources().getString(R.string.get_last_callLogs);
                    StringRequest mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            spinKitView.setVisibility(View.GONE);
                            data=response;
                            list.add(response);
                            //Toast.makeText(getApplicationContext(), list.toString(), Toast.LENGTH_SHORT).show();
                            cartAdapter= new CartAdapter(list,R.layout.recyler_item,Calllogs.this);
                            recyclerView.setLayoutManager(new LinearLayoutManager(Calllogs.this));
                            recyclerView.setAdapter(cartAdapter);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            spinKitView.setVisibility(View.GONE);
                            Toast.makeText(Calllogs.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            HashMap<String,String> params=new HashMap<>();
                            Log.i("phone",device_name);
                            params.put("phone",device_name);

                            return params;

                        }
                    };

                    RequestQueue mRequestQueue = Volley.newRequestQueue(Calllogs.this);
                    mRequestQueue.add( mStringRequest);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        mThread.start();

        return list;

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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.calllog_menu,menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.refresh_calllog){
            spinKitView.setVisibility(View.VISIBLE);
            startThread();
        }
        return true;
    }
}
