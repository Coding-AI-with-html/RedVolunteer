package com.redvolunteer.utils.persistence;

import com.redvolunteer.pojo.Chat;
import com.redvolunteer.pojo.RequestHelp;

import java.util.List;

import io.reactivex.Flowable;

public interface RemoteMessageDao {

    /**
     * it load's Chat
     * @return
     */
    Flowable<List<Chat>> loadChats(int NumResult, int anchorID);



    Flowable<List<Chat>> LoadUserMessages(String userID);

    Chat saveChat(Chat chatIntoStore);


}
