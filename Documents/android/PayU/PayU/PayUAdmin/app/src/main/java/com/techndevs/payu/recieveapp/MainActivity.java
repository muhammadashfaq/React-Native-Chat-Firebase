package com.techndevs.payu.recieveapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Common.api_device_name = android.os.Build.MODEL;


        startSplash();

    }


    private void startSplash() {
        Thread mThread= new Thread(){
            public void run(){
                super.run();
                try {
                    Thread.sleep(3000);
                    Intent mIntent=new Intent(MainActivity.this,IMEIActivity.class);
                    startActivity(mIntent);
                    finish();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        mThread.start();
    }

}
