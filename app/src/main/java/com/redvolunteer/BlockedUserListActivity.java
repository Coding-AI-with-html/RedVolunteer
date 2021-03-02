package com.redvolunteer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.redvolunteer.adapters.UserAdapter;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.utils.persistence.ExtraLabels;
import com.redvolunteer.viewmodels.UserViewModel;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;

public class BlockedUserListActivity  extends AppCompatActivity {

    private static final String TAG = "BlockedUserListActivity";

    private UserViewModel mMainViewModel;

    private Subscription Usersubscription;
    private User mShowingBlockedUser;
    ImageView mProfilePhoto;
    TextView mUserName;


    private ProgressDialog popupDialogProgress;

    private UserAdapter mUSerAdapter;

    RecyclerView mBlockedUserList;
    List<User> blockedUsers;

    private LinearLayout mNoBlockedUser;



    private User CurrentUser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mMainViewModel = ((RedVolunteerApplication) getApplication()).getUserViewModel();

        CurrentUser = mMainViewModel.retrieveCachedUser();
        String UserID = CurrentUser.getId();
        setContentView(R.layout.blockes_user_list_activity);

        if(NetworkCheker.getInstance().isNetworkAvailable(this)){
            mMainViewModel.loadCurrUserBlockedUserList(UserID).subscribe(new Subscriber<List<User>>() {
                @Override
                public void onSubscribe(Subscription subscription) {

                    subscription.request(1L);

                    Usersubscription  = subscription;
                }

                @Override
                public void onNext(List<User> users) {
                    setLayout(users);
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



    private void setLayout(List<User> mUsers){
        mNoBlockedUser = (LinearLayout) findViewById(R.id.no_bloc_user_text);
        mBlockedUserList = (RecyclerView) findViewById(R.id.blocked_user_list);

       blockedUsers = mUsers;
        mBlockedUserList.setHasFixedSize(true);
        mBlockedUserList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

      mUSerAdapter = new UserAdapter(getApplicationContext(), blockedUsers);
      mBlockedUserList.setAdapter(mUSerAdapter);

    }








}
