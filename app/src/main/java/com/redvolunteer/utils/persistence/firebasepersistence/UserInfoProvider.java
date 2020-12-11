package com.redvolunteer.utils.persistence.firebasepersistence;


import com.redvolunteer.pojo.User;

public class UserInfoProvider {



    User user;

    public UserInfoProvider(User user) {
        this.user = user;
    }


    public UserInfoProvider() {
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserInfoProvider{" +
                "user=" + user +
                '}';
    }
}

