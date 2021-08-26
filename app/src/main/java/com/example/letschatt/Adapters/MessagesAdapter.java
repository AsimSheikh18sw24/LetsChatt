package com.example.letschatt.Adapters;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschatt.Models.Message;
import com.example.letschatt.R;
import com.example.letschatt.databinding.ItemRecieveBinding;
import com.example.letschatt.databinding.ItemSendBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter{
    Context context;
    ArrayList<Message> messages;
    final  int ITEM_SEND = 1;
    final  int ITEM_RECIEVE = 2;
    String senderRoom;
    String receiverRoom;
    public MessagesAdapter(Context context, ArrayList<Message> messages, String senderRoom, String receiverRoom){

        this.messages=messages;
        this.context=context;
        this.senderRoom=senderRoom;
        this.receiverRoom=receiverRoom;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND){
            View v = LayoutInflater.from(context).inflate(R.layout.item_send,parent,false);
            return new sendViewHolder(v);
        }else{
            View v = LayoutInflater.from(context).inflate(R.layout.item_recieve,parent,false);
            return new RecieverViewHolder(v);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SEND;
        }
        else {
            return ITEM_RECIEVE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        int reaction[]=new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reaction)
                .build();
        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass() == sendViewHolder.class){
                sendViewHolder viewHolder = (sendViewHolder)holder;
                viewHolder.binding.feeling.setImageResource(reaction[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);

            }else{
                RecieverViewHolder viewHolder = (RecieverViewHolder)holder;
                viewHolder.binding.feeling.setImageResource(reaction[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            message.setFeeling(pos);
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId())
                    .setValue(message);
            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId())
                    .setValue(message);
            return true; // true is closing popup, false is requesting a new selection
        });

        if(holder.getClass() == sendViewHolder.class){
            sendViewHolder viewHolder = (sendViewHolder)holder;
            viewHolder.binding.message.setText(message.getMessage());

            if(message.getFeeling()>=0){
                //message.setFeeling(reaction[(int)message.getFeeling()]);
                viewHolder.binding.feeling.setImageResource(reaction[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);

            }
            else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v , event);
                    return false;
                }
            });
        }else{
            RecieverViewHolder viewHolder = (RecieverViewHolder)holder;
            viewHolder.binding.message.setText(message.getMessage());

            if(message.getFeeling()>=0){
                //message.setFeeling(reaction[(int)message.getFeeling()]);
                viewHolder.binding.feeling.setImageResource(reaction[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);

            }
            else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }
            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popup.onTouch(v , event);
                    return false;

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class sendViewHolder extends RecyclerView.ViewHolder{
        ItemSendBinding binding;
        public sendViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendBinding.bind(itemView);
        }
    }

    public class RecieverViewHolder extends RecyclerView.ViewHolder{
        ItemRecieveBinding binding;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecieveBinding.bind(itemView);

        }
    }

}
