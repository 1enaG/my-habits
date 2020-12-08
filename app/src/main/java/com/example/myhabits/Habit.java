package com.example.myhabits;

import java.util.Comparator;

public class Habit {
    private int id;
    private String name;
    private String description;
    private String imageURL;

    public Habit(){}
    public Habit(int id, String name, String description, String imageURL){
        this.id=id;
        this.name=name;
        this.description = description;
        this.imageURL = imageURL;
    }


    @Override
    public String toString() {
        return id +
                " " + name +
                " " + description +
                " " + imageURL;
    }

    //getters and setters:
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }



    //for comparing:
    public static Comparator<Habit> HabitNameAZComparator = new Comparator<Habit>() {
        @Override
        public int compare(Habit h1, Habit h2) {
            return h1.getName().compareTo(h2.getName());
        }
    };


    public static Comparator<Habit> HabitNameZAComparator = new Comparator<Habit>() {
        @Override
        public int compare(Habit h1, Habit h2) {
            return h2.getName().compareTo(h1.getName());
        }
    };

    public static Comparator<Habit> HabitIdAscendingComparator = new Comparator<Habit>() {
        @Override
        public int compare(Habit h1, Habit h2) {
            return h1.getId()-h2.getId();
        }
    };

    public static Comparator<Habit> HabitIdDescendingComparator = new Comparator<Habit>() {
        @Override
        public int compare(Habit h1, Habit h2) {
            return h2.getId()-h1.getId();
        }
    };

}
