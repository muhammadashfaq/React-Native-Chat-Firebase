package com.example.muhammadashfaq.recieveapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.muhammadashfaq.recieveapp.Common;
import com.example.muhammadashfaq.recieveapp.HomeActivity;
import com.example.muhammadashfaq.recieveapp.Interface.ItemClickListner;
import com.example.muhammadashfaq.recieveapp.Model.IMEINModel;
import com.example.muhammadashfaq.recieveapp.R;

import java.util.ArrayList;

public class IMEIAdapter extends RecyclerView.Adapter<IMEIAdapter.IMEIViewHolder> {

    Context context;
    ArrayList<IMEINModel> list;
    String mobileModel;
    int recyler_item_desing;
    LayoutInflater inflater;

    public IMEIAdapter(Context context, ArrayList<IMEINModel> list, int recyler_imei_item_desing){
        this.context=context;
        this.list=list;
        this.recyler_item_desing=recyler_imei_item_desing;
        this.inflater=LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public IMEIViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView=inflater.inflate(recyler_item_desing,parent,false);
        IMEIAdapter.IMEIViewHolder imeiViewHolder=new IMEIAdapter.IMEIViewHolder(itemView);
        return imeiViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IMEIViewHolder holder, final int position) {

            holder.btnName.setText(list.get(position).getLog_device_name());
            Common.device_name=list.get(position).getLog_device_name();

            holder.setItemClickListnerFood(new ItemClickListner() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                }
            });

            holder.btnName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent=new Intent(context, HomeActivity.class);
                    intent.putExtra("device_name",list.get(position).getLog_device_name());
                    context.startActivity(intent);

                }
            });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class IMEIViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
            ,View.OnCreateContextMenuListener
    {
        public Button btnName;
        private ItemClickListner itemClickListnerFood;

        public IMEIViewHolder(@NonNull View itemView) {
            super(itemView);
            btnName=itemView.findViewById(R.id.btn_mobile_model_number);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(this);
        }




        public void setItemClickListnerFood(ItemClickListner itemClickListnerFood) {
            this.itemClickListnerFood = itemClickListnerFood;
        }
        @Override
        public void onClick(View v) {
            itemClickListnerFood.onClick(v,getAdapterPosition(),false);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            Toast.makeText(context, "cotextMenu", Toast.LENGTH_SHORT).show();
            menu.setHeaderTitle("Delete Mobile:");
            menu.add(0,0,getAdapterPosition(), Common.DELETE);
            menu.add(0,1,getAdapterPosition(), "UPDATE");
        }
    }
}
