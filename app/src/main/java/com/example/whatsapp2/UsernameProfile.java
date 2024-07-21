package com.example.whatsapp2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import Modules.DataModule;

public class UsernameProfile extends AppCompatActivity {

    ImageView profilePhoto;
    EditText userName;
    Button nextBtn;
    ProgressBar progressBar;
    private final int GALLARY_REQ_CODE=1000;
    FirebaseAuth auth;
    FirebaseStorage storage;
    Uri selectedImageUri;

    DataModule dataModule;
    StorageReference storageReference=FirebaseStorage.getInstance().getReference("images");;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("profiledetails");


//    public void onStart() {
//        super.onStart();
//        FirebaseUser currentUser = auth.getCurrentUser();
//        if (currentUser != null) {
//            Intent intent = new Intent(getApplicationContext(), homePage.class);
//            startActivity(intent);
//            finish();
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_username_profile);

        profilePhoto=findViewById(R.id.profile_photo);
        nextBtn=findViewById(R.id.next_btn);
        progressBar=findViewById(R.id.progressbar);
        auth=FirebaseAuth.getInstance();
        userName=findViewById(R.id.username);
        storage=FirebaseStorage.getInstance();
        dataModule=new DataModule();

        String mobNum=getIntent().getStringExtra("mobNum");

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery=new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,GALLARY_REQ_CODE);
//
            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userNameTxt=userName.getText().toString();
                if (TextUtils.isEmpty(userNameTxt)){
                    Toast.makeText(UsernameProfile.this, "Please enter username", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadData(selectedImageUri,mobNum);
                    startActivity(new Intent(getApplicationContext(), homePage.class));
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode==RESULT_OK){
            if (requestCode==GALLARY_REQ_CODE){
                selectedImageUri=data.getData();
                profilePhoto.setImageURI(data.getData());

            }
        }
    }

    public void uploadData(Uri uri,String mobNum){
        String userNameTxt=userName.getText().toString();
        String mobileNo=mobNum;
        String uid = auth.getUid();
        dataModule.setUserName(userNameTxt);
        dataModule.setUserMobNum(mobileNo);
        dataModule.setUid_user(uid);
        progressBar.setVisibility(View.VISIBLE);
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("File Uploading");
        progressDialog.show();

        StorageReference reference=storageReference.child(String.valueOf(System.currentTimeMillis()));
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        dataModule.setProfilePic(String.valueOf(uri));
                        DatabaseReference newReference = databaseReference.push();
                        newReference.setValue(dataModule);
                        String referenceKey = newReference.getKey();
                        dataModule.setReference(referenceKey);
                        newReference.child("Reference").setValue(referenceKey);
                        Toast.makeText(UsernameProfile.this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(UsernameProfile.this, "Data doesnt uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float percent=(100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded: " +(int)percent+"%");
            }
        });
    }

}