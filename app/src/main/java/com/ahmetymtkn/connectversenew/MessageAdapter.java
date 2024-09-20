package com.ahmetymtkn.connectversenew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetymtkn.connectversenew.R;
import com.ahmetymtkn.connectversenew.model.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private ArrayList<Message> messageList;
    private String currentUserID;

    public MessageAdapter(ArrayList<Message> messageList, String currentUserID) {
        this.messageList = messageList;
        this.currentUserID = currentUserID;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getSenderID().equals(currentUserID)) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagebox_send, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messagebox_incoming, parent, false);
        }
        return new MessageViewHolder(view);
    }




    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageTextView.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}

