package com.redvolunteer.utils.persistence.sharedpreferencespersistence;

import android.content.Context;
import android.content.SharedPreferences;

import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.persistence.LocalUserDao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalUserDaolmpl implements LocalUserDao {

    /**
     * Name of the user preferences
     */
    private static final String USER_PREFERENCE = "user_shared_preferences";

    /**
     * User Fields
     */
    private static final String ID_FIELD  = "id";
    private static final String NAME_FIELD = "name";
    private static final String SURNAME_FIELD = "surname";
    private static final String CONTACT_FIELD = "contact";
    private static final String IMAGE_FIELD = "image";
    private static final String BIO_FIELD = "bio";
    private static final String BIRTH_FIELD = "birthdate";

    private SharedPreferences sharedPreferences;

    @Override
    public void save(User userToStore) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        //save user fields
        editor.putString(ID_FIELD, userToStore.getId());
        editor.putString(NAME_FIELD, userToStore.getFullName());
        editor.putString(SURNAME_FIELD, userToStore.getFullSurname());
        editor.putString(CONTACT_FIELD, userToStore.getEmail());
        editor.putString(IMAGE_FIELD, userToStore.getPhoto());
        editor.putString(BIO_FIELD, userToStore.getBiography());
        editor.putLong(BIRTH_FIELD, userToStore.getBirthDate());

        //commit changes
        editor.apply();


    }

    @Override
    public User load() {

        User localStoreUser = new User();

        //retrieve data from localstore
        localStoreUser.setId(sharedPreferences.getString(ID_FIELD, null));
        localStoreUser.setFullName(sharedPreferences.getString(NAME_FIELD, null));
        localStoreUser.setFullSurname(sharedPreferences.getString(SURNAME_FIELD, null));
        localStoreUser.setEmail(sharedPreferences.getString(CONTACT_FIELD, null));
        localStoreUser.setPhoto(sharedPreferences.getString(IMAGE_FIELD, null));
        localStoreUser.setBiography(sharedPreferences.getString(BIO_FIELD, null));
        localStoreUser.setBirthDate(sharedPreferences.getLong(BIRTH_FIELD, 0));


        return localStoreUser;

    }

    @Override
    public void wipe() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

    public LocalUserDaolmpl(Context context){

        sharedPreferences = context.getSharedPreferences(USER_PREFERENCE, 0);
    }
}
