package com.example.myhabits;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    CompactCalendarView compactCalendar;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()); // MMMM - full name of the month
    private Habit habit;
    private TextView tv_total_days_practiced, tv_habit_name;


    TextView tv_month;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //theme
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.darktheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }//end for themes


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        DatabaseHelper databaseHelper = new DatabaseHelper(StatisticsActivity.this);

        //get Habit from prev Activity:
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String habitName = intent.getStringExtra("name");
        habit = new Habit();
        habit.setId(id);
        habit.setName(habitName);

        //display habit name:
        tv_habit_name = findViewById(R.id.tv_stat_habit_name);
        tv_habit_name.setText(habit.getName());

        //get Dates for the Habit:
        List<Date> testHabitDates = databaseHelper.getDatesByHabit(habit);

        //total days praticed:
        int daysPracticed = testHabitDates.size();
        tv_total_days_practiced = findViewById(R.id.tv_totalDaysPracticed);
        tv_total_days_practiced.setText(String.valueOf(daysPracticed));

        //set the 'done' dates on Calendar:
        tv_month = findViewById(R.id.tv_month);
        tv_month.setText(simpleDateFormat.format(System.currentTimeMillis()));
        compactCalendar = findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true); //days of the week

        for(Date date : testHabitDates){
            long timestamp = date.getTime();
            Event event = new Event(Color.WHITE, timestamp, habit.getName()); //EpochConverter timestamp+L (nie wiem, co robić z "L")
            compactCalendar.addEvent(event);
        }

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {//display month name in textView:
                tv_month.setText(simpleDateFormat.format(firstDayOfNewMonth)); //set the "now" circle to first day of the month
            }
        });

    }// end of OnCreate
}

