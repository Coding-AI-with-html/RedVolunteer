package com.redvolunteer.newrequesthelp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
import com.redvolunteer.MapsActivity;
import com.redvolunteer.R;
import com.redvolunteer.pojo.RequestLocation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_OK;

public class NewHelpRequestFragmentSecond extends Fragment {

    private static final String TAG = "NewHelpRequestFragmentS";
    private  static final int LOCATION_PERMISSION = 1;

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
    NewHelpRequestFragmentSecond mContext;


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
        provideUserLocation();
        mContext = NewHelpRequestFragmentSecond.this;


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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CHECK_SETTINGS:
                if(resultCode == RESULT_OK)
                    enableGoogleApi();
                else {
                    Toast.makeText(getContext(), "Turite ijungti navigacija", Toast.LENGTH_LONG).show();
                }
                break;
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_help_request_page_two, container, false);
    }

    /**
     * ask the user to Enable the GPs
     */
    @SuppressLint("MissingPermission")
    public void enableGoogleApi(){

        LocationRequest mLocationReq = new LocationRequest();
        mLocationReq.setInterval(10000);
        mLocationReq.setFastestInterval(5000);
        mLocationReq.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        LocationSettingsRequest.Builder LocBuilder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationReq);
        LocBuilder.setAlwaysShow(true);

        SettingsClient client = LocationServices.getSettingsClient(getContext());
        final Task<LocationSettingsResponse> task = client.checkLocationSettings(LocBuilder.build());
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {


                LocationCallback callback = new LocationCallback(){

                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        for(Location location: locationResult.getLocations()){

                            RequestLocation loc = new RequestLocation();
                            loc.setLatitude(loc.getLatitude());
                            loc.setLongitude(location.getLongitude());
                            List<Address> addresses;

                            geocoder = new Geocoder(getContext(), Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(loc.getLatitude(),loc.getLongitude(),1);

                                String address = addresses.get(0).getAddressLine(0);
                                String country = addresses.get(0).getCountryName();
                                Log.d(TAG, "onLocationResult:Adresas "+ address + "Salis"+ country);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                        }

                    }
                };

                FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
                LocationRequest  locRequest = new LocationRequest();
                locRequest.setInterval(10000);
                locRequest.setFastestInterval(5000);
                locRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

                fusedLocationProviderClient.requestLocationUpdates(locRequest,
                        callback,
                        null);
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {


                        if(location != null){

                            Log.d(TAG, "onSuccess: Location Getting suscesfully  ");
                        }
                    }
                });
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                int StatusCode = ((ApiException)e).getStatusCode();
                switch (StatusCode){

                    case CommonStatusCodes
                            .RESOLUTION_REQUIRED:

                        try{

                            ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                            resolvableApiException.startResolutionForResult(getActivity(),
                                    REQUEST_CHECK_SETTINGS);
                        }
                        catch (IntentSender.SendIntentException sendException){

                        }
                        break;


                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case LOCATION_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableGoogleApi();
                } else {
                    Toast.makeText(getContext(), "Ijunkite navigacija!", Toast.LENGTH_LONG).show();
                }
                break;
            }
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
                enableGoogleApi();
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
