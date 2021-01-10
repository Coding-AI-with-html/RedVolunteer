package com.redvolunteer.utils.persistence.sharedpreferencespersistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.R;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.persistence.LocalUserDao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    private static final String REQUEST_FIELD = "my_requests";

    private SharedPreferences sharedPreferences;

    @Override
    public void save(User userToStore) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        //save user fields
        editor.putString(ID_FIELD, userToStore.getId());
        editor.putString(NAME_FIELD, userToStore.getName());
        editor.putString(SURNAME_FIELD, userToStore.getSurname());
        editor.putString(CONTACT_FIELD, userToStore.getEmail());
        //editor.putString(IMAGE_FIELD, userToStore.getPhoto());
        //editor.putString(BIO_FIELD, userToStore.getBiography());
        editor.putLong(BIRTH_FIELD, userToStore.getBirthDay());

        //commit changes
        editor.apply();


    }

    @Override
    public User load() {


        //retrieve data from localstore
        User localStoredUser = new User();
        localStoredUser.setId(sharedPreferences.getString(ID_FIELD, null));
        localStoredUser.setName(sharedPreferences.getString(NAME_FIELD, null));
        localStoredUser.setEmail(sharedPreferences.getString(CONTACT_FIELD, null));
        //localStoredUser.setPhoto(sharedPreferences.getString(IMAGE_FIELD, null));
        //localStoredUser.setBiography(sharedPreferences.getString(BIO_FIELD, null));
        localStoredUser.setBirthDay(sharedPreferences.getLong(BIRTH_FIELD, 0));

        List<String> requests = new ArrayList<>();
        requests.addAll(sharedPreferences.getStringSet(REQUEST_FIELD, new HashSet<String>()));
        return localStoredUser;


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
