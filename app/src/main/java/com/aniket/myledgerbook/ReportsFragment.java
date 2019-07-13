package com.aniket.myledgerbook;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aniket.myledgerbook.adapter.reportsadapter;
import com.aniket.myledgerbook.models.Customer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import com.aniket.myledgerbook.models.*;
/**
 * A simple {@link Fragment} subclass.
 */
public class ReportsFragment extends Fragment {


    public ReportsFragment() {
        // Required empty public constructor
    }

    RecyclerView r;
    ArrayList<report> array = new ArrayList<>();
    reportsadapter ra;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reports, container, false);
        r = v.findViewById(R.id.reportsrecyclerview);
        ra = new reportsadapter(array,getActivity());
        getFromdb();
        return v;
    }

    void getFromdb(){
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        email = email.replace(".","");
        email = email.replace("@","");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("customers").child(email);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    Customer cust = dataSnapshot1.getValue(Customer.class);
                    report rp = new report(cust.name,cust.id);
                    array.add(rp);
                    r.setHasFixedSize(true);
                    ra.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        r.setLayoutManager(new LinearLayoutManager(getActivity()));
        r.setAdapter(ra);
    }

}
