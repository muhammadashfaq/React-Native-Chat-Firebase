package com.techndevs.payu.recieveapp;

import com.techndevs.payu.recieveapp.Model.IMEINModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Common {
    public static ArrayList messageData;
    public static String senderNumber;
    public static String device_name;
    public static String api_device_name;
    public static String device_online_status;
    public static String DELETE= "DELETE";
    public static String MESSAGE_DEVICE_NAME;
    public static ArrayList<IMEINModel> tempp=new ArrayList();
    public static ArrayList<IMEINModel> listJson;
    public static ArrayList devicesList=new ArrayList();



    public static DatabaseReference devicesRef= FirebaseDatabase.getInstance().getReference("Devices");
}
