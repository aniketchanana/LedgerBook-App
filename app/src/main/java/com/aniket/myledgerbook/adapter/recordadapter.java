package com.aniket.myledgerbook.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aniket.myledgerbook.R;
import com.aniket.myledgerbook.models.*;
import java.util.ArrayList;

public class recordadapter extends RecyclerView.Adapter<recordadapter.viewholder> {
    Context context;
    ArrayList<records> array;
    public recordadapter(ArrayList<records> array,Context context){
        this.array = array;
        this.context = context;
    }
    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recordview,parent,false);
        return new viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
       holder.serialnum.setText(array.get(position).getId().substring(0,array.get(position).getId().indexOf(" ")));
       holder.money.setText(array.get(position).getMoney() + "Rs");
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class viewholder extends RecyclerView.ViewHolder{
        TextView serialnum,money;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            serialnum = itemView.findViewById(R.id.serialnumber);
            money = itemView.findViewById(R.id.money);
        }
    }
}
