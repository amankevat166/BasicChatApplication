package Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.whatsapp2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Adapters.ChatUserlistAdapter;
import Modules.DataModule;


public class ChatFragment extends Fragment {

    ArrayList<DataModule> arrayList=new ArrayList<>();
    FirebaseAuth auth=FirebaseAuth.getInstance();
    FirebaseDatabase database;
    RecyclerView recyclerView;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat, container, false);
        database=FirebaseDatabase.getInstance();
        recyclerView=view.findViewById(R.id.recycleview);

        ChatUserlistAdapter chatUserlistAdapter=new ChatUserlistAdapter(arrayList,getContext(),true);
        recyclerView.setAdapter(chatUserlistAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        database.getReference().child("profiledetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DataModule dataModule = dataSnapshot.getValue(DataModule.class);
                    if (dataModule.getUid_user().equals(FirebaseAuth.getInstance().getUid())){


                    }else{
                        arrayList.add(dataModule);
                    }

                }
                chatUserlistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}