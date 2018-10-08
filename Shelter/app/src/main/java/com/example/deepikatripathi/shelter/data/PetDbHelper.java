package com.example.deepikatripathi.shelter.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.deepikatripathi.shelter.R;

public class PetDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = PetDbHelper.class.getSimpleName();
    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="PetDbHelper.db";


    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_PETS_TABLE = "CREATE TABLE "+ PetsContract.PetsEntry.Table_Name + "(" + PetsContract.PetsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT " +","+
                PetsContract.PetsEntry.COLUMN_PET_NAME+" TEXT NOT NULL " +","+ PetsContract.PetsEntry.COLUMN_PET_BREED + " TEXT "+","+ PetsContract.PetsEntry.COLUMN_PET_GENDER+" INTEGER NOT NULL "+","+
                PetsContract.PetsEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);"  ;

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

       String SQL_DELETE_PETS_TABLE= "DELETE TABLE " + PetsContract.PetsEntry.Table_Name;


      db.execSQL(SQL_DELETE_PETS_TABLE);
        onCreate(db);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        onUpgrade(db,oldVersion,newVersion);
    }

}
