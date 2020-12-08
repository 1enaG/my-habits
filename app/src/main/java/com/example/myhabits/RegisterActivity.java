package com.example.myhabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText et_full_name, et_email, et_password, et_confirm_password;
    Button btn_register;
    ProgressBar progressBar;
    TextView tv_go_to_login;
    LinearLayout layout;

    FirebaseAuth fAuth; //for registering the user!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //for gradient animation:
        layout = findViewById(R.id.layout_register);
        AnimationDrawable animationDrawable = (AnimationDrawable) layout.getBackground();
        animationDrawable.setEnterFadeDuration(1500);
        animationDrawable.setExitFadeDuration(1500);
        animationDrawable.start();

        // for actual registration:
        et_full_name = findViewById(R.id.et_full_name);
        et_email = findViewById(R.id.et_reg_email);
        et_password = findViewById(R.id.et_reg_password);
        et_confirm_password = findViewById(R.id.et_reg_confirm_password);

        btn_register = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        tv_go_to_login = findViewById(R.id.tv_go_to_login);


        //for authentication:
        fAuth = FirebaseAuth.getInstance(); //to perform various operations for authentication

        //check if user is already logged in:
        if(fAuth.getCurrentUser() != null){ //then send them to main Activity at once
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }

        btn_register.setOnClickListener(new View.OnClickListener() {
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

                String confirmPassword = et_confirm_password.getText().toString().trim();
                if(! password.equals(confirmPassword)){
                    et_confirm_password.setError("Passwords do not match!");
                    return;
                }

                //if all is valid, proceed to registering user:
                progressBar.setVisibility(View.VISIBLE); //let user know reg process has started!

                //register user with Firebase:
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() { //check if registration is successful or not
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //redirect user to main activity or display error:
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User created!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            Toast.makeText(RegisterActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });


            }
        });

        tv_go_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });


    }
}
