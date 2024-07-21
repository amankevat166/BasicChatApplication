package Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsapp2.ChatDetails;
import com.example.whatsapp2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import Modules.DataModule;

public class ChatUserlistAdapter extends RecyclerView.Adapter<ChatUserlistAdapter.MyViewHolder> {
    ArrayList<DataModule> dataHolder;
    Context context;
    boolean isChat;

    public ChatUserlistAdapter(ArrayList<DataModule> dataHolder, Context context,boolean isChat) {
        this.dataHolder = dataHolder;
        this.context = context;
        this.isChat=isChat;
    }



    @Override
    public ChatUserlistAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chatoutside_design,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatUserlistAdapter.MyViewHolder holder, int position) {
        DataModule dataModule=dataHolder.get(position);
        Glide.with(context).load(dataModule.getProfilePic()).into(holder.userPic);
        holder.userNAme.setText(dataHolder.get(position).getUserName());

        //to set last message
        FirebaseDatabase.getInstance().getReference().child("chats")
                        .child(FirebaseAuth.getInstance().getUid()+dataModule.getUid_user())
                                .orderByChild("timeStamp")
                                        .limitToLast(1)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot snapshot1: snapshot.getChildren()){
                                                            holder.lastMessage.setText(snapshot1.child("message").getValue().toString());
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ChatDetails.class);
                intent.putExtra("userid",dataModule.getUid_user());
                intent.putExtra("profilepic",dataModule.getProfilePic());
                intent.putExtra("username",dataModule.getUserName());
                context.startActivity(intent);
            }
        });

//        if (isChat){
//            if (dataModule.getStatus().equals("online")){
//                holder.imgOn.setVisibility(View.VISIBLE);
//                holder.imgOff.setVisibility(View.GONE);
//            }
//            else {
//                holder.imgOff.setVisibility(View.VISIBLE);
//                holder.imgOn.setVisibility(View.GONE);
//            }
//        }
//        else {
//            holder.imgOff.setVisibility(View.GONE);
//            holder.imgOn.setVisibility(View.GONE);
//        }
    }

    @Override
    public int getItemCount() {
        return dataHolder.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView userPic,imgOn,imgOff;
        TextView userNAme,lastMessage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userPic=itemView.findViewById(R.id.profile_photo);
            userNAme=itemView.findViewById(R.id.username);
            lastMessage=itemView.findViewById(R.id.lastmessage);
            imgOn=itemView.findViewById(R.id.img_on);
            imgOff=itemView.findViewById(R.id.img_off);
        }
    }
}
