package com.example.letschatt.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letschatt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
TextView tv;
FirebaseAuth fAuth;
OtpView otpView;
String verify_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);
        //progesss dialog will be shown




        getSupportActionBar().hide();
        tv=findViewById(R.id.textView);
        otpView=findViewById(R.id.otp_view);
        // for open keyboard automatically
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        otpView.requestFocus();
        Intent i = getIntent();
        verify_id = i.getStringExtra("verificationId");
        tv.setText("Verify "+i.getStringExtra("number"));

        fAuth = FirebaseAuth.getInstance();


        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential credential =
                        PhoneAuthProvider.getCredential(verify_id,otp);
                fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(getApplicationContext(),SetupProfileActivity.class));
                            finishAffinity();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}