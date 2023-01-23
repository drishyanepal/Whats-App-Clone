package com.example.whatsapp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.ChatDetailsActivity;
import com.example.whatsapp.Models.UserModel;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.myViewHolder> {
    Context context;
    Activity activity;
    ArrayList<UserModel> list;

    public UserAdapter(Activity activity, Context context, ArrayList<UserModel> list) {
        this.activity = activity;
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sample_row, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.myViewHolder holder, int position) {
        UserModel model = list.get(position);
        Picasso.get().load(model.getProfilePic()).placeholder(R.drawable.profile).into(holder.profilePic);
        holder.userName.setText(model.getUserName());

        FirebaseDatabase.getInstance().getReference().child("Chats")
                .child(FirebaseAuth.getInstance().getUid() + model.getUserId())
                .orderByChild("timestamp")
                .limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        holder.lastMessage.setText(dataSnapshot.child("message").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatDetailsActivity.class);
                intent.putExtra("userId", model.getUserId());
                intent.putExtra("profilePic", model.getProfilePic());
                intent.putExtra("userName", model.getUserName());
                context.startActivity(intent);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView userName, lastMessage;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.nameTitle);
            lastMessage = itemView.findViewById(R.id.lastMessage);

        }
    }
}
