package com.example.letschatt.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.letschatt.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumberActivity extends AppCompatActivity {
EditText phone_number;
Button button;
FirebaseAuth fAuth;
ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        getSupportActionBar().hide();
        fAuth=FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending OTP...");
        dialog.setCancelable(false);


        phone_number = findViewById(R.id.phone_number);
        button = findViewById(R.id.continu);
        phone_number.requestFocus();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),OTPActivity.class);
                if(TextUtils.isEmpty(phone_number.getText().toString().trim())){
                    phone_number.setError("Please insert phone number");
                }
                else{
                    dialog.show();
                    button.setVisibility(View.GONE);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+92"+phone_number.getText().toString(),
                            60,
                            TimeUnit.SECONDS,
                            PhoneNumberActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                 }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                dialog.dismiss();
                                button.setVisibility(View.VISIBLE);
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationCode, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    dialog.dismiss();
                                    button.setVisibility(View.VISIBLE);
                                    i.putExtra("number",phone_number.getText().toString().trim());
                                    i.putExtra("verificationId",verificationCode);
                                    startActivity(i);
                                    super.onCodeSent(verificationCode, forceResendingToken);

                                }
                            }
                    );

                }
            }
        });


    }
}