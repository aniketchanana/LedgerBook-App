package com.aniket.myledgerbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aniket.myledgerbook.models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class UserDetailsForm extends AppCompatActivity {
    String name,phone,email,accountName;
    EditText edit_name,edit_phone,edit_account;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    Button submit_btn,logout;

    FirebaseAuth mAuth;
    StorageReference mstorageref;
    Uri imageUri;
    ImageView imageupload;
    final int GALLERYPICK = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        mAuth = FirebaseAuth.getInstance();
        edit_name = findViewById(R.id.user_name);
        edit_phone = findViewById(R.id.user_phone);
        edit_account = findViewById(R.id.user_account);
        submit_btn = findViewById(R.id.submit_details);

        name = edit_name.getText().toString();
        email = getIntent().getStringExtra("email");
        phone = edit_phone.getText().toString();
        accountName = edit_account.getText().toString();
        mstorageref = FirebaseStorage.getInstance().getReference("uploads");

        logout = findViewById(R.id.logout_btn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(UserDetailsForm.this,Login.class));
                finish();
            }
        });

        imageupload = findViewById(R.id.imageupload);
        imageupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(allOk()){
                    uploadImageToStore();
                }
            }
        });
    }

    void uploadImageToStore(){
        final ProgressDialog p = new ProgressDialog(this);
        p.setMessage("Saving...");
        p.setCancelable(false);
        p.show();
        if(imageUri == null){
            Toast.makeText(this,"profile pic is compulsory",3000).show();
            p.cancel();
        }else {
            final StorageReference fileref = mstorageref.child(System.
                    currentTimeMillis()+"."+getFileExtension(imageUri));
           final UploadTask muploadTask;
           muploadTask = fileref.putFile(imageUri);
           muploadTask.addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(UserDetailsForm.this,"error",3000).show();
                   p.cancel();
               }
           }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   Task<Uri> urlTask = muploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                       @Override
                       public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                           if (!task.isSuccessful()) {
                               throw task.getException();
                           }

                           // Continue with the task to get the download URL
                           return fileref.getDownloadUrl();
                       }
                   }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                       @Override
                       public void onComplete(@NonNull Task<Uri> task) {
                           if (task.isSuccessful()) {
                               Uri downloadUri = task.getResult();


                               saveInDatabase(downloadUri.toString());
                           } else {
                               // Handle failures
                               // ...
                           }
                       }
                   });


               }
           });
        }

    }
    void saveInDatabase(String link){
        final ProgressDialog p = new ProgressDialog(this);
        p.setCancelable(false);
        p.setMessage("saving...");
        p.show();
        name = edit_name.getText().toString();
        email = getIntent().getStringExtra("email");
        phone = edit_phone.getText().toString();
        accountName = edit_account.getText().toString();

        User newUser = new User(name.toLowerCase().trim(),email.toLowerCase().trim(),phone.toLowerCase().trim(),accountName.toLowerCase().trim(),link);

        email = email.replace(".","");
        email = email.replace("@","");

        db.child("users").child(email).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                p.cancel();
                startActivity(new Intent(UserDetailsForm.this,Login.class));
                finish();
            }
        });
    }

    String getFileExtension(Uri u){
        ContentResolver CR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(CR.getType(u));
    }

    boolean allOk(){
        name = edit_name.getText().toString();
        email = getIntent().getStringExtra("email");
        phone = edit_phone.getText().toString();
        accountName = edit_account.getText().toString();
        if(accountName.equals("") || phone.equals("") || name.equals("")){
            Toast.makeText(this,"All fields are compulsory",Toast.LENGTH_LONG).show();
            return false;
        }
        if(phone.length()!=10){
            //give user notice
            edit_phone.setError("Incorrect number");
            return false;
        }
        return true;
    }
    private void openGallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERYPICK);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERYPICK && resultCode == RESULT_OK && data != null)
        {
            try{
                imageUri = data.getData();
                imageupload.setImageURI(imageUri);
                Picasso.get()
                        .load(imageUri)
                        .into(imageupload);;
            }catch (Exception e){

            }
        }
    }
}
