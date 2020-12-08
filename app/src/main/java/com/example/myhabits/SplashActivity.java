package com.example.myhabits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    Button btn_get_started;
    Animation frombottom, fromtop, fadein;
    ImageView balloon;
    TextView tv_app_name;

    //FirebaseAuth fAuth; //for checking if the user is logged in
    //Splash is only displayed to new (unregistered) users
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //for authentication:
//        fAuth = FirebaseAuth.getInstance(); //to perform various operations for authentication
//
//        //check if user is already logged in:
//        if(fAuth.getCurrentUser() != null){ //then send them to main Activity at once
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
//            finish();
//
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);




        btn_get_started = (Button) findViewById(R.id.btn_get_started);
        balloon = (ImageView) findViewById(R.id.balloon);
        tv_app_name = (TextView) findViewById(R.id.tv_app_name);

        frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        fromtop = AnimationUtils.loadAnimation(this, R.anim.fromtop);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);

        btn_get_started.setAnimation(frombottom);
        balloon.setAnimation(fromtop);
        tv_app_name.setAnimation(fadein);


        btn_get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }
}
