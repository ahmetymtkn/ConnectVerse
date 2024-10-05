package com.ahmetymtkn.connectversenew;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetymtkn.connectversenew.databinding.RecyclerAllFriendsBinding;
import com.ahmetymtkn.connectversenew.model.user;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class userAdapter extends RecyclerView.Adapter<userAdapter.userHolder> {

    private ArrayList<user> userArrayList;

    public userAdapter(ArrayList<user> userArrayList) {
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public userHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerAllFriendsBinding recyclerAllFriendsBinding = RecyclerAllFriendsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new userHolder(recyclerAllFriendsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull userHolder holder, int position) {
        Log.d("user Info", "Username: " + userArrayList.get(position).name + ", downloadurl: " + userArrayList.get(position).downloadurl);

        holder.binding.allusername.setText(userArrayList.get(position).name);
        Picasso.get().load(userArrayList.get(position).downloadurl).into(holder.binding.alluserimage);
        user selectedUser = userArrayList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(holder.itemView.getContext(), UserAndFriendChatinPage.class);
                intent.putExtra("selectedUser", selectedUser);
                holder.itemView.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }
    public void updateData(ArrayList<user> userArrayList){
        this.userArrayList= userArrayList;
        notifyDataSetChanged();
    }

    public class userHolder extends RecyclerView.ViewHolder{
        private  RecyclerAllFriendsBinding binding;
        public userHolder(RecyclerAllFriendsBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

    }
}
