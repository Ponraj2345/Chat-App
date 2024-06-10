package com.example.comeback;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

       if(getIntent().getExtras()!=null){
           new Thread(new Runnable() {
               @Override
               public void run() {
                   try{
                       Thread.sleep(1500);
                       String userId=getIntent().getExtras().getString("userId");
                       Intent chat=new Intent(SplashActivity.this,ChatActivity.class);
                       chat.putExtra("uid",userId);
                       startActivity(chat);
                       finish();
                   }
                   catch (Exception e){
                       //nothing to catch here
                   }
               }
           }).start();

       }
       else{
           new Thread(new Runnable() {
               @Override
               public void run() {
                   try{
                       Thread.sleep(1500);
                       startActivity(new Intent(SplashActivity.this,LoginPhoneNumberActivity.class));
                       finish();
                   }
                   catch (Exception e){
                       //nothing to catch here
                   }
               }
           }).start();
       }
    }
}