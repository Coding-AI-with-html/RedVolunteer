package com.redvolunteer;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.NetworkCheker;
import com.redvolunteer.viewmodels.UserViewModel;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;

public class BlockedUserListActivity  extends AppCompatActivity {

    private static final String TAG = "BlockedUserListActivity";

    private UserViewModel mMainViewModel;

    private Subscription Usersubscription;
    private User mShowingBlockedUser;
    CircularImageView mProfilePhoto;
    TextView mUserName;

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
