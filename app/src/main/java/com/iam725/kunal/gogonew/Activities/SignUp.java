package com.iam725.kunal.gogonew.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.iam725.kunal.gogonew.R;

public class SignUp extends AppCompatActivity {

        FirebaseAuth auth;
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.sign_up);

                Button signUpButton = findViewById(R.id.signUp);
                final EditText emailText = findViewById(R.id.email_signup);
                final EditText passwordText = findViewById(R.id.password_signup);
                auth = FirebaseAuth.getInstance();

                signUpButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                final String email = emailText.getText().toString().trim();
                                String password = passwordText.getText().toString().trim();

                                if (TextUtils.isEmpty(email)) {
//                                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                                        emailText.setError("Enter email address");
                                    return;
                                }

                                if (TextUtils.isEmpty(password)) {
//                                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                                     passwordText.setError("Enter password");
                                    return;
                                }
                                if (password.length() < 6) {
                                        Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                }
                                auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (!task.isSuccessful()) {
                                                        Toast.makeText(SignUp.this, "Sign up failed",
                                                                Toast.LENGTH_SHORT).show();
                                                } else {
                                                        Toast.makeText(SignUp.this, "Email id \""+email+"\" is successfully registered !", Toast.LENGTH_SHORT).show();
                                                        startActivity(new Intent(SignUp.this, Login.class));
                                                        finish();
                                                }
                                        }
                                });
                        }
                });
        }
}
