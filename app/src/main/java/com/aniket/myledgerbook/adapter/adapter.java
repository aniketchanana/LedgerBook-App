package com.aniket.myledgerbook.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aniket.myledgerbook.CustomerviewHome;
import com.aniket.myledgerbook.models.*;
import com.aniket.myledgerbook.R;

import java.util.ArrayList;

public class adapter extends RecyclerView.Adapter<adapter.viewholder> {
    Context context;
    ArrayList<Customer> array;

    public adapter(ArrayList<Customer> array,Context context){
        this.array = array;
        this.context = context;
    }
    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customerdebitview,parent,false);
        return new viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        holder.name.setText(Character.toUpperCase(array.get(position).name.charAt(0)) + array.get(position).name.substring(1));
        holder.phone.setText(array.get(position).phone);
        holder.pending.setText(String.valueOf(array.get(position).pending)+"Rs");
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class viewholder extends RecyclerView.ViewHolder{
        TextView name;
        TextView phone;
        TextView pending;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.card_name);
            phone = itemView.findViewById(R.id.card_phone);
            pending = itemView.findViewById(R.id.card_pending);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Customer cust = array.get(getAdapterPosition());
                    Intent i = new Intent(context, CustomerviewHome.class);
                    i.putExtra("id",cust.id);
                    i.putExtra("name",cust.name);
                    i.putExtra("pending",String.valueOf(cust.pending));
                    i.putExtra("phone",cust.phone);
                    context.startActivity(i);
                }
            });
        }
    }
}
