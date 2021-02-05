package com.redvolunteer.dataModels;

import com.redvolunteer.pojo.Chat;
import com.redvolunteer.utils.persistence.RemoteMessageDao;
import com.redvolunteer.utils.persistence.RemoteRequestDao;
import com.redvolunteer.utils.persistence.RemoteUserDao;

import java.util.Collections;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

public class MessageModellimp implements MessageModel {



    /**
     *
     */
    private RemoteMessageDao remoteMessageDao;

    /**
     * DAO operates on the Requests stored on the remote database
     */
    private RemoteRequestDao remoteRequestDao;
    /**
     * Dao operates on the User on the remote database
     */
    private RemoteUserDao remoteUserDao;
    /**
     * Model to operate the Users
     */
    private UserModel userModel;

    public MessageModellimp(RemoteMessageDao remoteMessageDao, RemoteRequestDao remoteRequestDao, RemoteUserDao remoteUserDao, UserModel userModel) {
        this.remoteMessageDao = remoteMessageDao;
        this.remoteRequestDao = remoteRequestDao;
        this.remoteUserDao = remoteUserDao;
        this.userModel = userModel;
    }


    @Override
    public Flowable<List<Chat>> loadUserMessages() {
        return Flowable.create(new FlowableOnSubscribe<List<Chat>>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<List<Chat>> FlowEmitter) throws Exception {

                remoteMessageDao
                        .LoadUserMessages(userModel.GetLocalUser().getId())
                        .subscribe(new Consumer<List<Chat>>() {
                            @Override
                            public void accept(List<Chat> chats) throws Exception {

                                Collections.reverse(chats);
                                FlowEmitter.onNext(chats);

                            }
                        });
            }
        }, BackpressureStrategy.BUFFER);
    }


    @Override
    public void StoreMessage(Chat chatToStore) {
        remoteMessageDao.saveChat(chatToStore);
    }
}
