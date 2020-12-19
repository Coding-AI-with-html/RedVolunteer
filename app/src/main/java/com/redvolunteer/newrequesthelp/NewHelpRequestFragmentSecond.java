package com.redvolunteer.newrequesthelp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import com.redvolunteer.ConfirmAddress;
import com.redvolunteer.MapsActivity;
import com.redvolunteer.R;

public class NewHelpRequestFragmentSecond extends Fragment {

    private static final String TAG = "NewHelpRequestFragmentS";
    private  static final int LOCATION_PERMISSION = 1;

    private static final int REQUEST_CODE = 101;

    /**
     * Request check seetings
     */
    public static final int REQUEST_CHECK_SETTINGS = 2;


    private NewHelpRequestFragmentListener mListener;
    /**
     * for Geeting address
     */
    Geocoder geocoder;
    /**
     * layout
     */
    private TextView mRequestLocationLabel;
    LinearLayout mapChose;
    Context context;
    View myView;


    /**
     * To check if all fields are not empty
     */
    private boolean positionSelected = false;

    public NewHelpRequestFragmentSecond(){
        //Required empty constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //provideUserLocation();


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


        mBackButtonToFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });



        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFormFilled()){

                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_new_help_request_page_two, container, false);
        context = myView.getContext();
        mapChose = (LinearLayout) myView.findViewById(R.id.new_help_request_map);
        mapChose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlacePicker.IntentBuilder()
                        .setLatLong(40.748672, -73.985628)  // Initial Latitude and Longitude the Map will load into
                        .showLatLong(true)  // Show Coordinates in the Activity
                        .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
                        .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
                        .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
                        .setMarkerDrawable(R.drawable.marker) // Change the default Marker Image
                        .setMarkerImageImageColor(R.color.colorPrimary)
                        .setFabColor(R.color.fabColor)
                        .setPrimaryTextColor(R.color.colorPrimaryDark) // Change text color of Shortened Address
                        .setSecondaryTextColor(R.color.secondaryTextColor) // Change text color of full Address
                        .setBottomViewColor(R.color.bottomViewColor) // Change Address View Background Color (Default: White)
                        .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
                        .setMapType(MapType.NORMAL)
                        .setPlaceSearchBar(true, GOOGLE_API_KEY) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
                        .onlyCoordinates(true)  //Get only Coordinates from Place Picker
                        .hideLocationButton(true)   //Hide Location Button (Default: false)
                        .disableMarkerAnimation(true)   //Disable Marker Animation (Default: false)
                        .build(this)
                startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST);
            }




        });
        return myView;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



    private void provideUserLocation(){

        String provider = Settings.Secure.getString(getContext().getApplicationContext().getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(!provider.equals("")){

            if(ContextCompat.checkSelfPermission(getContext().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);


            } else {

            }



        } else {
            Toast.makeText(getContext(), R.string.gps_not_available, Toast.LENGTH_LONG).show();


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


}
