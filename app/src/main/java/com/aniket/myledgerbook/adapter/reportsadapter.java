package com.aniket.myledgerbook.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aniket.myledgerbook.R;
import com.aniket.myledgerbook.models.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class reportsadapter extends RecyclerView.Adapter<reportsadapter.viewholder> {

    ArrayList<report> array = new ArrayList<>();
    Context context;

    public reportsadapter(ArrayList<report> array, Context context){
        this.array = array;
        this.context = context;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reportview,parent,false);
        return new reportsadapter.viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        holder.reportName.setText(Character.toUpperCase(array.get(position).getName().toString().charAt(0))+array.get(position).getName().toString().substring(1));
    }

    @Override
    public int getItemCount() {
        return array.size();
    }

    class viewholder extends RecyclerView.ViewHolder{
        TextView reportName;
        ImageView reportImage;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            reportName = itemView.findViewById(R.id.report_name);
            reportImage = itemView.findViewById(R.id.report_pdf);

            reportImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    email = email.replace("@","");
                    email = email.replace(".","");
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference("records").child(email).child(array.get(getAdapterPosition()).getId());
                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String message = "";
                            for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                records new_record = dataSnapshot1.getValue(records.class);
                                message += new_record.getId() + "      " + new_record.getMoney() + "\n";
                            }
                            Toast.makeText(context,message,3000).show();
                            savepdf(message);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        }
    }

    public void savepdf(String message){
        com.itextpdf.text.Document mDoc = new Document();
        String mFileName = String.valueOf(System.currentTimeMillis());
        String mFilePath = Environment.getExternalStorageDirectory()+"/"+mFileName+".pdf";

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+mFileName+".pdf");
        try{
              PdfWriter.getInstance(mDoc,new FileOutputStream(mFilePath));
              mDoc.open();
              mDoc.add(new Paragraph(message));
              mDoc.close();
              Toast.makeText(context,mFileName+".pdf\nis saved to\n"+mFilePath,3000).show();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
        }catch (Exception e){
              Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
}
