package com.redvolunteer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class MapsActivity extends AppCompatActivity {

    Button btn_PickLocation;
    TextView tv_MyLocation;
    WifiManager wifiManager;

    private final static int PLACE_PICKER_REQUEST = 999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_activity);

        btn_PickLocation=(Button)findViewById(R.id.BtnPickLocation);
        tv_MyLocation=(TextView) findViewById(R.id.MyLocation);
        wifiManager= (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        btn_PickLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Disable Wifi
                wifiManager.setWifiEnabled(false);
                openPlacePicker();
            }
        });
    }

    private void openPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(MapsActivity.this, data);

                    double latitude = place.getLatLng().latitude;
                    double longitude = place.getLatLng().longitude;
                    String PlaceLatLng = String.valueOf(latitude)+" , "+String.valueOf(longitude);
                    tv_MyLocation.setText(PlaceLatLng);

            }
        }
    }


}
