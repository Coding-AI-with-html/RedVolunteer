package com.redvolunteer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.redvolunteer.R;
import com.redvolunteer.pojo.User;
import com.redvolunteer.viewmodels.UserViewModel;

import java.util.List;

public class UserMessageFragment extends Fragment {

    private RecyclerView mRecycleView;

    private List<User> mUsers;

    private UserViewModel mUserViewModel;

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
        return view;
    }
}
