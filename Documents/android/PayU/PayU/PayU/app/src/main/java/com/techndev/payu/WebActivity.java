package com.techndev.payu;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.techndev.payu.Service.PingService;

public class WebActivity extends AppCompatActivity
{
    WebView webView;
    WebSettings webSettings;
    ProgressDialog progressDialog;
    Intent mServiceIntent;
    private PingService pingService;

    String url;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);


        if(getIntent() != null ){
            url = getIntent().getStringExtra("url");
        }

//        Context context=getApplicationContext();
//        UpdateToServer serverClass=new UpdateToServer(context);
//        serverClass.addtoServer(context);

//        pingService = new PingService(this);
//        mServiceIntent = new Intent(this, PingService.class);
//        if (!isMyServiceRunning(PingService.class)) {
//            startService(mServiceIntent);
//        }



        webView=findViewById(R.id.webview);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAllowFileAccess(true);
        if(url != null){
            startWebView(url);
        }


    }


    private void startWebView(final String url)
    {
        WebSettings settings = webView.getSettings();
        webView.setWebChromeClient(new WebChromeClient());

        settings.setJavaScriptEnabled(true);

        //webView1.setScrollBarStyle(webView1.SCROLLBARS_OUTSIDE_OVERLAY);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.show();

        webView.setWebViewClient(new WebViewClient()
        {
            public void onPageFinished(WebView view, String url)
            {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getBaseContext(), "Error:" + description, Toast.LENGTH_SHORT).show();

            }
        });
        webView.loadUrl(url);
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

}
