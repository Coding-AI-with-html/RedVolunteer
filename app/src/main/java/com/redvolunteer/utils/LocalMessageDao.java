package com.redvolunteer.utils;

import com.redvolunteer.pojo.Chat;
import com.redvolunteer.pojo.RequestHelp;

import java.util.List;

public interface LocalMessageDao {

    /**
     * saves chat to the firebase
     */
    Chat save(Chat chatToSave);

    void wipe();

    /**
     * Get Chat
     */
    List<Chat> getChat(int numResults, int startOffset);

}
