package com.example.lightway;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
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
                changeImageViewPic();

            }
        });

        setFirebasePic = findViewById(R.id.setFirebasePic);

        setFirebasePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facebookPic = gatherFBData();
                setFirebasePic(facebookPic);
            }
        });

        profilePic = findViewById(R.id.facebookPicture);

    }

    private void setFirebasePic(String facebookPic){
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(facebookPic))
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

    private void changeImageViewPic() {

        Uri newPicture = mAuth.getCurrentUser().getPhotoUrl();

        if(newPicture != null){
            Picasso.get().load(newPicture).fit().centerCrop().into(profilePic);
        }else{
            Log.d("Tag", "newPicture is null");
        }

    }
    /*private void gatherFacebookPic(){

        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/100026029201882/picture",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here
                    }
                });

        request.executeAsync();
    }*/

    private String gatherFBData(){
        String facebookUserId = "";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // find the Facebook profile and get the user's id
        for(UserInfo profile : user.getProviderData()) {
            // check if the provider id matches "facebook.com"
            if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                facebookUserId = profile.getUid();
            }
        }

        return "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";

    }

}
