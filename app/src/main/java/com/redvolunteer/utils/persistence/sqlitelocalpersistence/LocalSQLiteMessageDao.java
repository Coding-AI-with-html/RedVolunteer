package com.redvolunteer.utils.persistence.sqlitelocalpersistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.redvolunteer.pojo.Chat;
import com.redvolunteer.pojo.RequestHelp;
import com.redvolunteer.pojo.RequestLocation;
import com.redvolunteer.utils.LocalMessageDao;

import java.util.ArrayList;
import java.util.List;

public class LocalSQLiteMessageDao implements LocalMessageDao {

    /**
     * save application context to access the sql helper
     */
    private Context appContext;

    /**
     * Current DB version infos
     */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MessageStore.db";

    /**
     * Creation command for the table
     */
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LocalSQLiteMessageDao.MessageEntry.TABLE_NAME + "(" +
                    MessageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    MessageEntry.COLUMN_RECEIVER + "TEXT," +
                    MessageEntry.COLUMN_SENDER + "TEXT," +
                    MessageEntry.COLUMN_MESSAGE + "TEXT)";

    /**
     * DEletion command for the whole store
     */
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MessageEntry.TABLE_NAME;

    /**
     * Columns required to retrieve the request
     */
    private static final String[] MESSAGE_PROJECTION = {
            MessageEntry._ID,
            MessageEntry.COLUMN_RECEIVER,
            MessageEntry.COLUMN_SENDER,
            MessageEntry.COLUMN_MESSAGE

    };

    public LocalSQLiteMessageDao(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public void wipe() {

       SQLiteHelper helper = new SQLiteHelper(appContext);

        //retrieve wrapped database;
        SQLiteDatabase localDb = helper.getWritableDatabase();

        localDb.execSQL(SQL_DELETE_ENTRIES);
        localDb.execSQL(SQL_CREATE_ENTRIES);

        helper.close();    }

    @Override
    public List<Chat> getChat(int numResults, int startOffset) {

        SQLiteHelper helper = new SQLiteHelper(appContext);

        int endquery = startOffset + numResults;
        String limit = startOffset + "," + endquery;

        List<Chat> chats = new ArrayList<>();

        Cursor cursor = helper.getReadableDatabase().query(
                LocalSQLiteRequestDao.RequestEntry.TABLE_NAME,       //Table name to query
                MESSAGE_PROJECTION,            // the columns to return
                null,                 //The columns for the WHer clause
                null,              //The values for the WHERE clause
                null,                  // don't group the rows
                null,                   //don't filter by row groups
                null,                  //the sort order
                limit
        );

        while(cursor.moveToNext()){

            Chat retrieved = this.retrieveChat(cursor);
            chats.add(retrieved);

        }
        cursor.close();
        helper.close();

        return chats;
    }

    @Override
    public Chat save(Chat chatToSave) {

        SQLiteHelper helper = new SQLiteHelper(appContext);

        ContentValues valueToStore = new ContentValues();
        valueToStore.put(MessageEntry.COLUMN_RECEIVER, chatToSave.getReceiver());
        valueToStore.put(MessageEntry.COLUMN_SENDER, chatToSave.getSender());
        valueToStore.put(MessageEntry.COLUMN_MESSAGE, chatToSave.getMessage());


        return chatToSave;
    }



    private Chat retrieveChat(Cursor cursor){

        Chat retrievedChat = new Chat();

        //indexes from column name
        int receiverC = cursor.getColumnIndex(MessageEntry.COLUMN_RECEIVER);
        int senderC = cursor.getColumnIndex(MessageEntry.COLUMN_SENDER);
        int messageC = cursor.getColumnIndex(MessageEntry.COLUMN_MESSAGE);


        //assign each value to the appropriate attribute
        retrievedChat.setReceiver(cursor.getString(receiverC));
        retrievedChat.setSender(cursor.getString(senderC));
        retrievedChat.setMessage(cursor.getString(messageC));



        return retrievedChat;


    }


    public static class MessageEntry implements BaseColumns {
        /**
         * Table
         */
        static final String TABLE_NAME = "message_table";

        /**
         * Columns
         */
        static final String COLUMN_RECEIVER = "receiver";
        static final String COLUMN_SENDER = "sender";
        static final String COLUMN_MESSAGE = "message";

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
