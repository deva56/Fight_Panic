package com.example.fightpanicnew.Models;

public class User {

    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    private String genderHidden;
    private String age;
    private String occupation;
    private String location;
    private String short_description;
    private String newPassword;
    private String token;
    private String profile_picture_path;
    private String profile_picture_name;
    private byte[] profileImage;
    private String _id;


    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setGenderHidden(String genderHidden) {
        this.genderHidden = genderHidden;
    }

    public void setProfile_picture_name(String profile_picture_name) {
        this.profile_picture_name = profile_picture_name;
    }

    public void setProfilePicturePath(String profilePicturePath) {
        this.profile_picture_path = profilePicturePath;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public void setProfileImage(byte[] profileImage) {
        this.profileImage = profileImage;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
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
        this.short_description = shortDescription;
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
        return short_description;
    }

    public byte[] getProfileImage() {
        return profileImage;
    }

    public String getId() {
        return _id;
    }

    public String getProfilePicturePath() {
        return profile_picture_path;
    }

    public String getGenderHidden() {
        return genderHidden;
    }

    public String getPassword() {
        return password;
    }

    public String getProfile_picture_name() {
        return profile_picture_name;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getToken() {
        return token;
    }

    public String get_id() {
        return _id;
    }
}
