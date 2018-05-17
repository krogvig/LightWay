package com.example.lightway;

import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class DeleteUserActivity extends AppCompatActivity {

    DatabaseReference userToBeRemoved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete your account?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //perform any action
                deleteUser();
                Toast.makeText(getApplicationContext(), "Yes clicked", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //perform any action
                Toast.makeText(getApplicationContext(), "No clicked", Toast.LENGTH_SHORT).show();
            }
        });

        //creating alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void deleteUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        userToBeRemoved = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            removeUserFromDatabase();
                            LoginManager.getInstance().logOut();

                        }
                    }});
    }

    private void removeUserFromDatabase(){
        userToBeRemoved.removeValue();

    }
}

