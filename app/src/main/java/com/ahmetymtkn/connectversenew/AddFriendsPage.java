package com.ahmetymtkn.connectversenew;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ahmetymtkn.connectversenew.databinding.FragmentAddFriendsPageBinding;
import com.ahmetymtkn.connectversenew.model.user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


public class AddFriendsPage extends Fragment {

    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private ArrayList<user> invitationArrayList;
    private FragmentAddFriendsPageBinding binding;
    private invitationAdapter invitationAdapterClass;
    private String SearchinguserID;

    public AddFriendsPage(){
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageReference = storage.getReference();
        invitationArrayList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddFriendsPageBinding.inflate(inflater, container, false);

        binding.invitationrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        invitationAdapterClass= new invitationAdapter(invitationArrayList);
        binding.invitationrecyclerview.setAdapter(invitationAdapterClass);
        binding.inviteuserlayout.setVisibility(View.GONE);

        binding.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchuser();
            }
        });

        binding.addusericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invitenewfriend();
            }
        });
        getfriend();

        return binding.getRoot();
    }


    private void getfriend(){
        db.collection("friendship")
                .document(auth.getUid())
                .collection("adduser")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (value != null){
                    invitationArrayList.clear();
                    for (DocumentSnapshot snapshot: value.getDocuments()){
                        Map<String, Object> data = snapshot.getData();
                        String downloadURL = (String) data.get("downloadurl");
                        String userID = (String) data.get("userID");
                        String username = (String) data.get("username");

                        user userInfo = new user(username, downloadURL,userID);

                        invitationArrayList.add(userInfo);
                    }
                    invitationAdapterClass.notifyDataSetChanged();
                }
            }
        });
        Collections.sort(invitationArrayList, new Comparator<user>() {
            @Override
            public int compare(user o1, user o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    private void invitenewfriend(){
        db.collection("users")
                .document(auth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String downloadURL = documentSnapshot.getString("downloadurl");
                            String username = documentSnapshot.getString("username");
                            String userID = documentSnapshot.getString("userID");

                            Map<String,Object> userInfo = new HashMap<>();
                            userInfo.put("downloadurl", downloadURL);
                            userInfo.put("username", username);
                            userInfo.put("userID",userID);

                            db.collection("friendship")
                                    .document(SearchinguserID)
                                    .collection("adduser")
                                    .document(auth.getUid())
                                    .set(userInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    binding.inviteuserlayout.setVisibility(View.GONE);
                                    binding.searcuseredittext.setText("");

                                    Toast.makeText(getContext(),"Friend invitation sent.",Toast.LENGTH_LONG);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Firestore", "Hata: " + e.getLocalizedMessage());

                                }
                            });

                        } else {
                            Toast.makeText(getContext(), "Kullanıcı verisi bulunamadı", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Firestore", "Hata: " + e.getLocalizedMessage());
                    }
                });

    }

    private void searchuser() {
        String searchText = binding.searcuseredittext.getText().toString();

        db.collection("users")
                .whereEqualTo("username", searchText)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            SearchinguserID = document.getString("userID");
                            binding.addusername.setText(document.getString("username"));
                            Picasso.get().load(document.getString("downloadurl")).into(binding.adduserimage);
                        }
                        binding.inviteuserlayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getContext(), "No user found with this username", Toast.LENGTH_SHORT).show();
                        binding.inviteuserlayout.setVisibility(View.GONE);
                    }
                } else {
                    Log.d("Firestore", "Hata: ", task.getException());
                }
            }
        });
    }




}