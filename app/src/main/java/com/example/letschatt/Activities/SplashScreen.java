package com.example.letschatt.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.letschatt.Activities.MainActivity;
import com.example.letschatt.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {
ImageView appIcon;
TextView appName,vendorName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        appIcon=findViewById(R.id.app_icon);
        appName=findViewById(R.id.app_name);
        vendorName=findViewById(R.id.vendorName);

        YoYo.with(Techniques.Landing).duration(6000).repeat(0).playOn(appIcon);
        YoYo.with(Techniques.Bounce).duration(6000).repeat(2).playOn(appName);
        YoYo.with(Techniques.FadeInRight).duration(6000).repeat(1).playOn(vendorName);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                    else{
                        startActivity(new Intent(getApplicationContext(), PhoneNumberActivity.class));
                        finish();
                    }
            }
        },5000);
    }
}