package com.redvolunteer.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import com.redvolunteer.BlockedUserListActivity;
import com.redvolunteer.FragmentInteractionListener;
import com.redvolunteer.MainActivity;
import com.redvolunteer.R;
import com.redvolunteer.RedVolunteerApplication;
import com.redvolunteer.RequestDescriptionActivity;
import com.redvolunteer.adapters.UserAdapter;
import com.redvolunteer.newrequesthelp.NewRequestHelpActivity;
import com.redvolunteer.pojo.Chat;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.utils.persistence.ExtraLabels;
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
    private Subscription mUserSubscription;

    private List<User> mUsers;
    private UserAdapter mUserAdapter;

    private UserViewModel mUserViewModel;
    private HelpRequestViewModel mHelpRequestViewModel;
    private MessageViewModel mMainModel;

    private FragmentInteractionListener mListener;
    private List<String> mUserChatList;
    User mShowedUSer;
    List<User> mUserList;
    private ProgressDialog popupDialogProgress;


    /**
     * LinearLayout if User Dont have Messages
     */
    private LinearLayout mNoMessageShow;

    /**
     * Image View where on click goes to blocked user list
     */
    ImageView mBlockedUserList;


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

        return  inflater.inflate(R.layout.fragment_message_wall, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void bind(View view){

        mNoMessageShow = (LinearLayout) view.findViewById(R.id.no_messages);

        mRecycleView = view.findViewById(R.id.recycler_viewer_msg);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBlockedUserList = view.findViewById(R.id.go_to_block_user_list);

        mBlockedUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToBlockList = new Intent(getActivity(), BlockedUserListActivity.class);
                startActivity(goToBlockList);
            }
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {



            if(NetworkCheker.getInstance().isNetworkAvailable(getContext())){
                mUserChatList = new ArrayList<>();
                ShowWhaitSpinner();


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
                        StopWhaitSpinner();
                        InitiliazeMessageView(chats);

                    }

                    @Override
                    public void onError(Throwable t) {
                        StopWhaitSpinner();
                        ShowRetrievedErrorPopupDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });





            } else {
                ShowNoInternetConnection();
            }
        }
    }


    private void InitiliazeMessageView(final List<Chat> chatsList){
        if(chatsList.size() !=0 ){
            mRecycleView.setVisibility(View.VISIBLE);
            mNoMessageShow.setVisibility(View.GONE);
            for(Chat chatting: chatsList){
                if(chatting.getSender().equals(mShowedUSer.getId())){
                    mUserChatList.add(chatting.getReceiver());
                }
                if(chatting.getReceiver().equals(mShowedUSer.getId())){
                    mUserChatList.add(chatting.getSender());
                }
            }
            getUserForMessages();

        }
    }


    private void getUserForMessages(){

        if(NetworkCheker.getInstance().isNetworkAvailable(getContext())) {
            mUserViewModel
                    .retrieveUserForMEssages().subscribe(new Subscriber<List<User>>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    subscription.request(1L);
                    if (mUserSubscription != null) {
                        mUserSubscription.cancel();

                    }
                    mUserSubscription = subscription;

                }

                @Override
                public void onNext(List<User> users) {
                    StopWhaitSpinner();
                    mUserList = users;
                    readChats();
                }

                @Override
                public void onError(Throwable t) {
                    StopWhaitSpinner();
                    ShowRetrievedErrorPopupDialog();
                }

                @Override
                public void onComplete() {

                }
            });
        }
    }

    private void readChats(){

        mUsers = new ArrayList<>();

                    for(User user: mUserList){

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
                    if(mUserAdapter == null){
                        mUserAdapter = new UserAdapter(getContext(), mUsers);
                        mRecycleView.setAdapter(mUserAdapter);
                    } else

                        mUserAdapter.notifyDataSetChanged();

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
    /**
     * Show's whait spinner
     */
    private void ShowWhaitSpinner() {

        this.popupDialogProgress = ProgressDialog.show(getActivity(), null, getString(R.string.loading_popup_message_spinner), true);
    }

    private void StopWhaitSpinner(){
        if(this.popupDialogProgress != null) {
            this.popupDialogProgress.dismiss();
        }

    }

    /**
     * It show's a no internet connection popup
     */
    private void ShowNoInternetConnection(){
        StopWhaitSpinner();
        //There was an error show error message
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.recconnecting_request)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /**
     * It shows a Dialog containing an error to the user
     */
    private void ShowRetrievedErrorPopupDialog(){
        ShowWhaitSpinner();
        //there is an error, show popup message
        Context context;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.error_message_download_resources)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
