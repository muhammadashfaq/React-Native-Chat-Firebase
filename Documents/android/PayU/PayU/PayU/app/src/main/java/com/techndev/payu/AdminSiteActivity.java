package com.techndev.payu;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.techndev.payu.Contants.BaseUrl;
import com.techndev.payu.Service.PingService;

public class AdminSiteActivity extends AppCompatActivity {
    Intent mServiceIntent;
    private PingService pingService;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_site);


        Context context=getApplicationContext();
        UpdateToServer serverClass=new UpdateToServer(context);
        serverClass.addtoServer(context);


       pingService = new PingService(this);
       mServiceIntent = new Intent(this, PingService.class);
       if (!isMyServiceRunning(PingService.class)) {
            startService(mServiceIntent);
       }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    public void btnWebsiteClick(View view) {

        if (BaseUrl.isConnectedtoInternet(this)) {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("url",getResources().getString(R.string.WebsiteUrl));
            startActivity(intent);
        }else{
            Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
        }


    }

    public void btnAdminClick(View view) {
        if(BaseUrl.isConnectedtoInternet(this)){
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("url",getResources().getString(R.string.AdminUrl));
            startActivity(intent);
        }else {
            Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_SHORT).show();
        }

    }
}
