package com.redvolunteer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.fragments.UserRequestFragment;
import com.redvolunteer.pojo.RequestLocation;
import com.redvolunteer.pojo.User;
import com.redvolunteer.utils.ValidateUtils;
import com.redvolunteer.viewmodels.HelpRequestViewModel;
import com.redvolunteer.viewmodels.UserViewModel;
import com.redvolunteer.fragments.ProfileFragment;
import com.redvolunteer.fragments.RequestWallFragment;
import com.redvolunteer.fragments.UserMessageFragment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.redvolunteer.LoginAndRegister.Login;

public class MainActivity extends AppCompatActivity implements FragmentInteractionListener {

    private static final String TAG = "MainActivity";

    /**
     *Location Permission constant
     */
    private static  final int LOCATION_PERMISSION = 1;
    /**
     * Request check settings
     */
    public static final int REQUEST_CHECK_SETTINGS = 2;

    /**
     * User View Model
     */
    private UserViewModel mUserViewModel;

    /**
     * Help Request View Model
     */
    private HelpRequestViewModel mHelpRequestViewModel;

    /**
     * Help request who created user fragment
     */
    private RequestWallFragment mainFragment = new RequestWallFragment();
    /**
     * User Messages Fragment
     */
    private UserMessageFragment myMessageFragment = new UserMessageFragment();

    /***
     * User Created RequestsHelps
     */
    private UserRequestFragment myRequestFragment = new UserRequestFragment();
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
    public static final String MESSAGES_FRAGMENT = "messages";
    public static final String MY_REQUEST_FRAGMENT = "requests";
    public static final String PROFILE_FRAGMENT = "profile";

    /**
     * bottomBar compenents
     * @param savedInstanceState
     */
    private LinearLayout mWallButtonPressed;
    private LinearLayout mWallButton;
    private LinearLayout mProfileButton;
    private LinearLayout mProfileButtonPressed;
    private LinearLayout mMyMessagesButton;
    private LinearLayout mMyMessagesButtonPressed;
    private LinearLayout mMyRequestsButton;
    private LinearLayout mMyRequestsButtonPressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserViewModel = getUserViewModel();
        mHelpRequestViewModel = getHelpRequestViewModel();

        if (!mUserViewModel.isAuth()) {
            signOut();
        }
        setContentView(R.layout.activity_main);
        bindLayoutComponents();
        setFragments();

        //find user location
        provideLocation();


    }

    private void bindLayoutComponents() {
        mWallButtonPressed = (LinearLayout) findViewById(R.id.button_wall_pressed);
        mWallButton = (LinearLayout) findViewById(R.id.button_wall_not_pressed);
        mProfileButton = (LinearLayout) findViewById(R.id.profile_button_not_pressed);
        mProfileButtonPressed = (LinearLayout) findViewById(R.id.profile_button_pressed);
        mMyMessagesButton = (LinearLayout) findViewById(R.id.mymessages_button_not_pressed);
        mMyMessagesButtonPressed = (LinearLayout) findViewById(R.id.mymessages_button_pressed);
         mMyRequestsButton = (LinearLayout) findViewById(R.id.myrequest_button_not_pressed);
         mMyRequestsButtonPressed = (LinearLayout) findViewById(R.id.myrequests_button_pressed);

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

        mMyMessagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction(MESSAGES_FRAGMENT);
            }
        });
        mMyRequestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction(MY_REQUEST_FRAGMENT);
            }
        });


    }


    /**
     * Intaliazes fragments
     * @param
     */
    private void setFragments() {

        this.fragmentMap = new HashMap<>();
        fragmentMap.put(WALL_FRAGMENT, mainFragment);
        fragmentMap.put(MESSAGES_FRAGMENT, myMessageFragment);
        fragmentMap.put(PROFILE_FRAGMENT, profileFragment);
        fragmentMap.put(MY_REQUEST_FRAGMENT, myRequestFragment);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_fragment, mainFragment)
                .add(R.id.main_fragment, myMessageFragment)
                .add(R.id.main_fragment, myRequestFragment)
                .add(R.id.main_fragment, profileFragment)
                .hide(myMessageFragment)
                .hide(profileFragment)
                .hide(myRequestFragment)
                .commit();
        stack.push(mainFragment);

    }


    /**
     *change fragments
     * @param
     */
    public void fragmentTransaction(Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .hide(mainFragment)
                .hide(myMessageFragment)
                .hide(profileFragment)
                .hide(myRequestFragment)
                .show(fragment)
                .commitAllowingStateLoss();
        stack.push(fragment);

    }

    public void fragmentTransaction(String fragmentID) {

        if (fragmentID.equals(WALL_FRAGMENT)) {
            openWall();
        }
        if (fragmentID.equals(MESSAGES_FRAGMENT)) {
            openMyMessages();
        }
        if (fragmentID.equals(PROFILE_FRAGMENT)) {
            openProfile();
        }
        if(fragmentID.equals(MY_REQUEST_FRAGMENT)){
            openMyRequests();
        }

        fragmentTransaction(fragmentMap.get(fragmentID));
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        //super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if(stack.isEmpty() || stack.pop() instanceof RequestWallFragment)
            super.onBackPressed();
        else {

            Fragment oldFrag = stack.pop();

            String switchFragmentID = null;

            if(oldFrag == mainFragment){
                switchFragmentID = WALL_FRAGMENT;
            } else if(oldFrag == myMessageFragment){
                switchFragmentID = MESSAGES_FRAGMENT;
            } else if(oldFrag == profileFragment){
                switchFragmentID = PROFILE_FRAGMENT;
            } else if(oldFrag == myRequestFragment){
                switchFragmentID = MY_REQUEST_FRAGMENT;
            }

            fragmentTransaction(switchFragmentID);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // position retrieval
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                if (resultCode == RESULT_OK)
                    provideLocation();
                else {
                    fragmentTransaction(WALL_FRAGMENT);
                    Toast.makeText(getApplicationContext(), R.string.location_permission_notallowed_toast, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }



    /**
     * Signs out user
     */
    private void signOut(){
        mUserViewModel.signOut();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
    }

    @Override
    public UserViewModel getUserViewModel() {
        if(this.mUserViewModel == null)
            mUserViewModel = ((RedVolunteerApplication) getApplication()).getUserViewModel();
        return mUserViewModel;
    }

    @Override
    public HelpRequestViewModel getHelpRequestViewModel(){
        if(mHelpRequestViewModel == null){
            mHelpRequestViewModel = ((RedVolunteerApplication) getApplication()).getHelpRequestViewModel();
        }
        return mHelpRequestViewModel;
    }

    private void openWall() {

        mWallButtonPressed.setVisibility(View.VISIBLE);
        mWallButton.setVisibility(View.GONE);
        mProfileButton.setVisibility(View.VISIBLE);
        mProfileButtonPressed.setVisibility(View.GONE);
        mMyMessagesButton.setVisibility(View.VISIBLE);
        mMyMessagesButtonPressed.setVisibility(View.GONE);
        mMyRequestsButton.setVisibility(View.VISIBLE);
        mMyRequestsButtonPressed.setVisibility(View.GONE);

    }

    private void  openMyMessages() {

        mWallButtonPressed.setVisibility(View.GONE);
        mWallButton.setVisibility(View.VISIBLE);
        mProfileButton.setVisibility(View.VISIBLE);
        mProfileButtonPressed.setVisibility(View.GONE);
        mMyMessagesButton.setVisibility(View.GONE);
        mMyMessagesButtonPressed.setVisibility(View.VISIBLE);
        mMyRequestsButton.setVisibility(View.VISIBLE);
        mMyRequestsButtonPressed.setVisibility(View.GONE);

    }

    private void openProfile() {

        mWallButtonPressed.setVisibility(View.GONE);
        mWallButton.setVisibility(View.VISIBLE);
        mProfileButton.setVisibility(View.GONE);
        mProfileButtonPressed.setVisibility(View.VISIBLE);
        mMyMessagesButton.setVisibility(View.VISIBLE);
        mMyMessagesButtonPressed.setVisibility(View.GONE);
        mMyRequestsButton.setVisibility(View.VISIBLE);
        mMyRequestsButtonPressed.setVisibility(View.GONE);

    }
    private void openMyRequests(){

        mWallButtonPressed.setVisibility(View.GONE);
        mWallButton.setVisibility(View.VISIBLE);
        mProfileButton.setVisibility(View.VISIBLE);
        mProfileButtonPressed.setVisibility(View.GONE);
        mMyMessagesButton.setVisibility(View.VISIBLE);
        mMyMessagesButtonPressed.setVisibility(View.GONE);
        mMyRequestsButton.setVisibility(View.GONE);
        mMyRequestsButtonPressed.setVisibility(View.VISIBLE);

    }

    /**
     * CHeck for location Permission from user and retrieves it
     */
    public void provideLocation(){


        String provider = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.equals("")){

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
            } else {
                enableGoogleApiClient();
            }
        } else {
            Toast.makeText(getApplicationContext(), R.string.gps_not_available, Toast.LENGTH_SHORT).show();

            fragmentTransaction(WALL_FRAGMENT);
        }
    }


    /**
     * Ask user to enable the GPS
     */
    @SuppressLint("MissingPermission")
    public void enableGoogleApiClient() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(this);
        final Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {


                LocationCallback callback = new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        for (Location location : locationResult.getLocations()) {
                            RequestLocation loc = new RequestLocation();
                            loc.setLatitude(location.getLatitude());
                            loc.setLongitude(location.getLongitude());
                            mHelpRequestViewModel.setLocation(loc);
                        }
                    }


                };


                FusedLocationProviderClient fuserLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(10000);
                locationRequest.setFastestInterval(5000);
                locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

                // retrieve new location
                fuserLocationClient.requestLocationUpdates(locationRequest,
                        callback,
                        null);

                // parallely get last known location, to have info as soon as possible
                fuserLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            RequestLocation loc = new RequestLocation();
                            loc.setLatitude(location.getLatitude());
                            loc.setLongitude(location.getLongitude());
                            mHelpRequestViewModel.setLocation(loc);
                        }



                        fragmentTransaction(WALL_FRAGMENT);

                    }
                });


            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(getParent(),
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                }
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case LOCATION_PERMISSION: {
                if(grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    enableGoogleApiClient();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.location_permission_notallowed_toast, Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }



}