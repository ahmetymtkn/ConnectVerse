package com.ahmetymtkn.connectversenew;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmetymtkn.connectversenew.databinding.ActivityMainBinding;
import com.ahmetymtkn.connectversenew.databinding.ActivityUserAndFriendChatinPageBinding;
import com.ahmetymtkn.connectversenew.model.Message;
import com.ahmetymtkn.connectversenew.model.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserAndFriendChatinPage extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Message> messageList;
    private MessageAdapter messageAdapter;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    private ActivityUserAndFriendChatinPageBinding binding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserAndFriendChatinPageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);








        Intent intent = getIntent();
        user selectedUser = (user) intent.getSerializableExtra("selectedUser");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Picasso.get().load(selectedUser.downloadurl).into(binding.friendimage);
        binding.friendname.setText(selectedUser.name);



        String userId = selectedUser.userID;
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.child("online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isOnline = dataSnapshot.getValue(Boolean.class);

                if (isOnline != null && isOnline) {
                    binding.aktiflik.setText("Online");
                    binding.aktiflik.setTextColor(Color.parseColor("#09950F"));
                } else {
                    binding.aktiflik.setText("Offline");
                    binding.aktiflik.setTextColor(Color.parseColor("#E30202"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hata yönetimi burada yapılabilir
            }
        });


        messageList = new ArrayList<>();
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

        messageAdapter = new MessageAdapter(messageList, firebaseAuth.getInstance().getCurrentUser().getUid());
        binding.recyclerview.setAdapter(messageAdapter);

        binding.messagesend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage(selectedUser);
            }
        });
        loadMessages(selectedUser);

        binding.chatspagetrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UserAndFriendChatinPage.this)
                        .setTitle("Delete Chat")
                        .setMessage("Are you sure for delete this chat?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            deleteChatFromFirebase(selectedUser);
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void deleteChatFromFirebase(user selectedUser){
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("messages").child(currentUser.getUid()).child(selectedUser.userID);
        Log.d("DeleteChat", "Chat reference: " + chatRef.toString());

        chatRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UserAndFriendChatinPage.this, "Sohbet silindi.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(UserAndFriendChatinPage.this, "Silme işlemi başarısız.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addMessage(user friend){
        DatabaseReference database = firebaseDatabase.getInstance().getReference();
        String userID = firebaseAuth.getInstance().getCurrentUser().getUid();
        String messagedUserID = friend.userID;
        String messageID1 = database.child("messages").child(userID).child(messagedUserID).push().getKey();
        String messageID2 = database.child("messages").child(messagedUserID).child(userID).push().getKey();
        String messageInfo = binding.sendMessageEditText.getText().toString();
        Message message = new Message(userID, messagedUserID, messageInfo, System.currentTimeMillis());
        database.child("messages").child(userID).child(messagedUserID).child(messageID1).setValue(message);
        database.child("messages").child(messagedUserID).child(userID).child(messageID2).setValue(message);
        binding.sendMessageEditText.setText("");
    }
    private void loadMessages(user friend) {
        DatabaseReference database = firebaseDatabase.getInstance().getReference();
        String userID = firebaseAuth.getInstance().getCurrentUser().getUid();
        String messagedUserID = friend.userID;

        // Mesajları dinlemek için yol tanımlanıyor
        DatabaseReference messagesRef = database.child("messages").child(userID).child(messagedUserID);

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                // Mesajları temizleyin veya sıfırlayın


                // Tüm mesajlar üzerinden döngü
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messageList.add(message);
                }

                Collections.sort(messageList, (m1, m2) -> Long.compare(m1.getTimestamp(), m2.getTimestamp()));
                messageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }

    protected void onStart() {
        super.onStart();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("online").setValue(true);
        }
    }

    protected void onPause() {
        super.onPause();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("online").setValue(false);
        }
    }



}