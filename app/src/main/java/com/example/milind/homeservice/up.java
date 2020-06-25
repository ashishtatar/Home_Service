package com.example.milind.homeservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class up extends AppCompatActivity {
    TextInputLayout etname,etphone,etemail,etpass;
    Button btnReg;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String name,email,phone,password;
    ProgressBar registerP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up);
        etemail=(TextInputLayout) findViewById(R.id.etemail);
        etphone=(TextInputLayout)findViewById(R.id.etphone);
        etname=(TextInputLayout)findViewById(R.id.etname);
        etpass=(TextInputLayout)findViewById(R.id.etpass);
        btnReg=(Button)findViewById(R.id.btnReg);
        registerP=(ProgressBar)findViewById(R.id.registerP);


       final String name=etname.getEditText().getText().toString();
       final String phone=etphone.getEditText().getText().toString();
       final String email = etemail.getEditText().getText().toString().trim();
       final String password = etpass.getEditText().getText().toString().trim();

        firebaseAuth=FirebaseAuth.getInstance();
       // FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("user");

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();

                //Toast.makeText(getApplicationContext(),"Hii",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void registerUser() {

        final String name=etname.getEditText().getText().toString();
        final String phone=etphone.getEditText().getText().toString();
        final String email = etemail.getEditText().getText().toString().trim();
        final String password = etpass.getEditText().getText().toString().trim();

        if (name.length()==0) {
            etname.setError("Name is required");
            etname.requestFocus();
            return;
        }
        if (phone.length()==0) {
            etemail.setError("Phone no is required");
            etemail.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etemail.setError("Email is required");
            etemail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.setError("Enter vaild Email");
            etemail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            etpass.setError("Password is required");
            etpass.requestFocus();
            return;
        }
        if (password.length() < 6) {
            etpass.setError("Min. Character is Required");
            etpass.requestFocus();
            return;
        }
        else{
            registerP.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User user = new User(name,phone,email);

                        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    sendemailverfication();
                                    registerP.setVisibility(View.GONE);
                                }else
                                    Toast.makeText(getApplicationContext(), "Unsuccesful", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }else
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void sendemailverfication()
    {
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(), "Registration successfully,verification email sent", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        startActivity(new Intent(up.this,in.class));

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Email not sent", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
