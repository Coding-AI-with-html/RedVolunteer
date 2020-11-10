package com.redvolunteer.pojo;

import java.util.List;

/**
 * User pojo
 */
public class User {


    /**
     * Unique id of the user in the system
     */
    private String id;

    private String fullName;
    private String fullSurname;
    private String email;
    private String biography;

    /**
     * Encoded user pic
     */
    private String photo;

    /**
     * Birth date in the unix second format
     */

    private long BirthDate;

    /**
     * List of the ID's of the volunters who gonna help
     */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullSurname() {
        return fullSurname;
    }

    public void setFullSurname(String fullSurname) {
        this.fullSurname = fullSurname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(long birthDate) {
        BirthDate = birthDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", fullName='" + fullName + '\'' +
                ", fullSurname='" + fullSurname + '\'' +
                ", email='" + email + '\'' +
                ", biography='" + biography + '\'' +
                ", photo='" + photo + '\'' +
                ", BirthDate=" + BirthDate +
                '}';
    }
}
