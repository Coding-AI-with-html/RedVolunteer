package com.redvolunteer.NewRequestHelp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.redvolunteer.MainActivity;
import com.redvolunteer.R;
import com.redvolunteer.RedVolunteerApplication;
import com.redvolunteer.ViewModels.HelpRequestViewModel;
import com.redvolunteer.ViewModels.UserViewModel;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;
import com.redvolunteer.pojo.User;
import com.google.android.libraries.places.api.Places;

import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class NewRequestHelpActivity extends AppCompatActivity implements LocationListener {
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
    LocationManager locationManager;


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


        mHelpRequestName = (EditText) findViewById(R.id.new_request_name);
        mRequestDescription = (EditText) findViewById(R.id.new_request_description);
        Button choseMap = (Button) findViewById(R.id.new_help_request_position_label);

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

                /**
                 * Takes User current location and makes it HelpRequestLocation
                 */

                if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions((Activity) mContext, new String[]
                            {ACCESS_FINE_LOCATION}, REQUEST_CODE);
                } else {

                    getLocation();



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

    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3, NewRequestHelpActivity.this);
        } catch (Exception e){
            e.printStackTrace();
        }

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


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: Latitude and longitude is:   " + location.getLatitude() + " AND " + location.getLongitude());

        try {
            Context context;
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String addres = addressList.get(0).getAddressLine(0);

            Log.d(TAG, "onLocationChanged: Address is:  " + addres);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
