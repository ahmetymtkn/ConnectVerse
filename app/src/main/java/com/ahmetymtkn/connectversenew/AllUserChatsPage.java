package com.ahmetymtkn.connectversenew;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahmetymtkn.connectversenew.databinding.FragmentAllUserChatsPageBinding;
import com.ahmetymtkn.connectversenew.databinding.FragmentAllUserFriendsPageBinding;
import com.ahmetymtkn.connectversenew.model.user;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class AllUserChatsPage extends Fragment {

    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private FragmentAllUserChatsPageBinding binding;

    public AllUserChatsPage(){
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageReference = storage.getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAllUserChatsPageBinding.inflate(inflater, container, false);

        binding.powerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intent = new Intent(getContext(), LoginPage.class);
                startActivity(intent);

            }
        });

        return binding.getRoot();

    }
}