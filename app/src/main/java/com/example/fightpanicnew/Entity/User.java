package com.example.fightpanicnew.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {

    @NonNull
    @PrimaryKey
    private String id;

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String genderHidden;
    private String age;
    private String occupation;
    private String location;
    private String shortDescription;
    private String profilePicturePathFirebase;
    private String profilePicturePathLocalStorage;
    private String profilePictureName;

    public User() {
    }

    public User(@NonNull String id, String username, String email, String firstName, String lastName, String gender, String age,
                String occupation, String location, String shortDescription, String genderHidden,
                String profilePicturePathFirebase, String profilePictureName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.age = age;
        this.occupation = occupation;
        this.location = location;
        this.shortDescription = shortDescription;
        this.genderHidden = genderHidden;
        this.profilePicturePathFirebase = profilePicturePathFirebase;
        this.profilePictureName = profilePictureName;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setGenderHidden(String genderHidden) {
        this.genderHidden = genderHidden;
    }

    public void setProfilePictureName(String profilePictureName) {
        this.profilePictureName = profilePictureName;
    }

    public void setProfilePicturePathFirebase(String profilePicturePathFirebase) {
        this.profilePicturePathFirebase = profilePicturePathFirebase;
    }

    public void setProfilePicturePathLocalStorage(String profilePicturePathLocalStorage) {
        this.profilePicturePathLocalStorage = profilePicturePathLocalStorage;
    }

    public String getProfilePicturePathLocalStorage() {
        return profilePicturePathLocalStorage;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getProfilePicturePathFirebase() {
        return profilePicturePathFirebase;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getLocation() {
        return location;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getGenderHidden() {
        return genderHidden;
    }

    public String getProfilePictureName() {
        return profilePictureName;
    }
}
