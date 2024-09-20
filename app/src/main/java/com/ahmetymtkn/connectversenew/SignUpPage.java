package com.ahmetymtkn.connectversenew;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import com.ahmetymtkn.connectversenew.databinding.ActivitySignUpPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class SignUpPage extends AppCompatActivity {

    Bitmap selectedImage;
    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    private ActivitySignUpPageBinding binding;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private FirebaseAuth auth;

    private String name,email,password,confirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerLauncher();

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageReference = storage.getReference();

        binding.changepageSigntext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpPage.this, LoginPage.class));
                finish();
            }
        });

        binding.addphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SignUpPage.this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpPage.this, android.Manifest.permission.READ_MEDIA_IMAGES)) {
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
                                        ImageDecoder.Source source = ImageDecoder.createSource(SignUpPage.this.getContentResolver(),imageData);
                                        selectedImage = ImageDecoder.decodeBitmap(source);
                                        binding.addimagetext.setVisibility(View.GONE);
                                        binding.addphoto.setImageBitmap(selectedImage);

                                    } else {
                                        selectedImage = MediaStore.Images.Media.getBitmap(SignUpPage.this.getContentResolver(),imageData);
                                        binding.addimagetext.setVisibility(View.GONE);
                                        binding.addphoto.setImageBitmap(selectedImage);
                                    }

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
                            Toast.makeText(SignUpPage.this,"Permisson needed!",Toast.LENGTH_LONG).show();
                        }
                    }

                });
    }

    private void controlName() {
        String username = binding.signupname.getText().toString();
        db.collection("users").whereEqualTo("username",username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    Toast.makeText(SignUpPage.this ,"User with this nickname already exists",Toast.LENGTH_LONG).show();
                } else {
                    newMemberSignUp();
                }
            }
        });
    }//kontrol edilmesi gerekiyor.

    private void controlInput(){
        name = binding.signupname.getText().toString();
        email = binding.signupemail.getText().toString();
        password = binding.signuppassword.getText().toString();
        confirmPassword = binding.signupconfirmpassword.getText().toString();
        if (name.isEmpty()|| email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || selectedImage == null ){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show();
        }
        else if (password.equals(confirmPassword) == false){
            Toast.makeText(this, "Passwords aren't equal.", Toast.LENGTH_LONG).show();
        } else {
            controlName();
        }
    }

    private void controlEmail(FirebaseUser user){
        user.sendEmailVerification()
                .addOnCompleteListener(verificationTask -> {
                    if (verificationTask.isSuccessful()) {
                        Toast.makeText(this, "Registration successful! Please verify your email address.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Verification email could not be sent. Registration is being canceled.", Toast.LENGTH_LONG).show();
                        user.delete().addOnCompleteListener(deleteTask -> {
                            if (deleteTask.isSuccessful()) {
                                Toast.makeText(this, "User deleted.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "User could not be deleted: " + deleteTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
    }


    private void newMemberSignUp(){
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    controlEmail(user);
                }
                else {
                    Toast.makeText(SignUpPage.this, "Error: User could not be retrieved.", Toast.LENGTH_LONG).show();
                }
                uploadImage();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Hata: kullanıcı oluşturamadım ");
                Toast.makeText(SignUpPage.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveUserInfo(Uri uri){
        String downloadUrl = uri.toString();
        String userID = auth.getCurrentUser().getUid();


        Map<String,Object> userInfo = new HashMap<>();
        userInfo.put("useremail", email);
        userInfo.put("downloadurl", downloadUrl);
        userInfo.put("username", name);
        userInfo.put("userID",userID);

        db.collection("users").document(userID).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(SignUpPage.this, "The user has been created successfully. Please log in!", Toast.LENGTH_LONG).show();
                auth.signOut();
                startActivity(new Intent(SignUpPage.this, LoginPage.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Hata: Bilgiler database kaydedilemedi.");
                Toast.makeText(SignUpPage.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void getImageUri(String imageName){
        StorageReference newReference = FirebaseStorage.getInstance().getReference(imageName);
        newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                saveUserInfo(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Hata: resmin url alamadım");
                Toast.makeText(SignUpPage.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadImage(){
        UUID uuid = UUID.randomUUID();
        final String imageName = "images/" + uuid + ".jpg";

        storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                getImageUri(imageName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("Hata: fotoğrafın yüklemesi sırasında hata çıktı");
                Toast.makeText(SignUpPage.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void signupMethod(View view){
        controlInput();


    }



}