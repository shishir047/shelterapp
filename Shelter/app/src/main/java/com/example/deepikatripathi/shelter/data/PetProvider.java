package com.example.deepikatripathi.shelter.data;

/**
 * Created by deepika tripathi on 7/19/2018.
 */
import android.util.Log;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.UriMatcher;
import android.database.Cursor;
import android.content.ContentResolver;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.deepikatripathi.shelter.R;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.JoiningGroup.PE;
import static com.example.deepikatripathi.shelter.data.PetsContract.PetsEntry.CONTENT_URI;
import static com.example.deepikatripathi.shelter.data.PetsContract.PetsEntry.PATH_PETS;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    private PetDbHelper mDbHelper;

    //adding codes to constants for using in uri's
        private static final int PETS=100;
        private static final int PETS_ID=101;

    private static UriMatcher sUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    //adding uri matcher
    //this will tell the type of uri's our app can handle
    //first one is for whole table
    //second one is for a specified row
    static{
        sUriMatcher.addURI(PetsContract.PetsEntry.CONTENT_AUTHORITY,PATH_PETS,PETS);
        sUriMatcher.addURI(PetsContract.PetsEntry.CONTENT_AUTHORITY,PATH_PETS+"/#",PETS_ID);
    }
    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
      mDbHelper=new PetDbHelper(getContext());
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch (match) {

            case PETS://single table
                cursor=database.query(PetsContract.PetsEntry.Table_Name,projection,selection,selectionArgs,null,null,null);
                break;
            case PETS_ID://in a specified row
                selection = PetsContract.PetsEntry._ID +"=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(PetsContract.PetsEntry.Table_Name, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("can not query unknown URI" + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
                //setting notifification for cursor loader
              cursor.setNotificationUri(getContext().getContentResolver(), uri);

                return cursor;
        }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }


    }

    private Uri insertPet(Uri uri, ContentValues values) {

        //checking data if valid data is inserted

        //we would suggest that name can not be null
        String name = values.getAsString(PetsContract.PetsEntry.COLUMN_PET_NAME);
        if(name == null) {
            throw new IllegalArgumentException("Pet requires a name");}
        //breed can be null so no validation for that
        //next is gender is valid
        Integer gender = values.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_GENDER);
        if (gender == null || !PetsContract.PetsEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires valid gender");
        }
        //checking for weight is valid
        Integer weight = values.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_WEIGHT);
        if (weight != null && weight < 0) {
            throw new IllegalArgumentException("Pet requires valid weight");
        }

       // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();



            // Insert the new pet with the given values
            int id = (int) database.insert(PetsContract.PetsEntry.Table_Name, null, values);
            // If the ID is -1, then the insertion failed. Log an error and return null.

            if (id == -1) {
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
                return null;
            }


        // Notify all listeners that the data has changed for the pet content URI
        getContext().getContentResolver().notifyChange(uri,null);

        //this will append the id of new row with the uri
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PETS_ID:
                selection = PetsContract.PetsEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);


        }


    }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_PET_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(PetsContract.PetsEntry.COLUMN_PET_NAME)) {
            String name = values.getAsString(PetsContract.PetsEntry.COLUMN_PET_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_GENDER} key is present,
        // check that the gender value is valid.
        if (values.containsKey( PetsContract.PetsEntry.COLUMN_PET_GENDER)) {
            Integer gender = values.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_GENDER);
            if (gender == null || !PetsContract.PetsEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires valid gender");
            }
        }

        // If the {@link PetEntry#COLUMN_PET_WEIGHT} key is present,
        // check that the weight value is valid.
        if (values.containsKey(PetsContract.PetsEntry.COLUMN_PET_WEIGHT)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer weight = values.getAsInteger(PetsContract.PetsEntry.COLUMN_PET_WEIGHT);
            if (weight != null && weight < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        // No need to check the breed, any value is valid (including null).

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase db=mDbHelper.getWritableDatabase();

        int numOfRows = db.update(PetsContract.PetsEntry.Table_Name,values,selection,selectionArgs);

        if (numOfRows != 0) {
                      getContext().getContentResolver().notifyChange(uri, null);
        }

        return numOfRows;
    }



    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                 rowsDeleted=database.delete(PetsContract.PetsEntry.Table_Name, selection, selectionArgs);
                break;
            case PETS_ID:
                // Delete a single row given by the ID in the URI
                selection = PetsContract.PetsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted= database.delete(PetsContract.PetsEntry.Table_Name, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
                       getContext().getContentResolver().notifyChange(uri, null);
                   }

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri){
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetsContract.PetsEntry.CONTENT_LIST_TYPE;
            case PETS_ID:
                return PetsContract.PetsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}