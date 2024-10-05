package com.ahmetymtkn.connectversenew;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmetymtkn.connectversenew.databinding.FragmentAllUserChatsPageBinding;
import com.ahmetymtkn.connectversenew.model.user;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;


public class AllUserChatsPage extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FragmentAllUserChatsPageBinding binding;
    private String userID;
    private ArrayList<user> lastmessages;
    private LastMessagesAdapter lastMessagesAdapterclass;
    String downloadURL,username;

    public AllUserChatsPage(){
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageReference = storage.getReference();
        lastmessages= new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentAllUserChatsPageBinding.inflate(inflater, container, false);

        userID=auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> data = documentSnapshot.getData();
                String downloadURL = (String) data.get("downloadurl");
                String username = (String) data.get("username");
                getUserInfo(downloadURL,username);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Hata",e.getLocalizedMessage().toString());
            }
        });



        binding.lastmessagerecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        lastMessagesAdapterclass = new LastMessagesAdapter(lastmessages,auth.getCurrentUser().getUid());
        binding.lastmessagerecycler.setAdapter(lastMessagesAdapterclass);

        lastChatingFriend();

        binding.powerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getContext(), LoginPage.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        return binding.getRoot();

    }

    private void getUserInfo(String imageURL,String userName){
        Picasso.get().load(imageURL).into(binding.userphoto);
        binding.username.setText(userName);
    }

    private void lastChatingFriend(){

        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("messages").child(userID);
        messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String messagedUserID = userSnapshot.getKey();
                    DatabaseReference lastmessagesRef = FirebaseDatabase.getInstance().getReference("messages")
                            .child(userID)
                            .child(messagedUserID);
                    Query lastMessageQuery = lastmessagesRef.orderByChild("timestamp").limitToLast(1);

                    lastMessageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                                    String sender = msgSnapshot.child("senderID").getValue(String.class);
                                    String receiver = msgSnapshot.child("receiverID").getValue(String.class);
                                    String messageText = msgSnapshot.child("message").getValue(String.class);
                                    Long timestamp = msgSnapshot.child("timestamp").getValue(Long.class);

                                    getuserInfo(searchfriendsId(sender,receiver),messageText);


                                }

                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("Hata",databaseError.toString());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Hata",databaseError.toString());
            }
        });

    }


    private void getuserInfo(String userID,String message){

        db.collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Map<String, Object> data = documentSnapshot.getData();
                downloadURL = (String) data.get("downloadurl");
                username = (String) data.get("username");
                lastmessages.add(new user(username,downloadURL,userID,message));
                lastMessagesAdapterclass.notifyDataSetChanged();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Hata",e.getLocalizedMessage().toString());
            }
        });
    }

    private String searchfriendsId(String sender,String reciver){
        if (sender.equals(auth.getCurrentUser().getUid())){
            return reciver;
        }else {
            return sender;
        }
    }
}