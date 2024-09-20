package com.ahmetymtkn.connectversenew;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ahmetymtkn.connectversenew.databinding.ActivitySignUpPageBinding;
import com.ahmetymtkn.connectversenew.databinding.FragmentAllUserFriendsPageBinding;
import com.ahmetymtkn.connectversenew.model.user;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;


public class AllUserFriendsPage extends Fragment {

    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private ArrayList<user> userArrayList;
    private FragmentAllUserFriendsPageBinding binding;
    private userAdapter userAdapterclass;
    private ArrayList<user> filteredUserArrayList;

    public AllUserFriendsPage(){
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageReference = storage.getReference();
        userArrayList = new ArrayList<>();
        filteredUserArrayList = new ArrayList<>();



    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentAllUserFriendsPageBinding.inflate(inflater, container, false);

        binding.alluser.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapterclass = new userAdapter(userArrayList);
        binding.alluser.setAdapter(userAdapterclass);
        binding.findfriendedit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUserList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        getfriend();

        return binding.getRoot();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void getfriend() {
        db.collection("friendship").document(auth.getUid()).collection("friends").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                if (value != null) {
                    userArrayList.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()) {
                        Map<String, Object> data = snapshot.getData();
                        String downloadURL = (String) data.get("downloadurl");
                        String username = (String) data.get("username");
                        String userID = (String) data.get("userID");
                        user userInfo = new user(username, downloadURL,userID);
                        userArrayList.add(userInfo);


                        Log.d("Friend Info", "Username: " + username + ", downloadurl: " + downloadURL);
                    }
                    Collections.sort(userArrayList, new Comparator<user>() {
                        @Override
                        public int compare(user o1, user o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
                    userAdapterclass.notifyDataSetChanged();
                }
            }
        });


    }

    private void filterUserList(String query) {

        filteredUserArrayList.clear();
        if (query.isEmpty()) {
            filteredUserArrayList.addAll(userArrayList);
        } else {
            for (user user : userArrayList) {
                if (user.getName().toLowerCase().startsWith(query.toLowerCase())) {
                    filteredUserArrayList.add(user);
                }
            }
        }
        userAdapterclass.updateData(filteredUserArrayList);
    }



}