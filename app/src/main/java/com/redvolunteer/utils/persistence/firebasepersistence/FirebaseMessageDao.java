package com.redvolunteer.utils.persistence.firebasepersistence;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.dataModels.MessageModel;
import com.redvolunteer.pojo.Chat;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.persistence.RemoteMessageDao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

public class FirebaseMessageDao implements RemoteMessageDao {

    private static final String TAG = "FirebaseMessageDao";

    private DatabaseReference mChatStore;

    private static final String MESSAGE_FIELD = "message";
    private static final String SENDER_FIELD = "sender";
    private static final String RECEIVER_FIELD = "receiver";

    public FirebaseMessageDao(FirebaseDatabase FireDatabase, String requestStoreName) {
        this.mChatStore = FireDatabase.getReference(requestStoreName);
    }

    @Override
    public Flowable<List<Chat>> loadChats(int NumResult, int anchorID) {
        return null;
    }

    @Override
    public Flowable<List<Chat>> LoadUserMessages(String userID) {
        return Flowable.create(new FlowableOnSubscribe<List<Chat>>() {
            @Override
            public void subscribe(@io.reactivex.annotations.NonNull FlowableEmitter<List<Chat>> FlowEmitter) throws Exception {


                mChatStore
                        .orderByChild("sender")
                        .equalTo(userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if(snapshot.exists()){

                                    List<Chat> retrievedChat = new ArrayList<>();
                                    for(DataSnapshot ds: snapshot.getChildren()){
                                        Chat wrapper = ds.getValue(Chat.class);
                                        retrievedChat.add(wrapper);
                                        //Log.d(TAG, "onDataChange: " + wrapper);
                                    }
                                    FlowEmitter.onNext(retrievedChat);
                                } else {

                                    mChatStore
                                            .orderByChild("receiver")
                                            .equalTo(userID)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    List<Chat> retrievedChat = new ArrayList<>();
                                                    for(DataSnapshot ds: snapshot.getChildren()){
                                                        Chat wrapper = ds.getValue(Chat.class);
                                                        retrievedChat.add(wrapper);
                                                        //Log.d(TAG, "onDataChange: " + wrapper);
                                                    }
                                                    FlowEmitter.onNext(retrievedChat);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //Required, but not needed;
                            }
                        });


            }
        }, BackpressureStrategy.BUFFER);

    }

    @Override
    public Chat saveChat(Chat chatIntoStore) {

        String chatID;

        chatID = chatIntoStore.getId();
        if(chatID == null){

            chatID = this.mChatStore.push().getKey();
            chatIntoStore.setId(chatID);
            this.mChatStore.child(chatID).setValue(chatIntoStore);
        }

        return chatIntoStore;
    }

}
