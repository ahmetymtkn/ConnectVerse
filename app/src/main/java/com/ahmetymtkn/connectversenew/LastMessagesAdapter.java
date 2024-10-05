package com.ahmetymtkn.connectversenew;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;

import com.ahmetymtkn.connectversenew.databinding.RecyclerAllUserChatsBinding;
import com.ahmetymtkn.connectversenew.model.user;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LastMessagesAdapter extends RecyclerView.Adapter<LastMessagesAdapter.LastMessageViewHolder> {
    private ArrayList<user> lastMessagesList;
    private static String currentUser;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private final String urlPattern = "https://firebasestorage";

    public LastMessagesAdapter(ArrayList<user> messageList, String currentUserID) {
        this.lastMessagesList = messageList;
        this.currentUser = currentUserID;
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = storage.getReference();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public LastMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerAllUserChatsBinding recyclerAllUserChatsBinding = RecyclerAllUserChatsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new LastMessageViewHolder(recyclerAllUserChatsBinding);
    }

    private void deleteChatFromFirebase(int position){
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("messages").child(currentUser).child(lastMessagesList.get(position).userID);
        Log.d("DeleteChat", "Chat reference: " + chatRef.toString());
        chatRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                lastMessagesList.remove(position);
                notifyDataSetChanged();
            } else {
            }
        });
    }

    @Override
    public void onBindViewHolder(LastMessageViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.chatsusername.setText(lastMessagesList.get(position).name);
        Picasso.get().load(lastMessagesList.get(position).downloadurl).into(holder.binding.chatsuserimage);


        if(lastMessagesList.get(position).lastmessage.contains(urlPattern)){
            holder.binding.lastmessage.setText("PHOTO \uD83D\uDCF7");
        }
        else {
            holder.binding.lastmessage.setText(lastMessagesList.get(position).getLastmessage());
        }
        holder.binding.chatspagetrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Delete Chat")
                            .setMessage("Are you sure for delete this chat?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                deleteChatFromFirebase(position);
                            })
                            .setNegativeButton("No", null)
                            .show();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), UserAndFriendChatinPage.class);
                intent.putExtra("selectedUser", lastMessagesList.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return lastMessagesList.size();
    }

    public static class LastMessageViewHolder extends RecyclerView.ViewHolder {
        RecyclerAllUserChatsBinding binding;
        public LastMessageViewHolder(RecyclerAllUserChatsBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
