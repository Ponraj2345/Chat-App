package com.example.comeback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Document;

import java.util.HashMap;

public class LoginUserDetailsActivity extends AppCompatActivity {

    Button btnLetMeIn;
    TextInputLayout tipLoginUserDetails;
    FirebaseAuth auth;
    ProgressBar pgLoginUserDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_user_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String phonenumber=getIntent().getStringExtra("phno");
        btnLetMeIn=findViewById(R.id.btn_let_me_in);
        pgLoginUserDetails=findViewById(R.id.pg_login_user_details);
        tipLoginUserDetails=findViewById(R.id.tip_login_user_details);
        auth=FirebaseAuth.getInstance();

        FirebaseFirestore.getInstance().collection("user details").document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if(task.isSuccessful()){
                   DocumentSnapshot snapshot=task.getResult();
                   if(snapshot.exists())
                   {
                       tipLoginUserDetails.getEditText().setText(snapshot.getString("username"));
                       pgLoginUserDetails.setVisibility(View.GONE);
                   }
                   else{
                       Toast.makeText(LoginUserDetailsActivity.this, "choose username", Toast.LENGTH_SHORT).show();
                       pgLoginUserDetails.setVisibility(View.GONE);
                   }
               }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginUserDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnLetMeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tipLoginUserDetails.getEditText().getText().toString().isEmpty()||tipLoginUserDetails.getEditText().getText().toString().length()<5){
                    tipLoginUserDetails.getEditText().setError("username must be atleast 5 chars");
                    return;
                }
                HashMap<String,String> map=new HashMap<>();
                map.put("username",tipLoginUserDetails.getEditText().getText().toString().trim());
                map.put("phone number",phonenumber.trim());
                map.put("uid",auth.getUid());
                getFcmToken();
                DocumentReference reference=FirebaseFirestore.getInstance().collection("user details").document(auth.getUid());
                reference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginUserDetailsActivity.this, "Welcome to the Party!!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginUserDetailsActivity.this, HomeActivity.class));
                                finish();
                            }

                            else
                                Toast.makeText(LoginUserDetailsActivity.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
        });

    }

    void getFcmToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String token=task.getResult();
                    FirebaseFirestore.getInstance().collection("user details").document(auth.getUid()).update("fcm token",token);
                }
            }
        });
    }
}