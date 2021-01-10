package com.redvolunteer.newrequesthelp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.redvolunteer.R;
import com.redvolunteer.RedVolunteerApplication;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;
import com.redvolunteer.pojo.User;
import com.redvolunteer.viewmodels.UserViewModel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class NewHelpRequestFragmentSecond extends Fragment {

    private static final String TAG = "NewHelpRequestFragmentS";
    /**
     * Place picker constant
     */
    private static final int PLACE_PICKER_REQUEST = 1;

    private DatabaseReference dataRef;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    /**
     * Location retrieved
     */
    private RequestLocation mRetrievedLocation;
    /**
     * User View MOdel
     */
    private UserViewModel mUserViewMODel;

    Object UserCreator = new User();

    private NewHelpRequestFragmentListener mListener;
    /**
     * layout
     */
    private TextView mRequestLocationLabel;
    WifiManager wifiManager;

    /**
     * To check if all fields are not empty
     */
    private boolean positionSelected = false;

    public NewHelpRequestFragmentSecond() {
        //Required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RequestLocation mUserLocation = mListener.getHelpRequestCreatorLocation();
        wifiManager= (WifiManager) this.getContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bind(view);
        super.onViewCreated(view, savedInstanceState);

    }

    /**
     * Binds UI to the field
     */
    private void bind(View view){


        ImageView mBackButtonToFirst = (ImageView) view.findViewById(R.id.new_help_request_page_back_button);
        Button buttonFinish = (Button) view.findViewById(R.id.new_help_request_button_finish);
        LinearLayout mapChosing = (LinearLayout) view.findViewById(R.id.new_help_request_map);
        mRequestLocationLabel  = (TextView) view.findViewById(R.id.position_label);


        mBackButtonToFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });



        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mAuth = mAuth.getInstance();
                if(isFormFilled()){
                    String userUID = mAuth.getCurrentUser().getUid();
                    RequestHelp requestHelp = mListener.getHelpRequest();
                    requestHelp.setHelpRequestCreatorID(userUID);
                    requestHelp.setRequestLocation(mRetrievedLocation);

                    mListener.finish(requestHelp);

                }
                else {
                    showRetrieveErrorPopupDialog();

                }
            }
        });
        mapChosing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Disable Wifi
                wifiManager.setWifiEnabled(false);
                openPlacePicker();
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_help_request_page_two, container, false);


    }


    private void openPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);

            //Enable Wifi
            wifiManager.setWifiEnabled(true);

        } catch (GooglePlayServicesRepairableException e) {
            Log.d("Exception",e.getMessage());

            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.d("Exception",e.getMessage());

            e.printStackTrace();
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(getContext(), data);

                mRetrievedLocation = new RequestLocation(place.getLatLng().latitude, place.getLatLng().longitude);
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                try {

                    List<Address> addressList = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                    String addres = addressList.get(0).getAddressLine(0);
                    mRetrievedLocation.setName(addres.toString());
                    mRequestLocationLabel.setText(addres.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                positionSelected = true;

            }
        }
    }






    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof NewHelpRequestFragmentListener){
            mListener = (NewHelpRequestFragmentListener) context;

        } else {
            throw  new RuntimeException(context.toString()
                    + " Must inplement NewFragmentListener");
        }


    }


    @Override
    public void onDetach() {
        super.onDetach();
    mListener = null;
    }

    private boolean isFormFilled(){
        boolean isFilled = false;
        if(positionSelected){
            isFilled = true;
        }
        return isFilled;
    }

    private void showRetrieveErrorPopupDialog() {

        // there was an error, show a popup message
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.form_not_completely_filled_popup)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button, null);
        AlertDialog alert = builder.create();
        alert.show();
    }

}
