package com.techndevs.payu.recieveapp.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.techndevs.payu.recieveapp.R;

public class CallViewHolder extends RecyclerView.ViewHolder {


    public TextView textViewCalllogs;
    public CallViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewCalllogs=itemView.findViewById(R.id.txt_vu_callogs);
    }
}
