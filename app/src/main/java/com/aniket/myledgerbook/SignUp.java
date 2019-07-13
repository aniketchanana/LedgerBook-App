package com.aniket.myledgerbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("");
    Button create_account;
    EditText user_email,user_password,user_password_confirm;
    String regex = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
    String regexpass = "^(?=.*[A-Z].*[A-Z])(?=.*[!@#$&*])(?=.*[0-9].*[0-9])(?=.*[a-z].*[a-z].*[a-z]).{8}$";
    String name,email,phone,password,passwordConfirm;


    final int GALLERYPICK = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        create_account = findViewById(R.id.create_account);
        user_email = findViewById(R.id.user_email);
        user_password = findViewById(R.id.user_password);
        user_password_confirm = findViewById(R.id.user_password_confirm);

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewAccount();
            }
        });
    }

    private void createNewAccount(){
        if(!verifyDetails()){
            return;
        }
        final ProgressDialog p = new ProgressDialog(this);
        p.setMessage("Signing Up...");
        p.show();
        p.setCancelable(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            p.cancel();
                            sendVerificationLink();
                        } else {
                            p.cancel();
                            Toast.makeText(SignUp.this, "already having a user",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    void sendVerificationLink(){
        mUser = mAuth.getCurrentUser();
        mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SignUp.this);
                alert.setTitle("Verify your email");
                alert.setMessage("Go to your email to verify your account");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAuth.signOut();
                        startActivity(new Intent(SignUp.this,Login.class));
                        finish();
                    }
                });
                AlertDialog newalert = alert.create();
                newalert.show();
            }
        });
    }
    void emptyalllfields(){
        user_email.setText("");
        user_password_confirm.setText("");
        user_password.setText("");
    }
    private Boolean verifyDetails(){
        //pending password strong weak confirmation
        email = user_email.getText().toString();
        password = user_password.getText().toString();
        passwordConfirm = user_password_confirm.getText().toString();

        if(email.equals("") || password.equals("") || passwordConfirm.equals("")){
            Toast.makeText(this,"All fields are compulsory",Toast.LENGTH_LONG).show();
            return false;
        }
        if(!(password.length()>=8)){
            //give user notice
            user_password.setError("Min 8 characters required");
            return false;
        }
        if(!email.matches(regex)){
            //give user notice
            user_email.setError("Please enter a valid email address");
            return false;
        }
        if(!password.equals(passwordConfirm)){
            user_password.setError("Both fiels should have same value");
            user_password_confirm.setError("Both fiels should have same value");
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
