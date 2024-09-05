package com.ahmetymtkn.connectversenew;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmetymtkn.connectversenew.databinding.RecyclerAddFriendsBinding;
import com.ahmetymtkn.connectversenew.model.user;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class invitationAdapter extends RecyclerView.Adapter<invitationAdapter.invitationHolder> {

    private ArrayList<user> invitationArrayList;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private FirebaseAuth auth;

    public invitationAdapter(ArrayList<user> invitationArrayList) {
        this.invitationArrayList = invitationArrayList;

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageReference = storage.getReference();
    }

    @NonNull
    @Override
    public invitationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerAddFriendsBinding recyclerAddFriendsBinding = RecyclerAddFriendsBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new invitationAdapter.invitationHolder(recyclerAddFriendsBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull invitationHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d("invitation Info", "Username: " + invitationArrayList.get(position).name + ", downloadurl: " + invitationArrayList.get(position).downloadurl);

        holder.binding.invitationusername.setText(invitationArrayList.get(position).name);
        Picasso.get().load(invitationArrayList.get(position).downloadurl).into(holder.binding.invitationusernimg);
        holder.binding.acceptinvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> friendInfo = new HashMap<>();
                friendInfo.put("userfriends",invitationArrayList.get(position).userID);
                db.collection("friendship").document(auth.getUid()).collection("friends").document(invitationArrayList.get(position).userID).set(friendInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        deletefriendshipinvite(invitationArrayList.get(position).userID);
                        invitationArrayList.remove(position);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("HATA","ARAKDAŞ EKLEME KISMINDA SIIKINTI VAR İNVİTATİON ADAPTER 59" );
                    }
                });

            }
        });
        holder.binding.refuseinvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletefriendshipinvite(invitationArrayList.get(position).userID);
                invitationArrayList.remove(position);
            }
        });
    }

    private void deletefriendshipinvite(String friendID){
        db.collection("friendship").document(auth.getUid()).collection("adduser").document(friendID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("HATA","ARAKDAŞ silme KISMINDA SIIKINTI VAR İNVİTATİON ADAPTER 83" );
            }
        });
    }


    @Override
    public int getItemCount() {
        return invitationArrayList.size();
    }





    public class invitationHolder extends RecyclerView.ViewHolder{

        private RecyclerAddFriendsBinding binding;

        public invitationHolder(RecyclerAddFriendsBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }

    }





}
