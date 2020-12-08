package com.example.myhabits;

public class HabitDisplayed extends Habit { //this class is used for displaying a habit in RecyclerView together with its 'done' attribute

    //additional field for current 'done' state:
    private boolean isChecked;

    public HabitDisplayed(){
        super(); //maybe i needn't write this, dunno
    }
    public HabitDisplayed(int id, String name, String description, String imageURL, boolean isChecked){
        super(id, name, description, imageURL);
        this.isChecked = isChecked;
    }
    public HabitDisplayed(int id, String name, String description, String imageURL){ //default value for done is FALSE
        super(id, name, description, imageURL);
        this.isChecked=false;
    }

   //getters and setters:
    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }
    @Override
    public String toString(){
        return super.toString()+" "+isChecked();
    }

}
