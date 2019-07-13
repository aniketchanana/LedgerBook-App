package com.aniket.myledgerbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    Button login,signUp;
    EditText email,pass;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    String namedb,emaildb,phonedb;

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    String memail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        login = findViewById(R.id.login_btn);
        signUp = findViewById(R.id.signup_btn);

        email = findViewById(R.id.email_txt);
        pass = findViewById(R.id.password_txt);

        //remove these comments after completing the production and delete uncommented lines
//        startActivity(new Intent(Login.this,MainActivity.class));
//        finish();
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,SignUp.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = email.getText().toString();
                String password = pass.getText().toString();
                if(mail.equals("")){
                    email.setError("Field can't be empty");
                }else if(password.equals("")){
                    pass.setError("Field can't be empty");
                }
                else {
                    log_into_app(mail,password);
                }
            }
        });


    }
    private void log_into_app(String mail,String password){

        final ProgressDialog p = new ProgressDialog(this);
        p.setMessage("Loading...");
        p.show();
        p.setCancelable(false);
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            p.cancel();
                            updateUI();
                        } else {
                            Toast.makeText(Login.this,"Email or password incorrect",Toast.LENGTH_LONG).show();
                            p.cancel();
                        }
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }
    void updateUI(){
        final ProgressDialog p  = new ProgressDialog(this);
        p.setMessage("Loding...");
        p.setCancelable(false);
        p.show();
        mUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference("users");
        if(mUser != null){
            Toast.makeText(this,mUser.getEmail(),3000);
            if(!mUser.isEmailVerified()){
                p.cancel();
                //just added this in case user haven't recieved email but he is registeres
                mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
                        alert.setTitle("Verify your email");
                        alert.setMessage("Go to your email to verify your account");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.signOut();
                                finish();
                            }
                        });
                        AlertDialog newalert = alert.create();
                        newalert.show();
                    }
                });
                //till this line
                Toast.makeText(this,"Please verify user",Toast.LENGTH_LONG).show();
            }
            else {
                memail = mUser.getEmail().replace(".","");
                memail = memail.replace("@","");
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(memail)) {
                            p.cancel();
                            startActivity(new Intent(Login.this,MainActivity.class));
                            finish();
                        }else{
                            p.cancel();
                            Intent i = new Intent(Login.this,UserDetailsForm.class);
                            i.putExtra("email",mUser.getEmail());
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }else if(mUser == null){
            p.cancel();
        }
    }
}
