package com.example.myhabits;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    //this class handles all db operations

    private static final int DB_VERSION = 6; //for adding calendar table

    //habits table
    public static final String HABIT_TABLE="habit_table";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_HABIT_NAME ="name";
    public static final String COLUMN_HABIT_DESCRIPTION = "decription"; //Ahhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
    public static final String COLUMN_IMAGE_URL = "imageURL";

    //calendar (dates) table
    public static final String CALENDAR_TABLE="calendar_table";
    public static final String COLUMN_CAL_ID = "id";
    public static final String COLUMN_CAL_DATE = "date"; //eg. 04.10.2020
    public static final String COLUMN_CAL_DAY ="day"; //SUN

    //habit_date table: habit_id, date_id, isChecked
    public static final String HABIT_DATE_TABLE="habit_date_table";
    public static final String COLUMN_HD_ID = "id";
    public static final String COLUMN_HD_HABIT_ID = "habit_id";
    public static final String COLUMN_HD_DATE_ID = "date_id";
    public static final String COLUMN_HD_DONE ="done"; //isDone for habit

    //habit_displayed_view: h.id, h.name, h.decription, h.imageURL, hd.done
    public static final String HABIT_DISPLAYED_VIEW="habit_displayed_view";




    public DatabaseHelper(Context context){ super(context, "habits.db", null, DB_VERSION);    }
   //public DatabaseHelper(Context context){ super(context, "habits.db", null, 2);    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //version 1
        String createHabitsTableStatement = "CREATE TABLE "+HABIT_TABLE+" ("+COLUMN_ID+" integer primary key autoincrement, "+COLUMN_HABIT_NAME+" text, "+COLUMN_HABIT_DESCRIPTION+" text, "+COLUMN_IMAGE_URL+" text)";
        db.execSQL(createHabitsTableStatement);

        //version 2
        String createCalendarTableStatement = "CREATE TABLE "+CALENDAR_TABLE+" ("+COLUMN_CAL_ID+" integer primary key autoincrement, "+COLUMN_CAL_DATE+" text, "+COLUMN_CAL_DAY+" text)";
        db.execSQL(createCalendarTableStatement);
        generateDatesForOneYear(db);


        //version 3-4
        String createHabitDateTableStatement = "CREATE TABLE "+HABIT_DATE_TABLE+" ("+COLUMN_HD_ID+" integer primary key autoincrement, "+COLUMN_HD_HABIT_ID+" integer, "+COLUMN_HD_DATE_ID+" integer, "+COLUMN_HD_DONE+" integer)";
        db.execSQL(createHabitDateTableStatement);

        // ------ SAMPLE DATA --------
        // ---(for demonstration purposes) ---
        String addSampleHabitStatement1 = "INSERT INTO habit_table (id, name, decription, imageURL) VALUES (1, 'Slovenian', 'Read 1 page!', 'https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1558103617l/45865535.jpg')";
        String addSampleHabitStatement2 = "INSERT INTO habit_table (id, name, decription, imageURL) VALUES (2, 'Violin', 'Play violin for 25 mins', 'https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/Man_Playing_Violin_on_Bench_at_Park.jpg/170px-Man_Playing_Violin_on_Bench_at_Park.jpg')";
        String addSampleHabitStatement3 = "INSERT INTO habit_table (id, name, decription, imageURL) VALUES (3, 'Journaling', 'write 1 page ', 'https://www.theholisticpursuit.com/wp-content/uploads/2018/02/coffee-journaling-calvin-hanson-holistic-pursuit-1-768x576.jpg')";
        String addSampleHabitStatement4 = "INSERT INTO habit_table (id, name, decription, imageURL) VALUES (4, 'Spanish', 'Do 1 Duolingo lesson ', 'https://www.swedishnomad.com/wp-content/images/2019/08/Spanish-Words-2.jpg')";
        String addSampleHabitStatement5 = "INSERT INTO habit_table (id, name, decription, imageURL) VALUES (5, 'Yoga', 'Do 30 mins of yoga', 'https://post.healthline.com/wp-content/uploads/2019/09/Female_Yoga_732x549-thumbnail.jpg')";
        String addSampleHabitStatement6 = "INSERT INTO habit_table (id, name, decription, imageURL) VALUES (6, 'Hungarian', 'Study for 15 mins ', 'https://kiszo.net/wp-content/uploads/2019/11/magyarnyelv.jpg')";

        db.execSQL(addSampleHabitStatement1);
        db.execSQL(addSampleHabitStatement2);
        db.execSQL(addSampleHabitStatement3);
        db.execSQL(addSampleHabitStatement4);
        db.execSQL(addSampleHabitStatement5);
        db.execSQL(addSampleHabitStatement6);
        // end of sample data


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //DO SMTH HERE!

//        db.execSQL("DROP TABLE IF EXISTS "+CALENDAR_TABLE);
//        String createCalendarTableStatement = "CREATE TABLE "+CALENDAR_TABLE+" ("+COLUMN_CAL_ID+" integer primary key autoincrement, "+COLUMN_CAL_DATE+" text, "+COLUMN_CAL_DAY+" text)";
//        db.execSQL(createCalendarTableStatement);
//
//        generateDatesForOneYear(db); //and fill the Calendar table



    }

    //methods to manipulate data in the database:
    public boolean addOneHabit(Habit habit){
        SQLiteDatabase db = this.getWritableDatabase();

        //class that works like an associative array or hashmap:
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_HABIT_NAME, habit.getName());
        cv.put(COLUMN_HABIT_DESCRIPTION, habit.getDescription());
        cv.put(COLUMN_IMAGE_URL, habit.getImageURL());
        //we don't need to pass in the id as it is autoincremented

        long insert = db.insert(HABIT_TABLE, null,cv);
        if(insert==-1){
            return false;
        }else{
            return true;
        }
    }//end of addOneHabit

    public boolean generateDatesForOneYear(SQLiteDatabase db){ //i'll just pass it in
        //generate an array of dates:
        List<Date> dates = new ArrayList<>();
        Calendar calendar = new GregorianCalendar(2020, 8, 1); //startDate
        Calendar endCalendar = new GregorianCalendar(2021, 10, 1); //endDate -> optimize this somehow!

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }

        //get database:
        //SQLiteDatabase db = getWritableDatabase();
       //date formatting
        String pattern = "dd.MM.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern); //you can get *local formatting* here somehow
        SimpleDateFormat weekDayFormat = new SimpleDateFormat("E"); //WED

        for (Date date : dates) {  //for each item, put colname-value pairs into cv -> insert into sqlite database (at once)
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_CAL_DATE, simpleDateFormat.format(date)); //should work, theoretically...
            cv.put(COLUMN_CAL_DAY, weekDayFormat.format(date)); //Wed
            long insert = db.insert(CALENDAR_TABLE, null,cv);
        }

        return true;
    } //end of generateDatesForOneYear

    public MyDate getMyDateByDate(Date sourceDate){ //for the moment, let it return *current date* (Date date)
       // Date currentDate = Calendar.getInstance().getTime();
        MyDate myDate = new MyDate();

        String pattern = "dd.MM.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern); //you can get *local formatting* here somehow
        String sourceDateStr = simpleDateFormat.format(sourceDate);
        String sql = "SELECT "+COLUMN_CAL_ID+", "+COLUMN_CAL_DATE+", "+COLUMN_CAL_DAY+" FROM "+CALENDAR_TABLE+" WHERE "+COLUMN_CAL_DATE+" = '"+sourceDateStr+"'";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
                int dateId = c.getInt(0);
                String dateStr = c.getString(1);
                String day = c.getString(2);

                myDate.setId(dateId); //optimize this bit!
                myDate.setDateStr(dateStr);
                myDate.setDay(day);

        }else{
            //failure, do not add anything to the list
        }
        //clean up after it all:
        c.close();
        db.close();
        return myDate;

    }

    // helper function:
    public static List<Date> getDatesBetweenUsingJava7(Date startDate, Date endDate) {
        List<Date> datesInRange = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);

        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            datesInRange.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        return datesInRange;
    }

    public List<Habit> getAllHabits(){
        List<Habit> returnList = new ArrayList<>();
        String sql = "SELECT * FROM "+HABIT_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
            do{ //careful here: col indexes are used so don't change the order!
                int habitId = c.getInt(0);
                String habitName = c.getString(1);
                String habitDescription = c.getString(2);
                String imageURL = c.getString(3);

                Habit habit = new Habit(habitId, habitName, habitDescription, imageURL);
                returnList.add(habit);

            }while(c.moveToNext());
        }else{
            //failure, do not add anything to the list
        }
        //clean up after it all:
        c.close();
        db.close();
        return returnList;

    }

    public boolean deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM "+HABIT_TABLE; //deletes everything
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
            return true;
        }else{
            return false;
        }

    }

    public boolean deleteOneHabit(Habit habit){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM "+HABIT_TABLE+" WHERE "+COLUMN_ID+"= "+habit.getId();
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }
    public boolean updateOneHabit(Habit habit){
        SQLiteDatabase db = this.getWritableDatabase(); // ! put strings in quotation marks !
        String sql = "UPDATE "+HABIT_TABLE+" SET "+COLUMN_HABIT_NAME+"= '"+habit.getName()+"', "+COLUMN_HABIT_DESCRIPTION+"= '"+habit.getDescription()+"', "+COLUMN_IMAGE_URL+"= '"+habit.getImageURL()+"' WHERE "+COLUMN_ID+" = "+habit.getId();
        db.execSQL(sql);
        return true;
    }//end of updateOne

    //for Statistics (calendar):
    public List<Date> getDatesByHabit(Habit habit){
        List<Date> returnList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT c.date FROM "+HABIT_DATE_TABLE+" hd INNER JOIN "+CALENDAR_TABLE+" c ON hd.date_id = c.id  WHERE hd.habit_id = "+habit.getId();
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
            do{ //careful here: col indexes are used so don't change the order!
                String dateStr = c.getString(0); //eg. 27.10.2020
                DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

                try {
                    Date date = (Date) formatter.parse(dateStr);
                    returnList.add(date);
                }catch (Exception ex){
                    //exception
                }


            }while(c.moveToNext());
        }else{
            //failure, do not add anything to the list
        }

        c.close();
        db.close();
        return returnList;
    } //end of getDatesbyHabit

    public MyDate getDateById(int id) { //test if this method works!
        MyDate date;
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT "+COLUMN_CAL_ID+", "+COLUMN_CAL_DATE+", "+COLUMN_CAL_DAY+" FROM "+CALENDAR_TABLE+" WHERE "+COLUMN_CAL_ID+"="+id;
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
            int dateId = c.getInt(0);
            String dateStr = c.getString(1);
            String day = c.getString(2);

            date = new MyDate(dateId, dateStr, day);

        }else{
           date = new MyDate();
            //failure, do not add anything to the list
        }
        //clean up after it all:
        c.close();
        db.close();
        return date;
    }

    //functions for working with HabitDate table:
    //insert habit-date pair
    //delete habit-date pair (by id?) or just by habit id AND date id
    // maybe update habit status...
    public boolean addHabitDate(HabitDisplayed habit, MyDate date){ //boolean done
        int doneInt = habit.isChecked() ? 1 : 0;  //alternatively could be gotten from habitDisplayed.isChecked() Dunno..
        SQLiteDatabase db = this.getWritableDatabase();

        //class that works like an associative array or hashmap:
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_HD_HABIT_ID, habit.getId());
        cv.put(COLUMN_HD_DATE_ID, date.getId());
        cv.put(COLUMN_HD_DONE, doneInt);
        //we don't need to pass in the id as it is autoincremented


        long insert = db.insert(HABIT_DATE_TABLE, null,cv);
        if(insert==-1){
            return false;
        }else{
            return true;
        }

    } //end of addHabitDate

    public boolean deleteHabitDate(HabitDisplayed habit, MyDate date){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "DELETE FROM "+HABIT_DATE_TABLE+" WHERE "+COLUMN_HD_HABIT_ID+"= "+habit.getId()+" AND "+COLUMN_HD_DATE_ID+"="+date.getId();
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }
    //MAYBE

    //create a view, from which we would get a list of habits with done status "by date"
    //VIEW: habit_id, habit_name,habit_descr, done    (where date_id = ... )

    public void createHabitsDisplayedView(){

    }

    public List<HabitDisplayed> getHabitsDisplayedByDate(MyDate date){
        List<HabitDisplayed> returnList = new ArrayList<>();

        // 1. fill the list with all the habits we have:
        String sql = "SELECT * FROM "+HABIT_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);
        if(c.moveToFirst()){
            do{ //careful here: col indexes are used so don't change the order!
                int habitId = c.getInt(0);
                String habitName = c.getString(1);
                String habitDescription = c.getString(2);
                String imageURL = c.getString(3);

                HabitDisplayed habit = new HabitDisplayed(habitId, habitName, habitDescription, imageURL); //default 'done' value set to false in ctor
                returnList.add(habit);

            }while(c.moveToNext());
        }else{
            //failure, do not add anything to the list
        }

        // 2. set 'done' to true for habits that are present in the habit_date table FOR THIS DATE:
        String sql2 = "SELECT "+COLUMN_HD_HABIT_ID+", "+COLUMN_HD_DONE+" FROM "+HABIT_DATE_TABLE+" WHERE "+COLUMN_HD_DATE_ID+"="+date.getId();
        Cursor c2 = db.rawQuery(sql2, null);
        if(c2.moveToFirst()){
            do{
                int habitId = c2.getInt(0);
                int isDone = c2.getInt(1); //not necessary, maybe simplify this later

                //find habit in the list and change its 'done' status:
                for(HabitDisplayed h:returnList){
                    if(h.getId()==habitId){
                        h.setChecked(true); //true if habit exists in habit-date list
                    }
                }

            }while(c2.moveToNext());
        }else{
            //failure
        }

        //clean up after it all:
        c.close();
        db.close();
        return returnList; //list of habits with their done values for a particular date

    }



    //// ---------------
    ///OKAY! New idea:
    //i create a list of <HabitDisplayed> filled from Habits table (normal) and set all "done" to default value 0
    //then i go through habitDate table for the day [have to pass current date into the function] AND if there are any entries [where done = 1 and date=current date] -> set "done" value of those habits to 1 (by the habit's id)
    //then i probably don't need a view and don't have to store any extra NULL-valued entries for habits for each day!

    //*use ctor without the last parameter to set checked to 0 by default
    /// -----------
}
