package com.example.lightway;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;


public class ProfileTemp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button changePicBtn;
    private ImageView profilePic;
    private Button setFirebasePicBtn;
    private EditText imageUri;
    private Button uploadPhotoBtn;
    private Uri urii;
    public static final int GALLERY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_temp);


        mAuth = FirebaseAuth.getInstance(); //Gets the instance of the Firebase Login.

        changePicBtn = findViewById(R.id.changeProfilePic);

        changePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //changeImageViewPic();

            }
        });

        setFirebasePicBtn = findViewById(R.id.setFirebasePic);

        setFirebasePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // String providerPicture = gatherProviderData();
                // changePicWithUri(Uri.parse(providerPicture));
                String gatheredImageUri = imageUri.getText().toString().trim();

                if(gatheredImageUri.isEmpty()){
                    String providerPicture = gatherProviderData();
                    if(providerPicture!=null){

                        changePicWithUri(Uri.parse(providerPicture));
                    }
                }else{

                    changePicWithUri(Uri.parse(gatheredImageUri));
                }
            }
        });

        profilePic = findViewById(R.id.facebookPicture);
        imageUri = findViewById(R.id.imageUri);
        uploadPhotoBtn = findViewById(R.id.uploadButton);

        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Opens up the gallery
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GALLERY_REQUEST);
            }
        });

        urii = mAuth.getCurrentUser().getPhotoUrl();

        RequestOptions options = new RequestOptions();

        Glide.with(ProfileTemp.this)
                .load(urii)
                .apply(options.centerInside())
                .into(profilePic);


    }

    //Method is used to display photo in a imageview, can be changed so uri is taken through a parameter.
    /*private void changeImageViewPic() {

        Uri newPicture = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl(); //Gets the firebase photo from the current user

        Picasso.get().setLoggingEnabled(true);

        if(newPicture != null){
            Picasso.get().load(newPicture).fit().centerCrop().into(profilePic, new Callback() {
                @Override
                public void onSuccess() {
                    Log.d("TAG", "Picture load was Successful");
                }

                @Override
                public void onError(Exception e) {
                    Log.d("ERROR", "Picture didnt load, Exception: " + e);
                }
            });
        }else{
            Log.d("Tag", "newPicture is null");
        }

    }*/


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

    // Gathers the profile picture of either Facebook or Google.
    private String gatherProviderData(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // find the Facebook profile and get the user's id
        for(UserInfo profile : user.getProviderData()) {
            // check if the provider id matches "facebook.com"
            if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                String facebookUserId = profile.getUid();
                return "https://graph.facebook.com/" + facebookUserId + "/picture?height=300";
            }
            //Checks if the provider id matches with "google.com"
            if(GoogleAuthProvider.PROVIDER_ID.equals(profile.getProviderId())){
                String url= FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
                url = url.replace("/s96-c/","/s300-c/");

                return url;
            }
        }
        return null;
    }

    //This method can be used to change the firebase users profile pic with an Uri
    private void changePicWithUri(Uri galleryphoto){
        FirebaseUser user = mAuth.getCurrentUser(); //Gets the current user
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(galleryphoto) //Sets the photo from the picture gathered from the gallery
                .build();
        user.updateProfile(profileUpdates) //Updates the profile on firebase
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Tag", "User profile updated.");
                            Toast.makeText(ProfileTemp.this, "Profile picture has been changed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {

                case GALLERY_REQUEST:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri selectedImage = data.getData(); //Gets the data from the selected image.
                        changePicWithUri(selectedImage); //Uploads the image to firebase
                        break;
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.e("TAG", "Selecting picture cancelled");
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e("ERROR", "Exception in onActivityResult : " + e.getMessage());
        }
    }

}
