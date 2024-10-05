package com.ahmetymtkn.connectversenew;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ahmetymtkn.connectversenew.model.Message;
import java.util.ArrayList;

public class AiChatMessageAdapter extends RecyclerView.Adapter<AiChatMessageAdapter.MessageViewHolder> {
    private ArrayList<Message> messageList;

    public AiChatMessageAdapter(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }
    public int getItemViewType(int position) {
        if (messageList
                .get(position)
                .getSenderID()
                .equals("user")
        ) {
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
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
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageTextView.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
