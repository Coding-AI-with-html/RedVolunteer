package com.redvolunteer.utils.persistence.sqlitelocalpersistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;
import com.redvolunteer.utils.persistence.LocalRequestDao;

import java.util.ArrayList;
import java.util.List;

public class LocalSQLiteRequestDao implements LocalRequestDao {


    /**
     * save application context to access the sql helper
     */
    private Context appContext;

    /**
     * Current DB version infos
     */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RequestStore.db";


    /**
     * Creation command for the table
     */
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " +RequestEntry.TABLE_NAME + "(" +
                    RequestEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    RequestEntry.COLUMN_REQUEST_NAME + "TEXT," +
                    RequestEntry.COLUMN_DESCRIPTION + "TEXT," +
                    RequestEntry.COLUMN_LONGITUDE + "REAL," +
                    RequestEntry.COLUMN_LATITUDE + "REAL," +
                    RequestEntry.COLUMN_PLACE_NAME + "TEXT)";

    /**
     * DEletion command for the whole store
     */
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + RequestEntry.TABLE_NAME;

    /**
     * Columns required to retrieve the request
     */
    private static final String[] REQUEST_PROJECTION = {
            RequestEntry._ID,
            RequestEntry.COLUMN_REQUEST_NAME,
            RequestEntry.COLUMN_DESCRIPTION,
            RequestEntry.COLUMN_LONGITUDE,
            RequestEntry.COLUMN_LATITUDE,
            RequestEntry.COLUMN_PLACE_NAME
    };
    public LocalSQLiteRequestDao(Context context){
        this.appContext = context;
    }
    @Override
    public void wipe() {

        SQLiteHelper helper = new SQLiteHelper(appContext);

        //retrieve wrapped database;
        SQLiteDatabase localDb = helper.getWritableDatabase();

        localDb.execSQL(SQL_DELETE_ENTRIES);
        localDb.execSQL(SQL_CREATE_ENTRIES);

        helper.close();


    }

    @Override
    public List<RequestHelp> getRequest(int numResults, int startOffest) {

        SQLiteHelper helper = new SQLiteHelper(appContext);

        int endquery = startOffest + numResults;
        String limit = startOffest + "," + endquery;

        List<RequestHelp> requests = new ArrayList<>();

        Cursor cursor = helper.getReadableDatabase().query(
                RequestEntry.TABLE_NAME,       //Table name to query
                REQUEST_PROJECTION,            // the columns to return
                null,                 //The columns for the WHer clause
                null,              //The values for the WHERE clause
                null,                  // don't group the rows
                null,                   //don't filter by row groups
                null,                  //the sort order
                limit
        );

        while(cursor.moveToNext()){

            RequestHelp retrieved = this.retrieveRequest(cursor);
            requests.add(retrieved);

        }
        cursor.close();
        helper.close();

        return requests;
    }

    @Override
    public RequestHelp save(RequestHelp requestHelpToStore) {

        SQLiteHelper helper = new SQLiteHelper(appContext);

        ContentValues valueToStore = new ContentValues();
        valueToStore.put(RequestEntry.COLUMN_REQUEST_NAME, requestHelpToStore.getName());
        valueToStore.put(RequestEntry.COLUMN_DESCRIPTION, requestHelpToStore.getDescription());
        valueToStore.put(RequestEntry.COLUMN_LONGITUDE, requestHelpToStore.getRequestLocation().getLongitude());
        valueToStore.put(RequestEntry.COLUMN_LATITUDE, requestHelpToStore.getRequestLocation().getLatitude());
        valueToStore.put(RequestEntry.COLUMN_PLACE_NAME, requestHelpToStore.getRequestLocation().getName());


        if(requestHelpToStore.getId() != null) {
            helper.getWritableDatabase().update(
                    RequestEntry.TABLE_NAME,
                    valueToStore,
                    RequestEntry._ID + "=" + requestHelpToStore.getId(),
                    null);
        } else {

            long newId = helper.getWritableDatabase().insert(RequestEntry.TABLE_NAME, null, valueToStore);


            requestHelpToStore.setId(String.valueOf(newId));
            }

        helper.close();

        return requestHelpToStore;
    }

    @Override
    public RequestHelp LoadRequestById(String requestID) {

        RequestHelp retrievedRequestHelps = null;

        SQLiteHelper helper = new SQLiteHelper(appContext);

        //Filter results WHere title = my title
        String selection = RequestEntry._ID + " = ?";
        String[] selectionArgs = {requestID};


        Cursor cursor = helper.getReadableDatabase().query(
                RequestEntry.TABLE_NAME,
                REQUEST_PROJECTION,
                selection,
                selectionArgs,
                null,
                null,
                null
        );


        //go to the first element of the cursor

        cursor.moveToFirst();

        retrievedRequestHelps = this.retrieveRequest(cursor);

        //free memory space
        cursor.close();
        helper.close();

        return retrievedRequestHelps;
    }

    private RequestHelp retrieveRequest(Cursor cursor){

        RequestHelp retrievedRequests = new RequestHelp();

        //indexes from column name
        int idC = cursor.getColumnIndex(RequestEntry._ID);
        int nameC = cursor.getColumnIndex(RequestEntry.COLUMN_REQUEST_NAME);
        int descrC = cursor.getColumnIndex(RequestEntry.COLUMN_DESCRIPTION);
        int lonC = cursor.getColumnIndex(RequestEntry.COLUMN_LONGITUDE);
        int latC = cursor.getColumnIndex(RequestEntry.COLUMN_LATITUDE);
        int placeNameC = cursor.getColumnIndex(RequestEntry.COLUMN_PLACE_NAME);


        //assign each value to the appropriate attribute
        retrievedRequests.setId(cursor.getString(idC));
        retrievedRequests.setName(cursor.getString(nameC));
        retrievedRequests.setDescription(cursor.getString(descrC));

        // set Request Location

        RequestLocation location = new RequestLocation();
        location.setLatitude(cursor.getDouble(latC));
        location.setLongitude(cursor.getDouble(lonC));
        location.setName(cursor.getString(placeNameC));
        retrievedRequests.setRequestLocation(location);

        return retrievedRequests;


    }





    public static class RequestEntry implements BaseColumns{

        /**
         * Table
         */
        static final String TABLE_NAME = "request_table";

        /**
         * Columns
         */
        static final String COLUMN_REQUEST_NAME = "name";
        static final String COLUMN_DESCRIPTION = "description";
        static final String COLUMN_LONGITUDE = "longitude";
        static final String COLUMN_LATITUDE = "latitude";
        static final String COLUMN_PLACE_NAME = "place";

    }
    private class SQLiteHelper extends SQLiteOpenHelper{

        public SQLiteHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

            onUpgrade(sqLiteDatabase, oldVersion, newVersion);
        }
    }

}
