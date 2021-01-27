package com.redvolunteer.fragments;

import android.os.Bundle;
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
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class UserMessageFragment extends Fragment {

    private RecyclerView mRecycleView;

    private List<User> mUsers;
    private UserAdapter mUserAdapter;

    private UserViewModel mUserViewModel;

    private FragmentInteractionListener mListener;
    private List<String> mUserChatList;
    private FirebaseUser mCurentUser;
    CircularImageView profPicture;


    DatabaseReference DataRefs;


    public UserMessageFragment(){

        //need empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_message_wall, container, false);
        mCurentUser = FirebaseAuth.getInstance().getCurrentUser();
        mRecycleView = view.findViewById(R.id.recycler_viewer_msg);
        profPicture = view.findViewById(R.id.profile_photo);
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

                    if(chatting.getSender().equals(mCurentUser.getUid())){
                        mUserChatList.add(chatting.getReceiver());
                    }
                    if(chatting.getReceiver().equals(mCurentUser.getUid())){
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
}
