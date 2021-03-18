package com.example.fightpanicnew.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "panicAttackRecord_table")
public class PanicAttackRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int attackStrength;
    private String dateOfCreation;
    private String description;
    private String title;
    private String attackDate;
    private String attackTime;

    public PanicAttackRecord(int attackStrength, String dateOfCreation, String description, String title, String attackDate, String attackTime) {
        this.attackStrength = attackStrength;
        this.dateOfCreation = dateOfCreation;
        this.description = description;
        this.title = title;
        this.attackDate = attackDate;
        this.attackTime = attackTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getAttackStrength() {
        return attackStrength;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAttackStrength(int attackStrength) {
        this.attackStrength = attackStrength;
    }

    public void setDateOfCreation(String dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttackDate() {
        return attackDate;
    }

    public void setAttackDate(String attackDate) {
        this.attackDate = attackDate;
    }

    public String getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(String attackTime) {
        this.attackTime = attackTime;
    }
}
