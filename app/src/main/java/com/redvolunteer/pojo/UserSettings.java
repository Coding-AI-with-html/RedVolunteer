package com.redvolunteer.pojo;

import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.redvolunteer.R;

public class UserSettings {
    private static final String TAG = "UserSettings";

    private User user;
    private Context mContext;
    FirebaseAuth mAuth;

    public UserSettings(User user){
        this.user = user;

    }

    public UserSettings() {
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserSettings GetUserSettingsFromDatabase(DataSnapshot dataSnapshot){
        String userUID = mAuth.getCurrentUser().getUid();
        User user = new User();
        UserSettings mSetings = new UserSettings();

        for(DataSnapshot ds: dataSnapshot.getChildren()){

            if (ds.getKey().equals(mContext.getString(R.string.database_all_users))) {
                Log.d(TAG, "GetUserSettingsFromDatabase: Providing datasnapshot" + ds);

                user.setFullName(
                        ds.child(userUID)
                                .getValue(User.class)
                                .getFullName()
                );
                user.setFullSurname(
                        ds.child(userUID)
                                .getValue(User.class)
                                .getFullSurname()
                );
                user.setBirthDate(
                        ds.child(userUID)
                                .getValue(User.class)
                                .getBirthDate()
                );
                user.setPhoto(
                        ds.child(userUID)
                                .getValue(User.class)
                                .getPhoto()
                );
                user.setBiography(
                        ds.child(userUID)
                                .getValue(User.class)
                                .getBiography()
                );

            }
        }

        return new UserSettings(user);
    }


}
