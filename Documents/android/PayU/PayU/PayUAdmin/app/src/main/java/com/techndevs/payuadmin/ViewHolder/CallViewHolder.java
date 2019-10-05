package com.example.muhammadashfaq.recieveapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.muhammadashfaq.recieveapp.R;

public class CallViewHolder extends RecyclerView.ViewHolder {


    public TextView textViewCalllogs;
    public CallViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewCalllogs=itemView.findViewById(R.id.txt_vu_callogs);
    }
}
