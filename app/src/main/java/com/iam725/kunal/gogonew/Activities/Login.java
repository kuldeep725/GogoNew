package com.iam725.kunal.gogonew.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
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
import com.iam725.kunal.gogonew.R;
import com.iam725.kunal.gogonew.Utilities.NetworkChangeReceiver;

public class Login extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private String email;
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    //private TextView mStatusTextView;
    //private TextView mDetailTextView;
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    Button loginButton;
    DatabaseReference requestDatabaseReference;
    FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main);

        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.input_email);
        passwordEditText = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        //get Reference to Request node of firebase database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        requestDatabaseReference = mFirebaseDatabase.getReference().child("Requests");

        final Intent i = new Intent(this, NetworkChangeReceiver.class);
        sendBroadcast(i);
        Button forgotPassword = findViewById (R.id.forgot_password);
        forgotPassword.setPaintFlags(forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                final EditText input = new EditText(Login.this);
                input.setHint("Enter email address");
                final float scale = getResources().getDisplayMetrics().density;
                final float dps = 13;
                int pixels = (int) (dps * scale + 0.5f);                //converting 40 dp into pixels
                input.setPadding(pixels, pixels, pixels, pixels);
                input.setHintTextColor(Color.parseColor("#777777"));
                builder.setView(input);
                builder.setTitle("Forgot Password");
                builder.setMessage("A password reset mail will be sent to this email id");
                builder.setIcon(R.drawable.ic_mail_black_24dp);
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!input.getText().toString().isEmpty()) {
                                FirebaseAuth.getInstance().sendPasswordResetEmail(input.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Email sent.");
                                                    Toast.makeText(Login.this, "Email Sent to " + input.getText().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                            else {
                                Toast.makeText(Login.this, "Please enter the email id", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                builder.setNegativeButton("Cancel", null).show();

            }
        });

        Button signUp = findViewById (R.id.sign_up);
        signUp.setPaintFlags(signUp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(Login.this, SignUp.class);
//                startActivity(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                final EditText input = new EditText(Login.this);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                input.setHint("Enter email address");
                final float scale = getResources().getDisplayMetrics().density;
                final float dps = 13;
                int pixels = (int) (dps * scale + 0.5f);                //converting 40 dp into pixels
                input.setPadding(pixels, pixels, pixels, pixels);
                input.setHintTextColor(Color.parseColor("#777777"));
                builder.setView(input);
                builder.setTitle("Request for Sign UP");
                builder.setMessage("A password will be sent to this email id once admin approves it");
                builder.setIcon(R.drawable.ic_mail_black_24dp);
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = input.getText().toString();
                        if (!email.isEmpty()) {
//                            FirebaseAuth.getInstance().sendPasswordResetEmail(input.getText().toString())
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                            if (task.isSuccessful()) {
                            if(email.matches(emailPattern)){
                                email = email.replace(".", ",");
                                Log.d(TAG, "Request sent.");
                                    requestDatabaseReference.child(email).setValue("Pending")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Log.d(TAG, "Request sent1");
                                                        Toast.makeText(Login.this, "Request sent", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        Log.d(TAG, "Request Failed");
                                                        Toast.makeText(Login.this, "Error Occurred", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                            }
                            else{
                                Toast.makeText(Login.this, "Invalid email id", Toast.LENGTH_SHORT).show();
                            }
//                                            }
//                                        }
//                                    });
                        }
                        else {
                            Toast.makeText(Login.this, "Please enter the email id", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.setNegativeButton("Cancel", null).show();

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                    loginButton.setBackground(getResources().getDrawable(R.drawable.sign_up_button_pressed));
                disableLoginButton();
                signIn ();
            }
        });
        //mStatusTextView = (TextView) findViewById(R.id.status);
       // mDetailTextView = (TextView) findViewById(R.id.detail);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MapsActivity.activityVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        MapsActivity.activityVisible = true;
    }
    public boolean isInternetOn () {

//                Log.e(TAG, "isInternetOn fired");
        ConnectivityManager connec =
                (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ) {

            // if connected with internet
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  ) {

            return false;
        }
        return  false;

    }
    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onStart() {
        super.onStart();

//        new MapsActivity().showInternetStatus();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            }
        });

        if (currentUser != null) {
            Intent i = new Intent(Login.this, MapsActivity.class);
            startActivity(i);
            finish();
        }

    }

    private void signIn() {

        if (!isInternetOn()){
            enableLoginButton();
            return;
        }
        email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            loginButton.setBackground(getResources().getDrawable(R.drawable.sign_up_button_event));
            enableLoginButton();
            return;
        }

        //showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
//                            loginButton.setImageResource(R.drawable.login_pressed_white);
                            //FirebaseUser user = mAuth.getCurrentUser();
                            Intent i = new Intent(Login.this, MapsActivity.class);
                            SharedPreferences prefs = getSharedPreferences("userId", MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = prefs.edit();
                            prefsEditor.putString("email", email);
                            prefsEditor.putString("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            prefsEditor.apply();
                            //i.putExtra("email", email);
//                            SharedPreferences prefs = Login.this.getSharedPreferences("contact", MODE_WORLD_READABLE);
//                            SharedPreferences.Editor prefsEditor = prefs.edit();
//                            prefsEditor.putString("email", email);
//                            prefsEditor.apply();
                            progressDialog.setTitle("GoGo");
                            progressDialog.setMessage("Logging In...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();
                            progressDialog.setCancelable(true);
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(5000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (progressDialog != null)
                                        progressDialog.dismiss();
                                }
                            }).start();
                            startActivity(i);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            loginButton.setBackground(getResources().getDrawable(R.drawable.sign_up_button_event));
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loginButton.setEnabled(true);
                        }

                        // [START_EXCLUDE]
                        /*if (!task.isSuccessful()) {
                           mStatusTextView.setText(R.string.auth_failed);
                        }*/
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailEditText.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Required.");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        String password = passwordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Required.");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }

    public void disableLoginButton(){
//        loginButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
    }

    public void enableLoginButton(){
//        loginButton.setBackground(getResources().getDrawable(R.drawable.sign_up_button_event));
        progressBar.setVisibility(View.INVISIBLE);
        loginButton.setEnabled(true);
    }
    @Override
        public void onDestroy() {
                super.onDestroy();
                if (progressDialog != null) {
                        progressDialog.dismiss();
                        progressDialog = null;
                }
        }

}
