package com.aniket.myledgerbook;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aniket.myledgerbook.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.aniket.myledgerbook.models.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }
    TextView name,phone,acc;
    Button logout;
    CircleImageView profilepic;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_profile, container, false);
        name = v.findViewById(R.id.user_name);
        phone = v.findViewById(R.id.user_phone);
        acc = v.findViewById(R.id.user_account);
        profilepic = v.findViewById(R.id.profilepic);
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users");
        String email = mAuth.getCurrentUser().getEmail();

        email = email.replace("@","");
        email = email.replace(".","");

        logout = v.findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(getActivity(),Login.class));
                getActivity().finish();
            }
        });

        db.child(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User new_user = dataSnapshot.getValue(User.class);
                name.setText(Character.toUpperCase(new_user.getName().charAt(0))+new_user.getName().substring(1));
                phone.setText(new_user.getPhone());
                acc.setText(Character.toUpperCase(new_user.getAccountName().charAt(0))+new_user.getAccountName().substring(1));
                Picasso.get().load(new_user.imageurl)
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.man)
                        .into(profilepic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return v;
    }

}
