package com.aniket.myledgerbook;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.aniket.myledgerbook.adapter.adapter;
import com.aniket.myledgerbook.models.*;

import java.util.ArrayList;

public class CustomersFragment extends Fragment {
    TextView allrecords,pendingrecords;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FloatingActionButton addNewCustomer;
    RecyclerView recyclerView;
    DatabaseReference dbreference = FirebaseDatabase.getInstance().getReference();
    ArrayList<Customer> allcustomers = new ArrayList<>();
    String email;
    RecyclerView.LayoutManager lmanager = new LinearLayoutManager(getActivity());
    TextView acc_name;
    adapter myadapter;
    public CustomersFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customers, container, false);
        addNewCustomer = v.findViewById(R.id.add_new);
        recyclerView = v.findViewById(R.id.recyclerView_display_customers);
        acc_name = v.findViewById(R.id.account_name);

        myadapter = new adapter(allcustomers,getActivity());
        recyclerView.setLayoutManager(lmanager);
        recyclerView.setAdapter(myadapter);

        allrecords = v.findViewById(R.id.allrecords);
        pendingrecords = v.findViewById(R.id.pendingrecords);

        email = mAuth.getCurrentUser().getEmail();
        email = email.replace(".","");
        email = email.replace("@","");

        setaccName();
        setview();


        addNewCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),new_customer.class));
            }
        });
        allrecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allrecords.setBackgroundColor(Color.parseColor("#272c3c"));
                pendingrecords.setBackgroundColor(Color.parseColor("#434857"));
                setview();
            }
        });
        pendingrecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pendingrecords.setBackgroundColor(Color.parseColor("#272c3c"));
                allrecords.setBackgroundColor(Color.parseColor("#434857"));
                setFilteredView();
            }
        });
        return v;
    }
    void setFilteredView(){
        dbreference = FirebaseDatabase.getInstance().getReference("customers").child(email);
        dbreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allcustomers.clear();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Customer cust = dataSnapshot1.getValue(Customer.class);
                    if(cust.pending > 0){
                        allcustomers.add(cust);
                    }
                    recyclerView.setHasFixedSize(true);
                }

                myadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void setview(){
        dbreference = FirebaseDatabase.getInstance().getReference("customers").child(email);
        dbreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allcustomers.clear();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Customer cust = dataSnapshot1.getValue(Customer.class);
                    allcustomers.add(cust);
                    recyclerView.setHasFixedSize(true);
                }

                myadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    void setaccName(){
        DatabaseReference dbreference1 = FirebaseDatabase.getInstance().getReference("users").child(email);
        dbreference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User new_user = dataSnapshot.getValue(User.class);
                String a = new_user.getAccountName();
                acc_name.setText(Character.toUpperCase(a.charAt(0)) + a.substring(1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
