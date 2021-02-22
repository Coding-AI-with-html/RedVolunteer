package com.redvolunteer.utils.persistence;

import com.redvolunteer.pojo.User;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;

public interface RemoteUserDao {

    /**
     * Load user from the Store, this operation might can be aSync;
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


    void blockUser(User userID, String BlockUserID);
    /**
     * Store a user in to he persistence store
     */
    void save(User userToStore);

    /**
     *Load User for Message Fragment
     */
    Flowable<List<User>> LoadUserForMessages();

    /**
     * Load's list of users who CurrentUser Blocked
     */
    Flowable<List<User>> LoadBlockedList(String CurrentUserID);
}
