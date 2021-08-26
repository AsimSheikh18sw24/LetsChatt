package com.example.letschatt.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.letschatt.Adapters.MessagesAdapter;
import com.example.letschatt.Models.Message;
import com.example.letschatt.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;
    String senderRoom, recieverRoom;
    FirebaseDatabase fDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();



        String name = getIntent().getStringExtra("name");
        String recieverUid = getIntent().getStringExtra("uid");
        String senderUid = FirebaseAuth.getInstance().getUid();

        senderRoom = senderUid+recieverUid;
        recieverRoom = recieverUid+senderUid;
        adapter = new MessagesAdapter(this,messages,senderRoom,recieverRoom);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        fDatabase = FirebaseDatabase.getInstance();
        fDatabase.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot snapshot1: snapshot.getChildren()){
                            Message message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());
                            messages.add(message);

                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = binding.messageBox.getText().toString();

                Date date = new Date();
                Message message = new Message(messageText,senderUid,date.getTime());
                binding.messageBox.setText("");
                String randomKey = fDatabase.getReference().push().getKey();
                HashMap<String, Object> lastMsgObj= new HashMap<>();
                lastMsgObj.put("lastMsg",message.getMessage());
                lastMsgObj.put("lastMsgTime",date.getTime());
                fDatabase.getReference().child("chats")
                        .child(senderRoom)
                        .updateChildren(lastMsgObj);
                fDatabase.getReference().child("chats")
                        .child(recieverRoom)
                        .updateChildren(lastMsgObj);
                fDatabase.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        fDatabase.getReference().child("chats")
                                .child(recieverRoom)
                                .child("messages")
                                .child(randomKey)
                                .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        });


                    }
                });
            }
        });
        //sets the name of the person on top
        getSupportActionBar().setTitle(name);
        //brings back button on action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
    // when back is pressed back to the main activity and finish chat activity
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}