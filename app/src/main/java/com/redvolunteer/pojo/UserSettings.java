package com.redvolunteer.pojo;

public class UserSettings {

    private User user;

    public UserSettings(User user){
        this.user = user;

    }

    public UserSettings() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


}
