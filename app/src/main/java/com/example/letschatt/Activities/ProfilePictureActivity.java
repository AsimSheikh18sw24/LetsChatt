package com.example.letschatt.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.letschatt.Activities.MainActivity;
import com.example.letschatt.R;


public class ProfilePictureActivity extends AppCompatActivity {
ImageView back,profile;
TextView nameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_picture);
        getSupportActionBar().hide();
        Intent i = getIntent();
        String image = i.getStringExtra("image");
        String name =i.getStringExtra("name");

        nameTv = findViewById(R.id.name);
        back = findViewById(R.id.back);
        profile = findViewById(R.id.profileImageShower);

        nameTv.setText(name);
        Glide.with(this).load(image)
                .placeholder(R.drawable.avatar)
                .into(profile);
        //go to back activity
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
        //choose image for update



    }
}