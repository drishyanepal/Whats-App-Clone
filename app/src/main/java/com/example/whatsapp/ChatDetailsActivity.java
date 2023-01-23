package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whatsapp.Adapters.ChatAdapter;
import com.example.whatsapp.Models.MessageModel;
import com.example.whatsapp.databinding.ActivityChatDetailsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailsActivity extends AppCompatActivity {
    ActivityChatDetailsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String profilePic = getIntent().getStringExtra("profilePic");
        String userName = getIntent().getStringExtra("userName");

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatDetailsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Picasso.get().load(profilePic).placeholder(R.drawable.profile).into(binding.chatImage);
        binding.userNameChat.setText(userName);

        ArrayList<MessageModel> list = new ArrayList<>();
        ChatAdapter adapter = new ChatAdapter(list, this, receiverId);
        binding.chatsRv.setAdapter(adapter);
        binding.chatsRv.setLayoutManager(new LinearLayoutManager(this));

        String senderRoom = senderId + receiverId;
        String receiverRoom = receiverId + senderId;

        DatabaseReference referenceOne = database.getReference().child("Chats").child(senderRoom);
        referenceOne.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel model = dataSnapshot.getValue(MessageModel.class);
                    model.setMessageId(dataSnapshot.getKey());
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
                MessageModel model = new MessageModel(senderId, message);
                model.setTimeStamp(new Date().getTime());
                binding.messageT.setText("");

                DatabaseReference reference = database.getReference().child("Chats").child(senderRoom);
                reference.push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DatabaseReference referenceS = database.getReference().child("Chats").child(receiverRoom);
                        referenceS.push().setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatDetailsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}