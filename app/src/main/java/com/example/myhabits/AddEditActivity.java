package com.example.myhabits;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class AddEditActivity extends AppCompatActivity {
    Button btn_ok;
    Button btn_cancel;
    EditText et_habitName, et_habitDescription, et_imageURL;
    TextView tv_habitId;
    DatabaseHelper databaseHelper;
    int id; //of current habit that is being edited
    List<Habit> habitsList;
    //maybe create a local list of habits if we need it
    //for working with database:


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.darktheme); //Setting the theme!!
        }
        else{
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        databaseHelper = new DatabaseHelper(AddEditActivity.this);
        habitsList = databaseHelper.getAllHabits(); //fill in the local list

        //getting references to layout elements:
        btn_ok=findViewById(R.id.btn_ok);
        btn_cancel=findViewById(R.id.btn_cancel);
        et_habitName=findViewById(R.id.et_habitName);
        et_habitDescription=findViewById(R.id.et_habitDescription);
        et_imageURL=findViewById(R.id.et_imageURL);
        tv_habitId=findViewById(R.id.tv_habitIdNumber);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        Habit habit = new Habit();

        if(id>-1){ //edit existing habit
            //find the habit with our id in the list:
            for (Habit h: habitsList) { //maybe replace this with working with db directly (instead of keeping a local list of habits in the class)
                if(h.getId()==id){
                    habit=h;

                }
            }
            //once we've found a matching habit:
            et_habitName.setText(habit.getName());
            et_habitDescription.setText(habit.getDescription());
            et_imageURL.setText(habit.getImageURL());
            tv_habitId.setText(String.valueOf(id));


        }else{ //create a new habit


        }

        //event listeners:
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something with this
                if(id>-1){ //update
                    Habit updateHabit = new Habit(id, et_habitName.getText().toString(), et_habitDescription.getText().toString(), et_imageURL.getText().toString());
                   databaseHelper.updateOneHabit(updateHabit);

                }else{//create a new habit
                    Habit newHabit = new Habit(-1, et_habitName.getText().toString(), et_habitDescription.getText().toString(), et_imageURL.getText().toString());
                    //insert habit into database!!
                    databaseHelper.addOneHabit(newHabit);
                }

                //habitsList=databaseHelper.getAllHabits(); //refresh the list
                Intent intent = new Intent(AddEditActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddEditActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });




    }
}
