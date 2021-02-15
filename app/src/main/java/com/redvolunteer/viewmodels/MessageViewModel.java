package com.redvolunteer.viewmodels;

import com.redvolunteer.dataModels.MessageModel;
import com.redvolunteer.pojo.Chat;

import java.util.List;

import io.reactivex.Flowable;

public class MessageViewModel {
    private MessageModel mMessageModel;

    public MessageViewModel(MessageModel mMessageModel) {
        this.mMessageModel = mMessageModel;
    }


    public Flowable<List<Chat>> getUserMessages(){
       return mMessageModel.loadUserMessages();
    }
    public Flowable<Chat> getMessagesByUSerID(String CurrentID){
        return mMessageModel.LoadMEssagesByUSerId(CurrentID);
    }
   public void StoreChat(Chat storeMessages){
        mMessageModel.StoreMessage(storeMessages);
    }

}
