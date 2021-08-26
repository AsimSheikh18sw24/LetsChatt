package com.example.letschatt.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.letschatt.Models.User;
import com.example.letschatt.databinding.ActivitySetupProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SetupProfileActivity extends AppCompatActivity {
    ActivitySetupProfileBinding binding;
    FirebaseAuth fAuth;
    FirebaseDatabase fDatabase;
    FirebaseStorage fStorage;
    Uri selectedImage;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating profile...");
        dialog.setCancelable(false);

        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        fStorage = FirebaseStorage.getInstance();

        binding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,45);
            }
        });
binding.saveProfile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String name = binding.nameBox.getText().toString();
        if(name.isEmpty()){
            binding.nameBox.setError("Please type a name");
        }
        //shows progress dialog
        dialog.show();
        if(selectedImage!=null){
            StorageReference reference = fStorage.getReference().child("Profiles").child(fAuth.getUid());
            reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUri = uri.toString();
                                String uid = fAuth.getUid();
                                String phone = fAuth.getCurrentUser().getPhoneNumber();
                                String name = binding.nameBox.getText().toString();

                                User user = new User(uid, name, phone, imageUri);
                                //inser data on firebase database
                                fDatabase.getReference()
                                        .child("users")
                                        .child(fAuth.getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                dialog.dismiss();
                                           Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                           startActivity(intent);
                                           finish();
                                            }
                                        });
                            }
                        });
                    }
                }
            });
        }else{
            String uid = fAuth.getUid();
            String phone = fAuth.getCurrentUser().getPhoneNumber();


            User user = new User(uid, name, phone, "No Image");
            //inser data on firebase database
            fDatabase.getReference()
                    .child("users")
                    .child(fAuth.getUid())
                    .setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
        }
    }
});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            if(data.getData() != null){
                selectedImage = data.getData();
                binding.imageView.setImageURI(selectedImage);

            }
        }
    }
}