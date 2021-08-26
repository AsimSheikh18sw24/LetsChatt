package com.example.letschatt.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.letschatt.Models.User;
import com.example.letschatt.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class UpdateProfileActivity extends AppCompatActivity {
ImageView iv,editName;
TextView nameTv , phoneTv;
/*Firebase RUles
{
  "rules": {
    ".read": "now < 1627844400000",  // 2021-8-2
    ".write": "now < 1627844400000",  // 2021-8-2
  }
}
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getSupportActionBar().setTitle("Update Profile");
        Intent i = getIntent();
        String image = i.getStringExtra("image"),
        name = i.getStringExtra("name"),
        phone = i.getStringExtra("phone");

        iv = findViewById(R.id.profile);
        editName = findViewById(R.id.editName);

        nameTv = findViewById(R.id.userName);
        phoneTv = findViewById(R.id.phoneNumber);
        Glide.with(this).load(image)
                .placeholder(R.drawable.avatar)
                .into(iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });//end of profile image onClickListener
        nameTv.setText(name);
        phoneTv.setText(phone);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,45);
            }
        });
        //listener on pencil to update user name
        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameOnFirebase();
            }
        });//end of onclick listener
    }

    public void editNameOnFirebase(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateProfileActivity.this);
        alertDialog.setTitle("Name");
        alertDialog.setMessage("Enter Your new name");

        final EditText input = new EditText(UpdateProfileActivity.this);
        input.setPadding(20,20,20,20);
        input.setHint("username");
        input.setTextColor(getResources().getColor(R.color.white));
        input.setBackground(getResources().getDrawable(R.drawable.edit_text_in_dialogue_design));
        input.setWidth(50);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(5,0,5,0);

        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_person);
        alertDialog.setPositiveButton("UPDATE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    String  name = input.getText().toString();
                    updateOnFirebase(name);
                    }
                });

        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
//3113244295
public void showToast(String s){
    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
}
public void updateOnFirebase(String s){
    ProgressDialog pd = new ProgressDialog(this);
    pd.setTitle("Updating username");
    pd.setCancelable(false);
    pd.show();

    DatabaseReference df = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name");
    df.setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){pd.dismiss();showToast("user name updated successfully");
            nameTv.setText(s);}
            else{
                pd.dismiss();showToast("user name can not be updated ");
            }
        }
    });
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            if(data.getData() != null){
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                Uri selectedImage = data.getData();
                ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("updating data");
                dialog.setCancelable(false);
                dialog.show();
                FirebaseStorage fStorage = FirebaseStorage.getInstance();
                DatabaseReference fDatabase = FirebaseDatabase.getInstance().getReference("users").child(fAuth.getCurrentUser().getUid());
                StorageReference reference = fStorage.getReference().child("Profiles").child(fAuth.getUid());
                reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUri = uri.toString();
                                    String uid = fAuth.getCurrentUser().getUid();
                                    String phone = fAuth.getCurrentUser().getPhoneNumber();

                                    User user = new User(uid, nameTv.getText().toString(), phone, imageUri);
                                    //inser data on firebase database
                                    fDatabase.setValue(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    dialog.dismiss();
                                                    showToast("data updated successfully");

                                                }
                                            });
                                }
                            });
                        }
                    }
                });

            }
        }
    }
}
