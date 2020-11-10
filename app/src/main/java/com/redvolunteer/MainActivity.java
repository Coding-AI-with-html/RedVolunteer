package com.redvolunteer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.redvolunteer.fragments.ProfileFragment;
import com.redvolunteer.fragments.RequestWallFragment;
import com.redvolunteer.fragments.UserMessageFragment;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import LoginAndRegister.Login;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener FAuthListener;
    /**
     *
     * @param savedInstanceState
     */
    private Context MainContext = MainActivity.this;



    /**
     * Help request who created user fragment
     */
    private RequestWallFragment mainFragment = new RequestWallFragment();
    /**
 * User Messages Fragment
 */
private UserMessageFragment myMessageFragment = new UserMessageFragment();

/**
 * Profile fragment
 */
private ProfileFragment profileFragment = new ProfileFragment();

/**
 * Stack used to manage Fragment states(back button)
 */
private LinkedList<androidx.fragment.app.Fragment> stack = new LinkedList<>();
    /**
     * maps fragments
     */
    private Map<String, androidx.fragment.app.Fragment> fragmentMap;


    /**
     * contants used to handle fragments
     */
    public static final String WALL_FRAGMENT = "main";
    public static final String MESSAGES_FRAGMENT = "requests";
    public static final String PROFILE_FRAGMENT = "profile";

    /**
     * bottomBar compenents
     * @param savedInstanceState
     */
    private LinearLayout mWallButtonPressed;
    private LinearLayout mWallButton;
    private LinearLayout mProfileButton;
    private LinearLayout mProfileButtonPressed;
    private LinearLayout mMyRequestButton;
    private LinearLayout mMyRequestButtonPressed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: Started main Activity");

        bindLayoutComponents();
        setFragments();
        setupFirebaseAuth();
    }

    private void bindLayoutComponents(){
        mWallButtonPressed = (LinearLayout) findViewById(R.id.button_wall_pressed);
        mWallButton = (LinearLayout) findViewById(R.id.button_wall_not_pressed);
        mProfileButton = (LinearLayout) findViewById(R.id.profile_button_not_pressed);
        mProfileButtonPressed = (LinearLayout) findViewById(R.id.profile_button_pressed);
        mMyRequestButton = (LinearLayout) findViewById(R.id.myrequest_button_not_pressed);
        mMyRequestButtonPressed = (LinearLayout) findViewById(R.id.myrequest_button_pressed);

        mWallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction(WALL_FRAGMENT);
            }
        });

        mProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction(PROFILE_FRAGMENT);
            }
        });

        mMyRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction(MESSAGES_FRAGMENT);
            }
        });

    }

    /**
     * Intaliazes fragments
     * @param
     */
    private void setFragments(){

        this.fragmentMap = new HashMap<>();
        fragmentMap.put(WALL_FRAGMENT, mainFragment);
        fragmentMap.put(MESSAGES_FRAGMENT, myMessageFragment);
        fragmentMap.put(PROFILE_FRAGMENT, profileFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_fragment, mainFragment)
                .add(R.id.main_fragment, myMessageFragment)
                .add(R.id.main_fragment, profileFragment)
                .hide(myMessageFragment)
                .hide(profileFragment)
                .commit();
        stack.push(mainFragment);

    }

    /**
     *change fragments
     * @param fragmentID
     */
    public void fragmentTransaction(Fragment fragment){

        getSupportFragmentManager()
                .beginTransaction()
                .hide(mainFragment)
                .hide(myMessageFragment)
                .hide(profileFragment)
                .show(fragment)
                .commitAllowingStateLoss();
        stack.push(fragment);

    }


    public void fragmentTransaction(String fragmentID){

        if(fragmentID.equals(WALL_FRAGMENT)){
            openWall();
        }
        if(fragmentID.equals(MESSAGES_FRAGMENT)){
            openMyRequests();
        }
        if(fragmentID.equals(PROFILE_FRAGMENT)){
            openProfile();
        }

        fragmentTransaction(fragmentMap.get(fragmentID));
    }


    /**
     * Modify top and bottom bar according to the wall fragments
     */

    private void openWall(){

        mWallButtonPressed.setVisibility(View.VISIBLE);
        mWallButton.setVisibility(View.GONE);
        mProfileButton.setVisibility(View.VISIBLE);
        mProfileButtonPressed.setVisibility(View.GONE);
        mMyRequestButton.setVisibility(View.VISIBLE);
        mMyRequestButtonPressed.setVisibility(View.GONE);

    }

    private void openMyRequests(){

        mWallButtonPressed.setVisibility(View.GONE);
        mWallButton.setVisibility(View.VISIBLE);
        mProfileButton.setVisibility(View.VISIBLE);
        mProfileButtonPressed.setVisibility(View.GONE);
        mMyRequestButton.setVisibility(View.GONE);
        mMyRequestButtonPressed.setVisibility(View.VISIBLE);

    }

    private void openProfile(){

        mWallButtonPressed.setVisibility(View.GONE);
        mWallButton.setVisibility(View.VISIBLE);
        mProfileButton.setVisibility(View.GONE);
        mProfileButtonPressed.setVisibility(View.VISIBLE);
        mMyRequestButton.setVisibility(View.VISIBLE);
        mMyRequestButtonPressed.setVisibility(View.GONE);

    }




    private void checkCurrentUser(FirebaseUser currentUser){
        Log.d(TAG, "checkCurrentUser: checking if user has already logged");
        if(currentUser == null){
            Intent intent = new Intent(MainContext, Login.class);
            startActivity(intent);

        }
        
    }
    public void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: starting authentication");
        mAuth = FirebaseAuth.getInstance();
        FAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                //check if user logged in
                checkCurrentUser(currUser);

                if(currUser != null){
                    Log.d(TAG, "onAuthStateChanged: SIgned_in" + currUser.getUid());

                } else {
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };

    }
    private void signOut(){
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(FAuthListener);
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(FAuthListener != null){
            mAuth.removeAuthStateListener(FAuthListener);
        }
    }
}