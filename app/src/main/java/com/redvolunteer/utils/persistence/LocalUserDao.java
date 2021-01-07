package com.redvolunteer.utils.persistence;

import com.redvolunteer.pojo.User;

public interface LocalUserDao {

    /**
     * saves a user in the local data store
     */
    void save(User userToStore);

    /**
     * load the currently saved user
     * @return local stored user
     */
    User load();



    /**
     * Deletes all local user infos
     */
    void wipe();
}
