package com.aniket.myledgerbook;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aniket.myledgerbook.models.Customer;
import com.aniket.myledgerbook.models.records;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class new_customer extends AppCompatActivity {
    EditText new_customer_name,new_customer_phone,new_customer_pending;
    Button add_new_customer_button;

    DatabaseReference db;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer);
        new_customer_name = findViewById(R.id.new_customer_name);
        new_customer_phone = findViewById(R.id.new_customer_phone);
        add_new_customer_button = findViewById(R.id.add_new_customer_button);
        new_customer_pending = findViewById(R.id.new_customer_pending);
        db = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        add_new_customer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = new_customer_name.getText().toString();
                String phone = new_customer_phone.getText().toString();
                String pending = new_customer_pending.getText().toString();

                if(name.equals("")){
                    new_customer_name.setError("Field canot be empty");
                }else if(phone.length()!=10){
                    new_customer_phone.setError("Please enter valid number");
                }else{
                    addNewCustToDatabase();
                }
            }
        });
    }
    void addNewCustToDatabase(){
        String email = mAuth.getCurrentUser().getEmail();
        email = email.replace(".","");
        email = email.replace("@","");
        String id = db.push().getKey();
        String name = new_customer_name.getText().toString().trim().toLowerCase();
        String phone = new_customer_phone.getText().toString().trim().toLowerCase();
        int pending = Integer.parseInt(new_customer_pending.getText().toString().trim());
        final ProgressDialog p = new ProgressDialog(this);
        p.setMessage("saving...");
        p.show();
        String id_ = db.push().getKey();
        Customer newCustomer = new Customer(name,phone,pending,id);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String transaction_id = dtf.format(now);
        transaction_id = transaction_id.replace("/","-");

        records newRecord = new records(String.valueOf(pending),transaction_id);

        db.child("records").child(email).child(id).child(id_).setValue(newRecord);
        db.child("customers").child(email).child(id).setValue(newCustomer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                p.cancel();
                finish();
            }
        });
    }
}
