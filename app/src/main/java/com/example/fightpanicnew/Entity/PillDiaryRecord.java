package com.example.fightpanicnew.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pillDiaryRecord_table")
public class PillDiaryRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String dateOfCreation;

    private String description;

    private String title;

    private String dateWhenTaken;

    private String timeWhenTaken;

    public PillDiaryRecord(String dateOfCreation, String description, String title, String dateWhenTaken, String timeWhenTaken) {
        this.dateOfCreation = dateOfCreation;
        this.description = description;
        this.title = title;
        this.dateWhenTaken = dateWhenTaken;
        this.timeWhenTaken = timeWhenTaken;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateWhenTaken() {
        return dateWhenTaken;
    }

    public void setDateWhenTaken(String dateWhenTaken) {
        this.dateWhenTaken = dateWhenTaken;
    }

    public String getTimeWhenTaken() {
        return timeWhenTaken;
    }

    public void setTimeWhenTaken(String timeWhenTaken) {
        this.timeWhenTaken = timeWhenTaken;
    }
}
