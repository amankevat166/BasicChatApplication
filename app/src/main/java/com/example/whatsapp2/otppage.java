package com.example.whatsapp2;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.TimeUnit;

import Modules.DataModule;

public class otppage extends AppCompatActivity {

    EditText otpEdit1,otpEdit2,otpEdit3,otpEdit4,otpEdit5,otpEdit6;
    Button nextBtn;

    FirebaseAuth auth;
    String otpId;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otppage);
        otpEdit1=findViewById(R.id.edit_otp1);
        otpEdit2=findViewById(R.id.edit_otp2);
        otpEdit3=findViewById(R.id.edit_otp3);
        otpEdit4=findViewById(R.id.edit_otp4);
        otpEdit5=findViewById(R.id.edit_otp5);
        otpEdit6=findViewById(R.id.edit_otp6);
        nextBtn=findViewById(R.id.next_btn);
//        reSend=findViewById(R.id.resend_txt);
        auth=FirebaseAuth.getInstance();



        //method for automatic switch edittext
        editTextInput();

        //method caaling for sending verifying and other task for otp
//        otpSEnd();

        otpId=getIntent().getStringExtra("otpid");
        String mobText=getIntent().getStringExtra("mobnum");
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String otpEditTxt1=otpEdit1.getText().toString();
                String otpEditTxt2=otpEdit2.getText().toString();
                String otpEditTxt3=otpEdit3.getText().toString();
                String otpEditTxt4=otpEdit4.getText().toString();
                String otpEditTxt5=otpEdit5.getText().toString();
                String otpEditTxt6=otpEdit6.getText().toString();
                if (TextUtils.isEmpty(otpEditTxt1)||
                        TextUtils.isEmpty(otpEditTxt2)||
                        TextUtils.isEmpty(otpEditTxt3)||
                        TextUtils.isEmpty(otpEditTxt4)||
                        TextUtils.isEmpty(otpEditTxt5)||
                        TextUtils.isEmpty(otpEditTxt6)){
                    Toast.makeText(otppage.this, "please Fill all the box", Toast.LENGTH_SHORT).show();
                }
                else {

                        if (otpId!=null){
                            String code=otpEdit1.getText().toString()
                                    +otpEdit2.getText().toString()
                                    +otpEdit3.getText().toString()
                                    +otpEdit4.getText().toString()
                                    +otpEdit5.getText().toString()
                                    +otpEdit6.getText().toString();

                            otpSEnd(mobText);
                            //verify that otp is valid
                            PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(otpId,code);
                            signInWithPhoneAuthCredential(phoneAuthCredential);
                        }



                }
            }
        });

//        reSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////
//
//            }
//        });

    }

    public void editTextInput(){
        otpEdit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                otpEdit2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpEdit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                otpEdit3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpEdit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                otpEdit4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpEdit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                otpEdit5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpEdit5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                otpEdit6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void otpSEnd(String mobText){


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
                        Toast.makeText(otppage.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

//                    @Override
//                    public void onCodeSent( String s,
//                                            PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//
//                        super.onCodeSent(s,forceResendingToken);
//                        otpId=s;
//                        // temp=newverificationId;
//                        Toast.makeText(otppage.this, "otp send successfully", Toast.LENGTH_SHORT).show();
//
//                    }
                });


    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String mobText=getIntent().getStringExtra("mobnum");
                            Intent intent=new Intent(getApplicationContext(),UsernameProfile.class);
                            intent.putExtra("mobNum",mobText);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            nextBtn.setVisibility(View.INVISIBLE);
                            startActivity(intent);
                        } else {
                            nextBtn.setVisibility(View.VISIBLE);
                            Toast.makeText(otppage.this, "otp is not valid", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}