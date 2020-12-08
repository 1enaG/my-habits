package com.example.myhabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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


    EditText et_email, et_password;
    Button btn_login;

    ProgressBar progressBar;
    TextView tv_go_to_register;

    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_email = findViewById(R.id.et_login_email);
        et_password = findViewById(R.id.et_login_password);
        btn_login = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        tv_go_to_register = findViewById(R.id.tv_go_to_register);

        fAuth = FirebaseAuth.getInstance();

        //validate input -> check email and password against data in db

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString().trim(); //originally an obj, not a string!
                String password = et_password.getText().toString().trim();

                if(TextUtils.isEmpty(email)){ //check if email has value
                    et_email.setError("Email is required!");
                    return; // no sense to proceed any further
                }
                if(TextUtils.isEmpty(password)){ //
                    et_password.setError("Password is required!");
                    return;
                }
                if(password.length() < 6) { //password has to be at least 6 characters long
                    et_password.setError("Password has to be at least 6 charaters long"); //do this with string resources for diff languages!
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate the user:
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //redirect user to main activity or display error:
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(LoginActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            //so the spinner stops spinning:
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            } //end of onClick
        });

        tv_go_to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });


    }
}
