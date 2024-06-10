package com.example.comeback;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageButton imgSrchBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        imgSrchBtn=findViewById(R.id.img_srch_user);
        FragmentManager manager=getSupportFragmentManager();


        imgSrchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,SearchUser.class));
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int item=menuItem.getItemId();
                if(item==R.id.btm_nav_chat){
                    manager.beginTransaction().replace(R.id.fragmentContainerView,new ChatFragment()).addToBackStack(null).commit();
                }
                if(item==R.id.btm_nav_profile){
                    manager.beginTransaction().replace(R.id.fragmentContainerView,new ProfileFragment()).addToBackStack(null).commit();
                }
                return true;
            }
        });
    }
}