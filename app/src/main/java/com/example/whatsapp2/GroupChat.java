package com.example.whatsapp2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import Adapters.ChatAdapter;
import Adapters.GroupChatAdapter;
import Modules.MessageModel;

public class GroupChat extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    ArrayList<MessageModel> messageModels=new ArrayList<>();
    RecyclerView recyclerView;
    TextView userName;
    EditText typeMessage;
    ImageButton sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_group_chat);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.green));

        recyclerView=findViewById(R.id.recycleview);
        userName=findViewById(R.id.username);
        typeMessage=findViewById(R.id.typemessage);
        sendBtn=findViewById(R.id.sendmessage_btn);
        String senderId= FirebaseAuth.getInstance().getUid();
        String receiveId=getIntent().getStringExtra("userid");

        userName.setText("Group chat");
        ChatAdapter groupChatAdapter=new ChatAdapter(messageModels,this);
        recyclerView.setAdapter(groupChatAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);


        firebaseDatabase.getReference().child("Group chat")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                messageModels.clear();
                                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    MessageModel model=dataSnapshot.getValue(MessageModel.class);
                                    messageModels.add(model);
                                }
                                groupChatAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=typeMessage.getText().toString();
                typeMessage.setText("");

                MessageModel messageModel=new MessageModel(senderId,message);
                messageModel.setTimeStamp(new Date().getTime());

                firebaseDatabase.getReference().child("Group chat")
                        .push()
                        .setValue(messageModel)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(GroupChat.this, "Message send", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
}