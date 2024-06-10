package com.example.comeback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginOTPActivity extends AppCompatActivity {
    TextInputLayout tipLoginOtp;
    ProgressBar pgOtp;
    Button btnVerify, btnResend;
    ImageView imgVerify;
    FirebaseAuth auth;

    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_otpactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
       String phonenumber = getIntent().getStringExtra("phno");
        auth=FirebaseAuth.getInstance();
        tipLoginOtp = findViewById(R.id.tip_otp);
        pgOtp=findViewById(R.id.pg_otp);
        btnVerify = findViewById(R.id.btn_otp_verify);
        btnResend = findViewById(R.id.btn_otp_resend);
        imgVerify = findViewById(R.id.img_verification_tick);

        sendOtp(phonenumber,false);



        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String enteredOtp=tipLoginOtp.getEditText().getText().toString();
              PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
              auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful())
                      {
                          imgVerify.setVisibility(View.VISIBLE);
                          Intent usernameIntent=new Intent(LoginOTPActivity.this,LoginUserDetailsActivity.class);
                          usernameIntent.putExtra("phno",phonenumber);
                          startActivity(usernameIntent);
                          finish();
                      }
                      else
                          tipLoginOtp.getEditText().setError(task.getException().getMessage());
                  }
              });

            }
        });
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendOtp(phonenumber,true);
                pgOtp.setVisibility(View.VISIBLE);
            }
        });
    }

    private void sendOtp(String phno, boolean isResend) {
        PhoneAuthOptions.Builder builder=PhoneAuthOptions.newBuilder(auth)
                .setActivity(this)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setPhoneNumber(phno)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        imgVerify.setVisibility(View.VISIBLE);
                        Intent usernameIntent=new Intent(LoginOTPActivity.this,LoginUserDetailsActivity.class);
                        startActivity(usernameIntent);
                        finish();
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        if(e instanceof FirebaseTooManyRequestsException)
                            Toast.makeText(LoginOTPActivity.this, "too many request for "+phno, Toast.LENGTH_SHORT).show();
                        else if(e instanceof FirebaseNetworkException)
                            Toast.makeText(LoginOTPActivity.this, "please check your network", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode=s;
                        resendingToken=forceResendingToken;
                        pgOtp.setVisibility(View.GONE);
                        Toast.makeText(LoginOTPActivity.this, "OTP sent to "+phno, Toast.LENGTH_SHORT).show();
                    }
                });
        if(isResend)
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        else
            PhoneAuthProvider.verifyPhoneNumber(builder.build());

    }
}