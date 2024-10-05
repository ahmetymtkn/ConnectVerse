package com.ahmetymtkn.connectversenew;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmetymtkn.connectversenew.databinding.ActivityUserAndFriendChatinPageBinding;
import com.ahmetymtkn.connectversenew.model.Message;
import com.ahmetymtkn.connectversenew.model.user;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

public class UserAndFriendChatinPage extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Message> messageList;
    private MessageAdapter messageAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private FirebaseStorage storage;


    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    Bitmap selectedImage;
    Uri imageData;

    private ActivityUserAndFriendChatinPageBinding binding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserAndFriendChatinPageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Intent intent = getIntent();
        user selectedUser = (user) intent.getSerializableExtra("selectedUser");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Picasso.get().load(selectedUser.downloadurl).into(binding.friendimage);
        binding.friendname.setText(selectedUser.name);

        registerLauncher();

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
            }
        });

        messageList = new ArrayList<>();
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));

        messageAdapter = new MessageAdapter(messageList, firebaseAuth.getInstance().getCurrentUser().getUid(),this);
        binding.recyclerview.setAdapter(messageAdapter);

        binding.messagesend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMessage(selectedUser);
            }
        });

        loadMessages(selectedUser);

        binding.recyclerview.post(() -> binding.recyclerview.scrollToPosition(messageList.size() - 1));


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

        binding.messagegrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(v.getContext(), android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) v.getContext(), android.Manifest.permission.READ_MEDIA_IMAGES)) {
                        Snackbar.make(view, "Permission need for gallery", Snackbar.LENGTH_INDEFINITE).setAction("give permission", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                            }
                        }).show();

                    } else {
                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                    }
                } else {
                    Intent ıntentGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(ıntentGallery);
                }
            }
        });
    }

    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intentFromResult = result.getData();
                            if (intentFromResult != null) {
                                imageData = intentFromResult.getData();
                                try {

                                    if (Build.VERSION.SDK_INT >= 28) {
                                        ImageDecoder.Source source = ImageDecoder.createSource(UserAndFriendChatinPage.this.getContentResolver(),imageData);
                                        selectedImage = ImageDecoder.decodeBitmap(source);

                                    } else {
                                        selectedImage = MediaStore.Images.Media.getBitmap(UserAndFriendChatinPage.this.getContentResolver(),imageData);
                                    }
                                    uploadImage();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                });


        permissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result) {
                            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            activityResultLauncher.launch(intentToGallery);

                        } else {
                            Toast.makeText(UserAndFriendChatinPage.this,"Permisson needed!",Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    private void uploadImage(){
        UUID uuid = UUID.randomUUID();
        final String imageName = "sendimages/" + uuid + ".jpg";

        storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getImageUri(imageName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Hata: fotoğrafın yüklemesi sırasında hata çıktı");
                Toast.makeText(UserAndFriendChatinPage.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getImageUri(String imageName){
        StorageReference newReference = FirebaseStorage.getInstance().getReference(imageName);
        newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                binding.sendMessageEditText.setText(uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Hata: resmin url alamadım");
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

        database
                .child("messages")
                .child(userID)
                .child(messagedUserID)
                .child(messageID1)
                .setValue(message);
        database
                .child("messages")
                .child(messagedUserID)
                .child(userID)
                .child(messageID2)
                .setValue(message);

        binding.sendMessageEditText.setText("");
    }

    private void loadMessages(user friend) {

        DatabaseReference database = firebaseDatabase.getInstance().getReference();
        String userID = firebaseAuth.getInstance().getCurrentUser().getUid();
        String messagedUserID = friend.userID;

        DatabaseReference messagesRef = database.child("messages").child(userID).child(messagedUserID);

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    messageList.add(message);
                }
                Collections.sort(messageList, (m1, m2) -> Long.compare(m1.getTimestamp(), m2.getTimestamp()));
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}

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