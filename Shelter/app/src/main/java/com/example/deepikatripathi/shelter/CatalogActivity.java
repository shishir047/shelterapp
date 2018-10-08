package com.example.deepikatripathi.shelter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.deepikatripathi.shelter.data.PetsContract;

import static android.R.attr.data;
import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class CatalogActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PET_LOADER=0;
     PetCursorAdapter pcd;
     ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        //display in list
        lv=(ListView)findViewById(R.id.listview);

        //show empty_view when no data
        View emptyView = findViewById(R.id.empty_view);
        lv.setEmptyView(emptyView);

        pcd =new PetCursorAdapter(this,null);
        lv.setAdapter(pcd);

        //adding intent for edit text
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(CatalogActivity.this, EditorActivity.class);
                //it takes the id of that pet which is selected and appened it with our uri
                Uri contUri = ContentUris.withAppendedId(PetsContract.PetsEntry.CONTENT_URI,id);
                i.setData(contUri);
                startActivity(i);
            }
        });

        //cursor loader
        getLoaderManager().initLoader(PET_LOADER, null,this);
    }


    //to display menu at the icon bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    //thsi method will insert a row in the database table whose content i have given in the method .
    //this method is invoked by insert dummy data in the menu option.
    void insertData(){

        Uri mNewUri;

        //THIS WILL insert values in the database
        ContentValues values=new ContentValues();
        values.put(PetsContract.PetsEntry.COLUMN_PET_NAME,"Toto");
        values.put(PetsContract.PetsEntry.COLUMN_PET_BREED,"Terrier");
        values.put(PetsContract.PetsEntry.COLUMN_PET_GENDER, PetsContract.PetsEntry.GENDER_MALE);
        values.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT,"10");

        mNewUri=getContentResolver().insert(PetsContract.PetsEntry.CONTENT_URI,values);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertData();

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = new String[]
                {PetsContract.PetsEntry._ID, PetsContract.PetsEntry.COLUMN_PET_NAME, PetsContract.PetsEntry.COLUMN_PET_BREED,PetsContract.PetsEntry.COLUMN_PET_GENDER, PetsContract.PetsEntry.COLUMN_PET_WEIGHT};


        return new android.content.CursorLoader(this, PetsContract.PetsEntry.CONTENT_URI, projection, null, null, null);

    }


    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        pcd.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer using it.

        pcd.swapCursor(null);
    }

}
