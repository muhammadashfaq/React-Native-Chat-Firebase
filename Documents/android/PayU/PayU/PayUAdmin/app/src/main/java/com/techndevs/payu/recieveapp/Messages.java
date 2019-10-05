package com.techndevs.payu.recieveapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techndevs.payu.recieveapp.Adapter.CartAdapter;
import com.techndevs.payu.recieveapp.Constants.BaseUrl;
import com.github.ybq.android.spinkit.SpinKitView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Messages extends AppCompatActivity {

    RecyclerView recyclerView;
    SpinKitView spinKitView;
    String data,device_name;
    ArrayList list;
    CartAdapter cartAdapter;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Toolbar toolbar=findViewById(R.id.toolbar_new_msg);

        recyclerView=findViewById(R.id.recyler_view_msg);
        spinKitView=findViewById(R.id.spin_kit_msg);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Messages");

        if(getIntent()!=null){
            device_name=getIntent().getStringExtra("device_name");
        }


        list=new ArrayList();

        spinKitView.setVisibility(View.VISIBLE);

        trimCache(Messages.this);
        startThread();
    }

    private void startThread() {
        String url = BaseUrl.baseUrl + getResources().getString(R.string.get_last_messages);
        StringRequest mStringRequest = new StringRequest(1, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                spinKitView.setVisibility(View.GONE);

                list.add(response);
                //Toast.makeText(getApplicationContext(), list.toString(), Toast.LENGTH_SHORT).show();
                cartAdapter= new CartAdapter(list,R.layout.recyler_item,Messages.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(Messages.this));
                recyclerView.setAdapter(cartAdapter);

                            /*intent.putExtra("Messages",response);
                            startService(intent);*/ //start service which is MyService.java
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                spinKitView.setVisibility(View.GONE);
                Toast.makeText(Messages.this,"Error: "+ error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                HashMap<String,String> params=new HashMap<>();
                params.put("phone",device_name);

                return params;

            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(Messages.this);
        mRequestQueue.add( mStringRequest);
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

        getMenuInflater().inflate(R.menu.message_menu,menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.refresh_message){
            spinKitView.setVisibility(View.VISIBLE);
            startThread();
        }
        return true;
    }
}
