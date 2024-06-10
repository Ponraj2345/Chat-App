package com.example.comeback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    CountryCodePicker ccpPhoneNumber;
    TextInputLayout tipPhoneNumber;
    Button btnPhno;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_phone_number);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth=FirebaseAuth.getInstance();
        ccpPhoneNumber=findViewById(R.id.ccp_phno);
        tipPhoneNumber=findViewById(R.id.tip_phno);
        btnPhno =findViewById(R.id.btn_phno);

        if(auth.getCurrentUser()!=null)
        {
            startActivity(new Intent(LoginPhoneNumberActivity.this, HomeActivity.class));
            finish();
        }
        ccpPhoneNumber.registerCarrierNumberEditText(tipPhoneNumber.getEditText());
        btnPhno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ccpPhoneNumber.isValidFullNumber()){
                    Intent otpIntent=new Intent(LoginPhoneNumberActivity.this,LoginOTPActivity.class);
                    otpIntent.putExtra("phno",ccpPhoneNumber.getFullNumberWithPlus());
                    startActivity(otpIntent);
                    finish();
                }
                else
                    tipPhoneNumber.getEditText().setError("invalid number");

            }
        });
    }
}