package com.redvolunteer.NewRequestHelp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.redvolunteer.MapsActivity;
import com.redvolunteer.R;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class NewHelpRequestFragmentSecond extends Fragment {

    private static final int PLACE_PICKER = 1;
    private static final int  REQUEST_CODE = 101;
    private static final String TAG = "NewHelpRequestFragmentS";


    private NewHelpRequestFragmentListener mListener;

    /**
     * layout
     */
    private TextView mRequestLocationLabel;
    LinearLayout mapChose;
    View myView;
    Context context;


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

    private void getLocationPermission() {
        ActivityCompat.requestPermissions(getActivity(),new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        return;

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
                if(ContextCompat.checkSelfPermission(NewHelpRequestFragmentSecond.this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(NewHelpRequestFragmentSecond.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Intent myIntent = new Intent(getContext(), MapsActivity.class);
                    getActivity().startActivity(myIntent);

                } else {
                    showLocationRequestPermission();

                }
            }




        });
        return myView;

    }
    private void showLocationRequestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION ) && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))

    } else {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION});
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