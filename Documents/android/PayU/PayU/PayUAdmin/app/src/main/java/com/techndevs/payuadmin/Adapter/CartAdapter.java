package com.example.muhammadashfaq.recieveapp.Adapter;
import android.content.Context;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.muhammadashfaq.recieveapp.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{
    public ArrayList cartArray;
    public int resourceItemdesign;
    public Context mContext;
    String mDataSet;


    public CartAdapter(ArrayList dataArray, int resourceItemdesign, Context mContext)
    {
        CartAdapter.this.cartArray = dataArray;
        this.resourceItemdesign = resourceItemdesign;
        this.mContext = mContext;
        Log.i("Calls",cartArray.toString());
    }

    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View itemView=layoutInflater.inflate(resourceItemdesign,viewGroup,false);
        CartAdapter.CartViewHolder homePageViewHolder=new CartAdapter.CartViewHolder(itemView);
        return homePageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {


        holder.itemName.setText(cartArray.get(position).toString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void remove(int position) {
        cartArray.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,cartArray.size());
        notifyDataSetChanged();
        Toast.makeText(mContext, "Item Deleted", Toast.LENGTH_LONG).show();
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
