package com.redvolunteer.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkCheker {





    private static  final NetworkCheker ourInstance = new NetworkCheker();


    public static NetworkCheker getInstance() {
        return ourInstance;
    }

    private NetworkCheker(){

    }




    public boolean isNetworkAvailable(Context context){

        boolean isAvailable = false;

        // native connection info manager

        ConnectivityManager ConManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);



        if(ConManager != null){

            NetworkInfo NetwInfo = ConManager.getActiveNetworkInfo();

            if(NetwInfo != null && NetwInfo.isConnected()){

                // Network is present and connected;
                isAvailable = true;
            }
        }

        return isAvailable;
    }
}
