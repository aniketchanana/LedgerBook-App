package com.aniket.myledgerbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aniket.myledgerbook.adapter.adapter;
import com.aniket.myledgerbook.models.Customer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import com.aniket.myledgerbook.models.records;
import com.aniket.myledgerbook.adapter.*;
public class CustomerviewHome extends AppCompatActivity {
    String name,phone,pending,id;
    Button watsapp,call,message;
    TextView t1,t2;
    RecyclerView recyclerviewforrecords;
    DatabaseReference db;
    ArrayList<records> allrecords = new ArrayList<>();
    recordadapter myrecordadapter;
    FloatingActionButton incomingCash,outgoingCash;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerview_home);
        t1 = findViewById(R.id.customerHomeName);
        t2 = findViewById(R.id.customerHomePhone);

        watsapp = findViewById(R.id.watsApp);
        call = findViewById(R.id.call);
        message = findViewById(R.id.message);

        incomingCash = findViewById(R.id.incomingcash);
        outgoingCash = findViewById(R.id.outgoingcash);
        mAuth = FirebaseAuth.getInstance();
        String email = mAuth.getCurrentUser().getEmail();
        email = email.replace("@","");
        email = email.replace(".","");

        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        pending = getIntent().getStringExtra("pending");
        id = getIntent().getStringExtra("id");
        t1.setText(Character.toUpperCase(name.charAt(0)) + name.substring(1));
        t2.setText(phone);
        db = FirebaseDatabase.getInstance().getReference("records").child(email).child(id);

        recyclerviewforrecords = findViewById(R.id.recyclerviewforrecords);
        recyclerviewforrecords.setLayoutManager(new LinearLayoutManager(this));


        watsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_VIEW);
                sendIntent.setPackage("com.whatsapp");
                String url = "https://api.whatsapp.com/send?phone=+91"+phone+ "&text=" + "Hello this message is to inform you " +
                         "that you have " + pending + " as pending balance please pay it as soon as possible";
                sendIntent.setData(Uri.parse(url));
                startActivity(sendIntent);
            }
        });

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(CustomerviewHome.this,new String[]{Manifest.permission.SEND_SMS},10);
                }
                else
                {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("sms:"+phone));
                    i.putExtra("sms_body","Hello this message is to inform you " +
                            "that you have " + pending+ "rs as pending balance please pay it as soon as possible");
                    startActivity(i);
                }
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED)
                {

                    ActivityCompat.requestPermissions(CustomerviewHome.this,new String[]{Manifest.permission.CALL_PHONE},10);
                }
                else
                {
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_DIAL);
                    i.setData(Uri.parse("tel:"+phone));
                    startActivity(i);
                }
            }
        });
//        getfromdb();

        incomingCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subtract();
            }
        });

        outgoingCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });
    }
    void add(){
        Intent i = new Intent(CustomerviewHome.this,outgoingcash.class);
        i.putExtra("id",id);
        startActivity(i);
    }
    void subtract(){
        Intent i = new Intent(CustomerviewHome.this,incomingcash.class);
        i.putExtra("id",id);
        startActivity(i);
    }

    void getfromdb(){
        allrecords.clear();
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){
                    records rec = dataSnapshot1.getValue(records.class);
                    allrecords.add(rec);
                    recyclerviewforrecords.setHasFixedSize(true);
                }
                myrecordadapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        myrecordadapter = new recordadapter(allrecords,CustomerviewHome.this);
        recyclerviewforrecords.setAdapter(myrecordadapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getfromdb();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
