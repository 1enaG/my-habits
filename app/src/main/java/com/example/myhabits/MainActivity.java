package com.example.myhabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.Gesture;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    public static final int SWIPE_THRESHOLD = 100;
    public static final int SWIPE_VELOCITY_THRESHOLD = 50;
    DatabaseHelper databaseHelper;

    //FOR LANGUAGES:
    // these two variables will be used by SharedPreferences
    private static final String FILE_NAME = "file_lang"; // preference file name
    private static final String KEY_LANG = "key_lang"; // preference key

    //for bottom navigation bar:
    private BottomNavigationView bottomNavigationView;

    //for displaying items in RecyclerView:
    private RecyclerView recyclerView;
    private RecyclerView.Adapter myAdapter;
    private RecyclerView.LayoutManager layoutManager;
    //private List<Habit> habitsList; //to be replaced. As is, can be used for statistics Activities
    private List<HabitDisplayed> habitsList;
    private Menu menu;
    private TextView tv_dateString;
    private TextView tv_day;

    private List<HabitDisplayed> displayedHabitsList; // habits, currently displayed with their isChecked values
    //we can pass it into <Habit> funcs so sorting and such would still work

    //for swipes:
    private GestureDetector gestureDetector;

    //for displaying a date for the list:
    private static MyDate displayedDate; //date that is currently being displayed in the textViews

    //for adding rows to HabitDate db from RecyclerViewAdapter
    public static MyDate getDisplayedDate(){
        return displayedDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.darktheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        //  load language after super and before setContentView
        loadLanguage();
        setContentView(R.layout.activity_main);

        //to work with db:
        databaseHelper = new DatabaseHelper(MainActivity.this);

        Date currentDate = Calendar.getInstance().getTime();
        displayedDate = databaseHelper.getMyDateByDate(currentDate);
        displayDate(displayedDate);

        //habitsList = databaseHelper.getAllHabits();
        habitsList = databaseHelper.getHabitsDisplayedByDate(displayedDate);

        //recyclerView:
        recyclerView = findViewById(R.id.rv_habitList);

        layoutManager = new LinearLayoutManager(this); //use linear layout
        recyclerView.setLayoutManager(layoutManager);

        myAdapter = new RecyclerViewAdapter(habitsList, MainActivity.this);  //SHOULD refresh this after date is changed
        recyclerView.setAdapter(myAdapter);

        //for swipes:
        gestureDetector = new GestureDetector(MainActivity.this, this);

        //for bottom navigation:
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.bottom_menu_prev: //go to prev date
                        goToPrevDate();
                        break;
                    case R.id.bottom_menu_home: //go to today
                        goToToday();
                        break;
                    case R.id.bottom_menu_next: //go to next date
                        goToNextDate(); //go to next date
                        break;
                }

                return true;
            }
        });

    }

    public void generateDates(){
        //generate an array of dates:

        List<Date> dates = new ArrayList<>();
        Calendar calendar = new GregorianCalendar(2020, 10, 17); //startDate
        Calendar endCalendar = new GregorianCalendar(2020, 10, 30); //endDate -> optimize this somehow!

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }

        //date formatting
        String pattern = "dd.MM.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern); //you can get *local formatting* here somehow
        SimpleDateFormat weekDayFormat = new SimpleDateFormat("E"); //WED

        for(Date date : dates){
            Toast.makeText(MainActivity.this, simpleDateFormat.format(date), Toast.LENGTH_SHORT).show();
        }


    } //end of generateDatesForOneYear

    //Two funcs to work with menu:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            //Sort by...
            case R.id.menu_sort_aToZ:
                Collections.sort(habitsList, Habit.HabitNameAZComparator);
                Toast.makeText(MainActivity.this, "sort a to z", Toast.LENGTH_SHORT).show();
                myAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_sort_zToA:
                Collections.sort(habitsList, Habit.HabitNameZAComparator);
                Toast.makeText(MainActivity.this, "sort z to a", Toast.LENGTH_SHORT).show();
                myAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_sort_idAscending:
                Collections.sort(habitsList, Habit.HabitIdAscendingComparator);
                Toast.makeText(MainActivity.this, "sort id ascending", Toast.LENGTH_SHORT).show();
               // generateDates();
                myAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_sort_idDescending:
                Collections.sort(habitsList, Habit.HabitIdDescendingComparator);
                Toast.makeText(MainActivity.this, "sort id descending", Toast.LENGTH_SHORT).show();
                myAdapter.notifyDataSetChanged();
                return true;
                //Add
            case R.id.menu_addHabit:
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                startActivity(intent);
                return true;

            //Language:
            case R.id.menu_lang_en:
                saveLanguage("en");
                return true;

            case R.id.menu_lang_hu:
                saveLanguage("hu");
                return true;

            case R.id.menu_lang_pl:
                saveLanguage("pl");
                return true;

            case R.id.menu_lang_sp:
                saveLanguage("es");
                return true;

            case R.id.menu_settings:
                Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intentSettings);
                return true;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut(); //logout and send user to login activity
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();

            default: return true;

        }


    } //end of onOptionsItemSelected

    //Funcs for working with context menu:




    //Gesture methods:
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }


    @Override
    public boolean onFling(MotionEvent downEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
        boolean result = false; //whether or not we have consumed the event

        float diffX = moveEvent.getX() - downEvent.getX();
        float diffY = moveEvent.getY() - downEvent.getY();

        if(Math.abs(diffX)>Math.abs(diffY)){ //right or left swipe
            if(Math.abs(diffX)> SWIPE_THRESHOLD && Math.abs(velocityX)> SWIPE_VELOCITY_THRESHOLD){
                if(diffX>0){ // swipe right (go to left page)
                    goToPrevDate();
                    result=true;

                }else{ //swipe left (go to right page)
                    goToNextDate();
                    result=true;
                }
            }
        }else{ // up or down swipe
            if(Math.abs(diffY)>SWIPE_THRESHOLD && Math.abs(velocityY)>SWIPE_VELOCITY_THRESHOLD){
                Toast.makeText(MainActivity.this, "up or down swipe", Toast.LENGTH_LONG).show();
            result=true;
            }
            
        }
        return result; // true = event was consumed, false = event not consumed
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { //from Activity superclass
        gestureDetector.onTouchEvent(event); //pass the event from activity to gesture detector -> to onFling()
        return super.onTouchEvent(event);
    }

    //Handling gestures:
    private void goToPrevDate() { //to to previous date
        //Toast.makeText(MainActivity.this, "right swipe", Toast.LENGTH_LONG).show();
        // - get previous date from using displayed date (through id? prev id?)
        // - change displayedDate to new value
        // - display the displDate in textView (maybe eventually you can call onCreate here so it redraws both the date and the habits list according to date)
        int prevDateId = displayedDate.getId()-1;
        MyDate prevDate = databaseHelper.getDateById(prevDateId);
        displayedDate = prevDate; //changing the class var!
        displayDate(displayedDate);
        //Reload recyclerView (fill it with habits according to new date) - separate func, but call it here!
        reloadHabitsList(displayedDate);
    }
    private void goToNextDate() { //go to next date
        //Toast.makeText(MainActivity.this, "left swipe", Toast.LENGTH_LONG).show();
        int nextDateId = displayedDate.getId()+1;
        MyDate nextDate = databaseHelper.getDateById(nextDateId);
        displayedDate = nextDate;
        displayDate(displayedDate);
        reloadHabitsList(displayedDate);
    }
    private void goToToday(){
        Toast.makeText(MainActivity.this, "today", Toast.LENGTH_SHORT).show();
        Date currentDate = Calendar.getInstance().getTime();
        displayedDate = databaseHelper.getMyDateByDate(currentDate);
        displayDate(displayedDate);
        reloadHabitsList(displayedDate);
    }


    private void reloadHabitsList(MyDate date){
        habitsList = databaseHelper.getHabitsDisplayedByDate(date); //we have changed the date currently displayed and now have to reload the view
        myAdapter = new RecyclerViewAdapter(habitsList, MainActivity.this);  //SHOULD refresh this after date is changed
        recyclerView.setAdapter(myAdapter);

        //maybe replace with myAdapter.notifyDataSetChanged(); if I can make it work
    }

    //helper function to display a date in TextViews:
    public void displayDate(MyDate displayedDate){
        tv_dateString = findViewById(R.id.tv_dateString);
        tv_day = findViewById(R.id.tv_day);
        tv_dateString.setText(displayedDate.getDateStr());
        tv_day.setText(displayedDate.getDay().toUpperCase());
    }

    //we also need a helper function to draw the list according to current date (so takes in displayedDate, draws list):
    //in order to do this we have to implement the whole Habit-Date db thing
    // so when we tick a checkbox, we can add the habit to our done (Habit-Date) db table -> if it is in the table -> checkbox checked; if it isn't -> unchecked
    // (and remove the entry when it gets unchecked)
    public void displayHabitsList(MyDate displayedDate){

    }

    //Languages stuff:
    public void saveLanguage(String lang) {
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LANG, lang);
        editor.apply();
        recreate(); //refreshing activity
    }

    private void loadLanguage() {
// this method should be called before setContentView() method of the onCreate method
        Locale locale = new Locale(getLangCode());
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private String getLangCode() {
        SharedPreferences preferences = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String langCode = preferences.getString(KEY_LANG, "en");
        return langCode;
    }
}
