package com.ahmetymtkn.connectversenew;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Toast;

import com.ahmetymtkn.connectversenew.databinding.ActivityLoginPageBinding;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    private ActivityLoginPageBinding binding;

    private Intent intent;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private String email,password;


    ActivityResultLauncher<String> permissionLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        controlUser();

        binding.changepagetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, SignUpPage.class));
                finish();
            }
        });

        binding.resetpassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String emailAdress = binding.loginemail.getText().toString();
                if(emailAdress.isEmpty()){
                    Toast.makeText(LoginPage.this, "Please fill email", Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(emailAdress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isComplete()){
                                    Toast.makeText(LoginPage.this, "PLease control your email!", Toast.LENGTH_SHORT).show();}
                                    else {
                                        Toast.makeText(LoginPage.this, "Don't send", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        }
        private void controlUser(){
            if(user != null){
                startActivity(new Intent(LoginPage.this, ChatPages.class));
                finish();
            }
        }

        private boolean controlInput(){
            email = binding.loginemail.getText().toString();
            password = binding.loginPassword.getText().toString();
            if ( email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return false;
            }
            else {
                return true;
            }
        }

        public void loginMethod(View view){
            if (controlInput()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                user = mAuth.getCurrentUser();
                                if (user != null) {
                                    if (user.isEmailVerified()) {

                                        Toast.makeText(LoginPage.this, "Login successful!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(LoginPage.this,ChatPages.class));
                                        finish();

                                    } else {
                                        Toast.makeText(LoginPage.this, "You need to verify your email address. Please check your email.", Toast.LENGTH_LONG).show();
                                        mAuth.signOut();
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginPage.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }

}