package com.example.lightway;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.squareup.picasso.Picasso;

public class ProfileTemp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button changeProfilePic;
    private ImageView profilePic;
    private Button setFirebasePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_temp);

        mAuth = FirebaseAuth.getInstance();

        changeProfilePic = findViewById(R.id.changeProfilePic);

        changeProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFacebookPic();

            }
        });

        setFirebasePic = findViewById(R.id.setFirebasePic);

        setFirebasePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFirebasePic();
            }
        });

        profilePic = findViewById(R.id.facebookPicture);

    }

    private void setFirebasePic(){
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("JagHeterMicke")
                .setPhotoUri(Uri.parse("https://i.imgur.com/KrQ3CLS.jpg"))
                //.setPhotoUri(Uri.parse("http://square.github.io/picasso/static/sample.png"))
                .build();
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Tag", "User profile updated.");
                        }
                    }
                });
    }

    private void changeFacebookPic() {

        Uri newPicture = mAuth.getCurrentUser().getPhotoUrl();

        if(newPicture != null){
            Picasso.get().load(newPicture).fit().centerCrop().into(profilePic);
        }else{
            Log.d("Tag", "newPicture is null");
        }




    }
}
