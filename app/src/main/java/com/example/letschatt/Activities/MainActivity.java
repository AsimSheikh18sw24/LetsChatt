package com.example.letschatt.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.letschatt.Adapters.TopStatusAdapter;
import com.example.letschatt.Models.Status;
import com.example.letschatt.Models.UserStatus;
import com.example.letschatt.R;
import com.example.letschatt.Models.User;
import com.example.letschatt.Adapters.UsersAdapter;
import com.example.letschatt.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseDatabase fDatabase;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    ProgressDialog dialog;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading status...");
        dialog.setCancelable(false);
        fDatabase = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        userStatuses = new ArrayList<>();

        fDatabase.getReference().child("users").child(FirebaseAuth.getInstance().getUid()).
        addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user =snapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        usersAdapter = new UsersAdapter(this, users);
        statusAdapter = new TopStatusAdapter(this,userStatuses);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusList.setLayoutManager(linearLayoutManager);
        binding.statusList.setAdapter(statusAdapter);
        binding.recyclerView.setAdapter(usersAdapter);

        fDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    users.add(user);
                }
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fDatabase.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists()){
                for(DataSnapshot storySnapshot: snapshot.getChildren()){
                    UserStatus status = new UserStatus();
                    status.setName(storySnapshot.child("name").getValue(String.class));
                    status.setName(storySnapshot.child("profileImage").getValue(String.class));
                    status.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));
                    ArrayList<Status> statuses = new ArrayList<>();
                    for(DataSnapshot statusSnapshot: storySnapshot.child("statuses").getChildren()){
                        //Status sampleStatus = statusSnapshot.getValue(Status.class);
                        Status sampleStatus = statusSnapshot.getValue(Status.class);
                        statuses.add(sampleStatus);

                    }
                    status.setStatuses(statuses);
                    userStatuses.add(status);
                }
                statusAdapter.notifyDataSetChanged();
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.status:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,75);
                        break;
                }
                return false;
            }
        });
    }
    //getting image through request code

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
            if(data.getData()!=null){
                dialog.show();
                FirebaseStorage storage = FirebaseStorage.getInstance();
                Date date = new Date();
                StorageReference reference = storage.getReference().child("status")
                .child(date.getTime()+"");
                reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UserStatus userStatus=  new UserStatus();
                                userStatus.setName(user.getName());
                                userStatus.setProfileImage(user.getProfileImage());
                                userStatus.setLastUpdated(date.getTime());
                                HashMap<String, Object> obj = new HashMap<>();
                                obj.put("name",user.getName());
                                obj.put("profileImage",userStatus.getProfileImage());
                                obj.put("lastUpdated",userStatus.getLastUpdated());

                                String imageUrl = uri.toString();
                                Status status = new Status(imageUrl,userStatus.getLastUpdated());

                                fDatabase.getReference().child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .updateChildren(obj);
                                fDatabase.getReference().child("stories")
                                        .child(FirebaseAuth.getInstance().getUid())
                                        .child("statuses")
                                        .push()
                                        .setValue(status);
                                dialog.dismiss();
                            }
                        });
                    }
                });

            }
        }
    }

    //creating options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topmenu,menu);
        return super.onCreateOptionsMenu(menu);

    }
    //handling on menu click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                Toast.makeText(getApplicationContext(),"Search Clicked",Toast.LENGTH_LONG).show();
                break;
            case R.id.groups:
                Toast.makeText(getApplicationContext(),"groups Clicked",Toast.LENGTH_LONG).show();
                break;
            case R.id.invite:
                Toast.makeText(getApplicationContext(),"invite Clicked",Toast.LENGTH_LONG).show();
                break;
            case R.id.settings:
                Intent updateIntent = new Intent(getApplicationContext(),UpdateProfileActivity.class);

                updateIntent.putExtra("image",user.getProfileImage());
                updateIntent.putExtra("name",user.getName());
                updateIntent.putExtra("phone",user.getPhoneNumber());
                startActivity(updateIntent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}