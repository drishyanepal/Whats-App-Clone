package com.example.whatsapp.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsapp.Models.MessageModel;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter {
    final int SENDER_VIEW_TYPE = 1;
    final int RECEIVER_VIEW_TYPE = 2;
    ArrayList<MessageModel> list;
    Context context;
    String receiverId;

    public ChatAdapter(ArrayList<MessageModel> list, Context context, String receiverId) {
        this.list = list;
        this.receiverId = receiverId;
        this.context = context;
    }

    public ChatAdapter(ArrayList<MessageModel> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            return SENDER_VIEW_TYPE;
        } else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel model = list.get(position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Message")
                        .setMessage("Are you sure to delete this message?")
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseDatabase.getInstance().getReference().child("Chats")
                                        .child(FirebaseAuth.getInstance().getUid() + receiverId)
                                        .child(model.getMessageId())
                                        .setValue(null);
                            }
                        }).setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                return false;
            }
        });

        if (holder.getClass() == SenderViewHolder.class) {
            ((SenderViewHolder) holder).messageSender.setText(model.getMessage());
        } else {
            ((ReceiverViewHolder) holder).messageReceiver.setText(model.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {
        TextView messageSender, timeStampSender;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            messageSender = itemView.findViewById(R.id.messageSender);
            timeStampSender = itemView.findViewById(R.id.timeSender);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView messageReceiver, timeStampReceiver;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            messageReceiver = itemView.findViewById(R.id.messageReceiver);
            timeStampReceiver = itemView.findViewById(R.id.timeReceiver);
        }
    }
}
