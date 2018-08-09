package com.example.mayur.uploadimage;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail,edtPassword;
    private Button btnLogin;
    private TextView forgotpwd,signUp;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get Firebase instance

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser()!= null){

            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }


        edtEmail =(EditText)findViewById(R.id.edtemail);
        edtPassword=(EditText)findViewById(R.id.edtpassword);
        btnLogin=(Button)findViewById(R.id.register);
        forgotpwd=(TextView)findViewById(R.id.forgotpwd);
        signUp=(TextView)findViewById(R.id.getreg);

        progressDialog = new ProgressDialog(this);

        //Get Firebase Auth instance again

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        forgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity( new Intent(LoginActivity.this,ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = edtEmail.getText().toString().trim();
                final String password = edtPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){

                    Toast.makeText(getApplicationContext(),"Enter Email ID First",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){

                    Toast.makeText(getApplicationContext(),"Enter the Password",Toast.LENGTH_LONG).show();
                    return;
                }

                progressDialog.setMessage("Registering User please wait ");
                progressDialog.show();


                //Authenticate user now using Firebase Auth process

                auth.createUserWithEmailAndPassword(email,password)

                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.


                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        edtPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            progressDialog.dismiss();

                            }
                        });
            }
        });
    }
}
