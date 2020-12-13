package com.redvolunteer.utils.persistence;

import com.redvolunteer.pojo.User;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;

public interface RemoteUserDao {

    /**
     * Load user from the Sutore, this operation might can be aSync;
     * @param userID
     * @return
     */
    Flowable<User> loadById(String userID);

    /**
     * Load a set of users from the store. operation might can be aSync;
     * @param userIDs
     * @return
     */
    Flowable<Map<String, User>> loadByIds(List<String> userIDs);



    /**
     * Store a user in to he persistence store
     */
    void save(User userToStore);
}
