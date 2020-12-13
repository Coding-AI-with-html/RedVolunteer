package com.redvolunteer.dataModels;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.redvolunteer.R;
import com.redvolunteer.pojo.User;

public class FIrebaseUserInfoProviderMethod {
    private static final String TAG = "FIrebaseUserInfoProvider";


    /**
     * Firebase
     * @param context
     */
    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private Context mContext;

    public FIrebaseUserInfoProviderMethod(Context context) {

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        dataRef= mFirebaseDatabase.getReference();
        mContext = context;
        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }


    public UserInfoProvider getUserInfo(DataSnapshot dataSnapshot) {

        User user = new User();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            if(ds.getKey().equals(mContext.getString(R.string.database_Help_seekers))) {
                Log.d(TAG, "getUserInfo: info user " + ds);
                try{
                    user.setId(
                            ds.child(userID)
                            .getValue(User.class)
                            .getId()
                    );
                    user.setFullName(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getFullName()
                    );
                    user.setFullSurname(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getFullSurname()
                    );

                    user.setBirthDate(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getBirthDate()
                    );
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

        }


        return new UserInfoProvider(user);
    }
}
