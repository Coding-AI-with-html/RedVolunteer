package com.redvolunteer.utils.persistence.firebasepersistence;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.redvolunteer.dataModels.MessageModel;
import com.redvolunteer.pojo.Chat;
import com.redvolunteer.utils.persistence.RemoteMessageDao;

import java.util.List;

import io.reactivex.Flowable;

public class FirebaseMessageDao implements RemoteMessageDao {

    private DatabaseReference mChatStore;

    public FirebaseMessageDao(FirebaseDatabase FireDatabase, String requestStoreName) {
        this.mChatStore = FireDatabase.getReference(requestStoreName);
    }

    @Override
    public Flowable<List<Chat>> LoadUserMessages(String userID) {
        return null;
    }

    @Override
    public void saveChat(Chat chatIntoStore) {

    }
}
