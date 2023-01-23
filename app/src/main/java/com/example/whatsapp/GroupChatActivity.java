package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whatsapp.Adapters.ChatAdapter;
import com.example.whatsapp.Models.MessageModel;
import com.example.whatsapp.databinding.ActivityGroupChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class GroupChatActivity extends AppCompatActivity {
    ActivityGroupChatBinding binding;
    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        ArrayList<MessageModel> list = new ArrayList<>();
        ChatAdapter adapter = new ChatAdapter(list, GroupChatActivity.this);
        binding.chatsRv.setAdapter(adapter);
        binding.chatsRv.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        String id = auth.getUid();
        binding.userNameChat.setText("Friends Group");

        database.getReference().child("GroupChat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel model = dataSnapshot.getValue(MessageModel.class);
                    list.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.messageT.getText().toString();
                MessageModel model = new MessageModel(id, message);
                model.setTimeStamp(new Date().getTime());
                binding.messageT.setText("");
                database.getReference().child("GroupChat").push().setValue(model);
            }
        });
    }
}