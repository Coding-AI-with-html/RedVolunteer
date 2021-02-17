package com.redvolunteer.dataModels;

import com.redvolunteer.pojo.Chat;

import java.util.List;

import io.reactivex.Flowable;

public interface MessageModel {

    Flowable<List<Chat>> loadUserMessages();

    Flowable<Chat> LoadMEssagesByUSerId();
    void StoreMessage(Chat chatToStore);


}
