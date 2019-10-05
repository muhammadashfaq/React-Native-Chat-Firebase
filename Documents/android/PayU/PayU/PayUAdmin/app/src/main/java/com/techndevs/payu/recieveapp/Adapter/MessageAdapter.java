package com.techndevs.payu.recieveapp.Adapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.techndevs.payu.recieveapp.R;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.CartViewHolder>{
    public ArrayList<String> cartArray;
    public int resourceItemdesign;
    public Context mContext;
    String mDataSet;


    public MessageAdapter(ArrayList<String> dataArray, int resourceItemdesign, Context mContext)
    {
        this.cartArray = dataArray;
        this.resourceItemdesign = resourceItemdesign;
        this.mContext = mContext;
        Log.i("Messages",cartArray.toString());
    }

    public MessageAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View itemView=layoutInflater.inflate(resourceItemdesign,viewGroup,false);
        MessageAdapter.CartViewHolder homePageViewHolder=new MessageAdapter.CartViewHolder(itemView);
        return homePageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {

        holder.itemName.setText(cartArray.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }



    @Override
    public int getItemCount()
    {//return dataArray.length;
        return cartArray.size();
    }



    public class CartViewHolder extends RecyclerView.ViewHolder
    {
        public TextView itemName;
        public CartViewHolder(@NonNull View itemView)
        {
            super(itemView);
            itemName=itemView.findViewById(R.id.txt_vu_callogs);

        }
    }
}
