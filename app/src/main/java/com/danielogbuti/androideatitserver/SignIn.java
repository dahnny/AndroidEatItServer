package com.danielogbuti.androideatitserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.danielogbuti.androideatitserver.common.Common;
import com.danielogbuti.androideatitserver.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignIn extends AppCompatActivity {

    MaterialEditText editPhone, editPassword;
    Button signIn;

    FirebaseDatabase database;
    DatabaseReference user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        editPassword = (MaterialEditText)findViewById(R.id.editPassword);
        editPhone = (MaterialEditText)findViewById(R.id.editPhone);
        signIn = (Button)findViewById(R.id.buttonSignIn);

       database = FirebaseDatabase.getInstance();
       user = database.getReference("User");

       signIn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               signInUser(editPassword.getText().toString(),editPhone.getText().toString());
           }
       });
    }

    private void signInUser(String password, String phone) {
        final ProgressDialog dialog = new ProgressDialog(SignIn.this);
        dialog.setMessage("Please wait");
        dialog.show();

        final String localPhone = phone;
        final String localPassword = password;
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists()){
                    dialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if (Boolean.parseBoolean(user.getIsStaff())){
                        if (user.getPassword().equals(localPassword)){
                            Intent intent = new Intent(SignIn.this,Home.class);
                            Common.currentUser = user;
                            startActivity(intent);
                            finish();

                        }else {
                            dialog.dismiss();
                            Toast.makeText(SignIn.this,"Incorrect Password",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        dialog.dismiss();
                        Toast.makeText(SignIn.this,"Login with a staff account",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    dialog.dismiss();
                    Toast.makeText(SignIn.this,"User does not exist in the database",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
