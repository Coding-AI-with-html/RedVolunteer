package com.redvolunteer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.redvolunteer.FragmentInteractionListener;
import com.redvolunteer.R;
import com.redvolunteer.RedVolunteerApplication;
import com.redvolunteer.adapters.UserAdapter;
import com.redvolunteer.pojo.Chat;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.MessageViewModel;
import com.redvolunteer.viewmodels.UserViewModel;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.FlowableSubscriber;

public class UserMessageFragment extends Fragment {


    private static final String TAG = "UserMessageFragment";
    private RecyclerView mRecycleView;
    private Subscription MessageRetrievedSubscription;

    private List<User> mUsers;
    private UserAdapter mUserAdapter;

    private UserViewModel mUserViewModel;
    private HelpRequestViewModel mHelpRequestViewModel;
    private MessageViewModel mMainModel;

    private FragmentInteractionListener mListener;
    private List<String> mUserChatList;
    //private FirebaseUser mCurentUser;
    User mShowedUSer;


    DatabaseReference DataRefs;


    public UserMessageFragment(){

        //need empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserViewModel = mListener.getUserViewModel();
        mShowedUSer = mUserViewModel.retrieveCachedUser();
        mHelpRequestViewModel = mListener.getHelpRequestViewModel();
        mMainModel = mListener.getMessageViewModel();

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_message_wall, container, false);
        mRecycleView = view.findViewById(R.id.recycler_viewer_msg);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));



        mUserChatList = new ArrayList<>();

        DataRefs = FirebaseDatabase.getInstance().getReference("Chats");
        DataRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Dsnapshot) {

                mUserChatList.clear();
                for(DataSnapshot snapshot: Dsnapshot.getChildren()){
                    Chat chatting =  snapshot.getValue(Chat.class);

                    if(chatting.getSender().equals(mShowedUSer.getId())){
                        mUserChatList.add(chatting.getReceiver());
                    }
                    if(chatting.getReceiver().equals(mShowedUSer.getId())){
                        mUserChatList.add(chatting.getSender());
                    }
                }
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {




            if(NetworkCheker.getInstance().isNetworkAvailable(getContext())){

                mMainModel.getUserMessages().subscribe(new Subscriber<List<Chat>>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.request(1L);

                        if(MessageRetrievedSubscription !=null){
                            MessageRetrievedSubscription.cancel();
                        }
                        MessageRetrievedSubscription = subscription;
                    }

                    @Override
                    public void onNext(List<Chat> chats) {

                        if(chats.size()!=0){
                            InitiliazeMessageView(chats);
                        }
                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


            }
        }
    }
    private void InitiliazeMessageView(final List<Chat> chatsList){
        UserAdapter mUserAdapter = new UserAdapter(getContext(), mUsers);
    }

    private void readChats(){

        mUsers = new ArrayList<>();

        DataRefs = FirebaseDatabase.getInstance().getReference("Help_Seekers");

        DataRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot Dsnapshot) {
                mUsers.clear();

                for(DataSnapshot ds: Dsnapshot.getChildren()){
                    User user = ds.getValue(User.class);

                    //display user from chat's
                    for(String id: mUserChatList){
                        if(user.getId().equals(id)){
                            if(mUsers.size() != 0){
                                for(User user1: mUsers){
                                    if(!user.getId().equals(user1.getId())){
                                        mUsers.add(user);
                                    }
                                }
                            } else {
                                mUsers.add(user);
                            }
                        }
                    }
                }
                mUserAdapter = new UserAdapter(getContext(), mUsers);
                mRecycleView.setAdapter(mUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof FragmentInteractionListener){
            mListener = (FragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentsInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener= null;
    }
}
