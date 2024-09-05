package com.ahmetymtkn.connectversenew;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ahmetymtkn.connectversenew.databinding.FragmentAddFriendsPageBinding;
import com.ahmetymtkn.connectversenew.databinding.FragmentAllUserFriendsPageBinding;
import com.ahmetymtkn.connectversenew.model.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;


public class AddFriendsPage extends Fragment {

    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private ArrayList<user> invitationArrayList;
    private FragmentAddFriendsPageBinding binding;
    private invitationAdapter invitationAdapterClass;

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
        binding.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchuser();
            }
        });

        binding.relativeLayout2.setVisibility(View.GONE);
        binding.addusername.setVisibility(View.GONE);
        binding.addusericon.setVisibility(View.GONE);

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
        db.collection("friendship").document(auth.getUid()).collection("adduser").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                }
            }
        });
        
    }

    private void invitenewfriend(){

    }

    private void searchuser(){

    }


}