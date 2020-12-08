package com.example.myhabits;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private Switch myswitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.darktheme); //Setting the theme!!
        }
        else{
            setTheme(R.style.AppTheme);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        myswitch = findViewById(R.id.myswitch);
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            myswitch.setChecked(true);
        }

        //listen for changes and toggle themes
        myswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //sharedPref.saveCurrentTheme("night");
                    //sharedPref.setNightModeState(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); //maybe remove this

                    restartActivity(); //custom method
                }
                else{
                    // sharedPref.setNightModeState(false);
                    // sharedPref.saveCurrentTheme("day");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    restartActivity();
                }
            }
        });

    }

    public void restartActivity() {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class); //sends an Intent to the same activity
        startActivity(intent);
        finish(); //not sure what this does

    }
}
