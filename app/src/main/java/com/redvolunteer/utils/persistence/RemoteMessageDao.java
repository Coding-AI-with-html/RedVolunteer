package com.redvolunteer.utils.persistence;

import com.redvolunteer.pojo.Chat;

import java.util.List;

import io.reactivex.Flowable;

public interface RemoteMessageDao {
    
    Flowable<List<Chat>> LoadUserMessages(String userID);

    void saveChat(Chat chatIntoStore);
}
