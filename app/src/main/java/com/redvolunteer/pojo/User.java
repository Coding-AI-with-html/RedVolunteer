package com.redvolunteer.pojo;

import java.time.LocalDate;

/**
 * User pojo
 */
public class User {


    /**
     * Unique id of the user in the system
     */
    private String id;

    private String Name;
    private String Surname;
    private String Email;
    private String biography;
    private long Phone_Number;
    private String Gender;

    /**
     * Encoded user pic
     */
    private String photo;

    /**
     * Birth date in the unix second format
     */

    private long BirthDay;

    /**
     * Constructor for creating new User
     */
    /**
     * List of the ID's of the volunters who gonna help
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public long getPhone_Number() {
        return Phone_Number;
    }

    public void setPhone_Number(long phone_Number) {
        Phone_Number = phone_Number;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getBirthDay() {
        return BirthDay;
    }

    public void setBirthDay(long birthDay) {
        BirthDay = birthDay;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", Name='" + Name + '\'' +
                ", Surname='" + Surname + '\'' +
                ", Email='" + Email + '\'' +
                ", biography='" + biography + '\'' +
                ", Phone_Number=" + Phone_Number +
                ", Gender='" + Gender + '\'' +
                ", photo='" + photo + '\'' +
                ", BirthDay=" + BirthDay +
                '}';
    }
}
