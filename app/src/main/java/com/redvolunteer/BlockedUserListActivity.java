package com.redvolunteer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
    CircularImageView mProfilePhoto;
    TextView mUserName;


    private ProgressDialog popupDialogProgress;

    private UserAdapter mUSerAdapter;

    ListView mBlockedUserList;

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
            ShowWhaitSpinner();
            mMainViewModel.loadCurrUserBlockedUserList(UserID).subscribe(new Subscriber<List<User>>() {
                @Override
                public void onSubscribe(Subscription subscription) {

                    subscription.request(1L);

                    Usersubscription  = subscription;
                }

                @Override
                public void onNext(List<User> users) {
                    StopWhaitSpinner();
                    setLayout();
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


    private void setLayout(){
        mNoBlockedUser = (LinearLayout) findViewById(R.id.no_bloc_user_text);
        mProfilePhoto = (CircularImageView) findViewById(R.id.blocked_user_photo);
        mUserName = (TextView) findViewById(R.id.blocked_user_name);
        mBlockedUserList = (ListView) findViewById(R.id.blocked_user_list);

        mProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentToProfileDetails = new Intent(getApplicationContext(), UserDetailsActivity.class);
                intentToProfileDetails.putExtra(ExtraLabels.USER_ID, mShowingBlockedUser.getId());
                startActivity(intentToProfileDetails);


            }
        });


    }


    /**
     * Show's whait spinner
     */
    private void ShowWhaitSpinner() {

        this.popupDialogProgress = ProgressDialog.show(getApplicationContext(), null, getString(R.string.loading_popup_message_spinner), true);
    }

    private void StopWhaitSpinner(){
        if(this.popupDialogProgress != null) {
            this.popupDialogProgress.dismiss();
        }

    }
}
