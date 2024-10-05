package com.ahmetymtkn.connectversenew;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ahmetymtkn.connectversenew.model.Message;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private ArrayList<Message> messageList;
    private String currentUserID;
    private final String urlPattern = "https://firebasestorage";
    private Context context;

    public MessageAdapter(ArrayList<Message> messageList, String currentUserID, Context context) {
        this.messageList = messageList;
        this.currentUserID = currentUserID;
        this.context=context;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderID().equals("user")) {
            return 1;
        } else if (messageList.get(position).getSenderID().equals(currentUserID)) {
            return 2;
        } else{
            return 3;
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.messagebox_ai, parent, false);
        } else if (viewType == 2) {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.messagebox_send, parent, false);
        } else {
            view = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.messagebox_incoming, parent, false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(message.getMessage().contains(urlPattern)){
            holder.image.setVisibility(View.VISIBLE);
            holder.messageTextView.setVisibility(View.GONE);
            Glide.with(context)
                    .load(message.getMessage())
                    .into(holder.image);

        }
        else {
            holder.messageTextView.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        ImageView image;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            image = itemView.findViewById(R.id.photo);
        }
    }
}

