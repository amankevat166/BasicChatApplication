package com.example.whatsapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import Adapters.ChatAdapter;
import Modules.MessageModel;

public class ChatDetails extends AppCompatActivity {

    EditText typeMessage;
    ImageView profilePic;
    TextView userName;
    ImageButton backBtn,sendBtn;
    RecyclerView recyclerView;
    FirebaseAuth auth;
    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat_details);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));
        backBtn=findViewById(R.id.back_btn);
        profilePic=findViewById(R.id.profile_photo);
        userName=findViewById(R.id.username);
        sendBtn=findViewById(R.id.sendmessage_btn);
        recyclerView=findViewById(R.id.recycleview);
        typeMessage=findViewById(R.id.typemessage);
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        final String senderId=auth.getUid();
        String receiveId=getIntent().getStringExtra("userid");
        String profilePicTxt=getIntent().getStringExtra("profilepic");
        String userNameTxt=getIntent().getStringExtra("username");
        final String senderRoom=senderId+receiveId;
        final String receiverRoom=receiveId+senderId;

        Glide.with(getApplicationContext()).load(profilePicTxt).into(profilePic);
        userName.setText(userNameTxt);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), homePage.class));
            }
        });

        //show chats
        chatShow(senderRoom,receiveId);



        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dataUpload(senderId,senderRoom,receiverRoom);
            }
        });
    }



    public void chatShow(String senderRoom,String receiveId){
        ArrayList<MessageModel> messageModels=new ArrayList<>();
        ChatAdapter chatAdapter=new ChatAdapter(messageModels,this,receiveId);
        recyclerView.setAdapter(chatAdapter);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        database.getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageModels.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()){
                            MessageModel model=snapshot1.getValue(MessageModel.class);
                            model.setMessageId(snapshot1.getKey());
                            messageModels.add(model);
                        }
                        chatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void dataUpload(String senderId,String senderRoom,String receiverRoom ){
        String message=typeMessage.getText().toString();
        final MessageModel model=new MessageModel(senderId,message);
        model.setTimeStamp(new Date().getTime());
        typeMessage.setText("");

        database.getReference().child("chats")
                .child(senderRoom)
                .push()
                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .push()
                                .setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                    }
                });
    }
}