package com.example.letschatt.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.letschatt.Activities.ChatActivity;
import com.example.letschatt.Models.User;
import com.example.letschatt.Activities.ProfilePictureActivity;
import com.example.letschatt.R;
import com.example.letschatt.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder>{
    Context context;
    ArrayList<User> users;
    public UsersAdapter(Context context, ArrayList<User> users){
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation,parent,false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = users.get(position);
        String senderId = FirebaseAuth.getInstance().getUid();
        String senderRoom = senderId + user.getUid();
        FirebaseDatabase.getInstance().getReference()
                .child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){


                        String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                        long time = snapshot.child("lastMsgTime").getValue(Long.class);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                        holder.binding.lastMessage.setText(lastMsg);
                        holder.binding.time.setText(dateFormat.format(new Date(time)));
                        }else{
                        holder.binding.lastMessage.setText("Tap to chat");

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //
        holder.binding.name.setText(user.getName());
        //on clicking profile image it will show the profile image
        holder.binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(context, ProfilePictureActivity.class);
                profileIntent.putExtra("image",user.getProfileImage());
                profileIntent.putExtra("name",user.getName());
                context.startActivity(profileIntent);
                Toast.makeText(context,"image clicked",Toast.LENGTH_LONG).show();
            }
        });
        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.profileImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name",user.getName());
                intent.putExtra("uid",user.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{
        RowConversationBinding binding;
        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
