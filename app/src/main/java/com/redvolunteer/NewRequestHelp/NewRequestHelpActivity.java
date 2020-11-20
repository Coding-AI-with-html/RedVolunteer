package com.redvolunteer.NewRequestHelp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.redvolunteer.MainActivity;
import com.redvolunteer.R;
import com.redvolunteer.RedVolunteerApplication;
import com.redvolunteer.ViewModels.HelpRequestViewModel;
import com.redvolunteer.ViewModels.UserViewModel;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;
import com.redvolunteer.pojo.User;
import com.google.android.libraries.places.api.Places;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class NewRequestHelpActivity extends AppCompatActivity  {
    private static final String TAG = "NewRequestHelpActivity";

    private Context mContext = NewRequestHelpActivity.this;


    /**
     * Request being craited
     */
    private RequestHelp newHelpRequest;
    /**
     * User View Model
     */
    private UserViewModel mUserViewModel;
    /**
     * Request View Model
     */
    private HelpRequestViewModel MainViewModel;

    /**
     * PLACE PICKER CONSTANT
     * @param savedInstanceState
     */
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int REQUEST_CODE = 101;

    /**
     * Layout
     */
    private EditText mHelpRequestName;
    private TextView mRetrievedPositionLabel;
    private EditText mRequestDescription;


    /**
     * Location Retrieved
     */
    private RequestLocation mRetrievedLocation;

    /**
     * If Fields were be empty
     */
    private boolean nameSelected = false;
    private boolean positionSelected = false;
    private boolean mDescriptionWrited = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_help_request);
        MainViewModel = ((RedVolunteerApplication) getApplication()).getHelpRequestViewModel();
        mUserViewModel = ((RedVolunteerApplication) getApplication()).getUserViewModel();
        bind();
    }

    /**
     * Binds UI to the layout
     */
    private void bind(){
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));

        PlacesClient ClientPlace = Places.createClient(mContext);

        mHelpRequestName = (EditText) findViewById(R.id.new_request_name);
        mRequestDescription = (EditText) findViewById(R.id.new_request_description);
        LinearLayout choseMap = (LinearLayout) findViewById(R.id.new_help_request_map);

        ImageView mBackButton = (ImageView) findViewById(R.id.new_request_cancel_button);
        Button finishButton = findViewById(R.id.new_request_button_finish);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }

        });

        mHelpRequestName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nameSelected = mHelpRequestName.getText().length() > 0;
            }
        });

        choseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Starting navigation acitivity");
                List<Place.Field> placeField = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

                FindCurrentPlaceRequest requestLocation = FindCurrentPlaceRequest.builder(placeField).build();

                /**
                 * Takes User current location and makes it HelpRequestLocation
                 */

                if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) mContext, new String[]
                            {ACCESS_FINE_LOCATION}, REQUEST_CODE);
                } else {

                    ClientPlace.findCurrentPlace(requestLocation).addOnSuccessListener(new OnSuccessListener<FindCurrentPlaceResponse>() {
                        @Override
                        public void onSuccess(FindCurrentPlaceResponse findCurrentPlaceResponse) {
                           for(PlaceLikelihood placeLikelihood: findCurrentPlaceResponse.getPlaceLikelihoods()){
                               Log.d(TAG, "onSuccess: something else" + placeLikelihood.getPlace().getLatLng().longitude + "Latitude" + placeLikelihood.getPlace().getLatLng().latitude);
                           }
                        }
                    });








                }



            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isFormFilled()){

                    RequestHelp requestHelp = new RequestHelp();
                    requestHelp.setName(mHelpRequestName.getText().toString());
                    requestHelp.setRequestLocation(mRetrievedLocation);
                    requestHelp.setDescription(mRequestDescription.getText().toString());




                } else  {
                    showRetrievedErrorPopupDialog();
                }
            }
        });
    }

    public User getHelpRequestCreator(){

        return mUserViewModel.retrieveCachedUser();
    }

    public RequestLocation getUserLocation(){
        return MainViewModel.getUserLocation();
    }




    public void finish(RequestHelp newRequestHelp){
        this.newHelpRequest = newRequestHelp;

        MainViewModel.createNewHelpRequest(newRequestHelp);


        startActivity(new Intent(this, MainActivity.class));
    }
    /**
     * Returns truee if the form is filled, otherwise return false
     */
    private boolean isFormFilled(){
        boolean isFilled = false;
        if(nameSelected && positionSelected && mRequestDescription.getText().toString().length()> 0){
            isFilled = true;
        }
        return isFilled;
    }



    /**
   * it shows error popup
  */
  private void showRetrievedErrorPopupDialog(){

      //of there's an error show a popup message
     AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
     builder.setMessage("Hey you forgot some parametres")
             .setCancelable(false)
             .setPositiveButton(R.string.ok_button, null);
     AlertDialog dialog = builder.create();
     dialog.show();



  }

}
