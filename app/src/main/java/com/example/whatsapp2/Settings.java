package com.example.whatsapp2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import Modules.DataModule;

public class Settings extends AppCompatActivity {

    EditText userName;
    Button savBtn;
    ImageView profilePhoto,backBtn;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage storage;
    Uri selectedImageUri;
    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("profiledetails");
    String nodeId;
    String uid;
    DataModule dataModule;

    private final int GALLARY_REQ_CODE=1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);

        profilePhoto=findViewById(R.id.profile_photo);
        userName=findViewById(R.id.username);
        auth=FirebaseAuth.getInstance();
        savBtn=findViewById(R.id.save_btn);
        backBtn=findViewById(R.id.back_btn);
        firebaseDatabase=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();
        dataModule=new DataModule();

        FirebaseApp.initializeApp(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), homePage.class));
            }
        });
        //load firebase data to components

            FirebaseUser currentUser=FirebaseAuth.getInstance().getCurrentUser();

            Query profUser=reference.orderByChild("uid_user").equalTo(currentUser.getUid());
            profUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        DataSnapshot profSnapshot=snapshot.getChildren().iterator().next();
                        DataModule dataModule = dataSnapshot.getValue(DataModule.class);
                        String userNameTxt=dataModule.getUserName();
                        String userPicTxt=dataModule.getProfilePic();
                        dataModule.setUserName(userNameTxt);
                        dataModule.setProfilePic(userPicTxt);
                        userName.setText(userNameTxt);
                        nodeId = dataSnapshot.getKey();
                        Glide.with(Settings.this).load(userPicTxt).into(profilePhoto);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        savBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageUri=getUri();
                Log.d("nodeId","node id :"+nodeId);
                Toast.makeText(Settings.this, "node id "+nodeId, Toast.LENGTH_SHORT).show();
                String userNameTxt=userName.getText().toString().trim();
                Map<String,Object> update=new HashMap<>();
                update.put("userName",userNameTxt);
             // update.put("profilePic",selectedImageUri);
                StorageReference storageReference=FirebaseStorage.getInstance().getReference("images");;
                StorageReference sreference=storageReference.child(String.valueOf(System.currentTimeMillis()));
                sreference.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        sreference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
update.put("profilePic",uri.toString());
reference.child(nodeId).updateChildren(update);

                            }
                        });
                    }
                });
//                reference.child(nodeId).updateChildren(update)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()){
//                                    Toast.makeText(Settings.this, "data update successfully", Toast.LENGTH_SHORT).show();
//                                }
//                                else {
//                                    Toast.makeText(Settings.this, "Data failed to update"+task.getException(),  Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iGallery=new Intent(Intent.ACTION_PICK);
                iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iGallery,GALLARY_REQ_CODE);
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

    public void getUserData(){
    }

    public Uri getUri() {
        Uri imageUri = null;
        // Assuming you have an ImageView called 'imageView' in your layout
        //ImageView imageView = findViewById(R.id.);
        Drawable drawable = profilePhoto.getDrawable();

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        FileOutputStream fileOutputStream;
        try {
            File file = new File(getCacheDir(), "image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            imageUri = Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("image", "image uri in method:" + imageUri);
        return imageUri;

    }
}