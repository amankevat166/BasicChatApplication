package com.example.whatsapp2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class mobnum_page extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    ProgressBar progressBar;
    EditText mobNumber;
    Button otpButton;
    FirebaseAuth auth;
    String otpId;


    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), homePage.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mobnum_page);


        countryCodePicker=findViewById(R.id.countrycode);
        mobNumber=findViewById(R.id.mob_num);
        otpButton=findViewById(R.id.send_otp_btn);
        auth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressbar);


        countryCodePicker.registerCarrierNumberEditText(mobNumber);


        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobText=mobNumber.getText().toString();

                if (TextUtils.isEmpty(mobText) || !countryCodePicker.isValidFullNumber()){
                    mobNumber.requestFocus();
                    mobNumber.setError("please enter valid mobilenumber");
                }

                else {
                    otpSEnd();
                }

            }
        });
    }

    public void otpSEnd(){

        String mobText=countryCodePicker.getFullNumberWithPlus().trim();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(

                mobText,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(mobnum_page.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent( String s,
                                            PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                        super.onCodeSent(s,forceResendingToken);
                        otpId=s;
                        Intent intent=new Intent(getApplicationContext(), otppage.class);
                        intent.putExtra("otpid",otpId);
                        intent.putExtra("mobnum",countryCodePicker.getFullNumberWithPlus().trim());
                        startActivity(intent);
                        Toast.makeText(mobnum_page.this, "otp send successfully", Toast.LENGTH_SHORT).show();

                    }

                });


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            Intent intent=new Intent(getApplicationContext(), UsernameProfile.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                            startActivity(intent);
                        } else {
                            Toast.makeText(mobnum_page.this, "otp is not valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}