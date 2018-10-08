package com.example.deepikatripathi.shelter;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.deepikatripathi.shelter.data.PetsContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Allows user to create a new pet or edit an existing one.
     */

        /** EditText field to enter the pet's name */
        private EditText mNameEditText;

        /** EditText field to enter the pet's breed */
        private EditText mBreedEditText;

        /** EditText field to enter the pet's weight */
        private EditText mWeightEditText;

        /** EditText field to enter the pet's gender */
        private Spinner mGenderSpinner;

    private static final int PET_LOADER1=0;

        /**
         * Gender of the pet. The possible values are:
         * 0 for unknown gender, 1 for male, 2 for female.
         */
        private int mGender = 0;

        Uri contUri;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_editor);

            //taking the intent for edit text
            Intent i=getIntent();
            contUri=i.getData();
            if(contUri==null)
                setTitle(getString(R.string.editor_activity_title_new_pet));
            else
                setTitle("Edit Pet");

            //cursor loader
            getLoaderManager().initLoader(PET_LOADER1, null,this);

            // Find all relevant views that we will need to read user input from
            mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
            mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
            mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
            mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

            setupSpinner();
        }

        /**
         * Setup the dropdown spinner that allows the user to select the gender of the pet.
         */
        private void setupSpinner() {
            // Create adapter for spinner. The list options are from the String array it will use
            // the spinner will use the default layout
            ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                    R.array.array_gender_options, android.R.layout.simple_spinner_item);

            // Specify dropdown layout style - simple list view with 1 item per line
            genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

            // Apply the adapter to the spinner
            mGenderSpinner.setAdapter(genderSpinnerAdapter);

            // Set the integer mSelected to the constant values
            mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals(getString(R.string.gender_male))) {
                            mGender = PetsContract.PetsEntry.GENDER_MALE; // Male
                        } else if (selection.equals(getString(R.string.gender_female))) {
                            mGender = PetsContract.PetsEntry.GENDER_FEMALE; // Female
                        } else {
                            mGender = PetsContract.PetsEntry.GENDER_UNKNOWN; // Unknown
                        }
                    }
                }

                // Because AdapterView is an abstract class, onNothingSelected must be defined
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    mGender = 0; // Unknown
                }
            });
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu options from the res/menu/menu_editor.xml file.
            // This adds menu items to the app bar.
            getMenuInflater().inflate(R.menu.menu_editor, menu);
            return true;
        }

        //to save data in database
        void saveData() {
            String s1 = mNameEditText.getText().toString().trim();
            String s = mBreedEditText.getText().toString().trim();
            String s2 = mWeightEditText.getText().toString().trim();
            int weight = Integer.parseInt(s2);

            //THIS WILL insert values in the database
            ContentValues values = new ContentValues();
            values.put(PetsContract.PetsEntry.COLUMN_PET_NAME, s1);
            values.put(PetsContract.PetsEntry.COLUMN_PET_BREED, s);
            values.put(PetsContract.PetsEntry.COLUMN_PET_GENDER, mGender);
            values.put(PetsContract.PetsEntry.COLUMN_PET_WEIGHT, weight);

            if (contUri == null){
                //it is a new pet
                Uri newUri = getContentResolver().insert(PetsContract.PetsEntry.CONTENT_URI, values);
                if (newUri == null) {
                    // If the new content URI is null, then there was an error with insertion.
                    Toast.makeText(this, getString(R.string.editor_insert_pet_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the insertion was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_insert_pet_successful),
                            Toast.LENGTH_SHORT).show();
                }
        }else {
                // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentPetUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver().update(contUri, values, null, null);
                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.editor_update_pet_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // User clicked on a menu option in the app bar overflow menu
            switch (item.getItemId()) {
                // Respond to a click on the "Save" menu option
                case R.id.action_save:
                    //it inserts data in a database
                 saveData();
                    //it means editor is closed and returned to previous activity
                    //NOW WE will goto catalog activity
                    finish();
                    return true;
                // Respond to a click on the "Delete" menu option
                case R.id.action_delete:
                    // Do nothing for now
                    return true;
                // Respond to a click on the "Up" arrow button in the app bar
                case android.R.id.home:
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //create and return a cursor that query data from single row

        String[] projection=new String[]
                {PetsContract.PetsEntry._ID, PetsContract.PetsEntry.COLUMN_PET_NAME, PetsContract.PetsEntry.COLUMN_PET_BREED, PetsContract.PetsEntry.COLUMN_PET_GENDER, PetsContract.PetsEntry.COLUMN_PET_WEIGHT};


        return new android.content.CursorLoader(this,contUri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            return;}
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (data.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_NAME);
            int breedColumnIndex = data.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_BREED);
            int genderColumnIndex = data.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = data.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_WEIGHT);

            // Extract out the value from the Cursor for the given column index
            String name = data.getString(nameColumnIndex);
            String breed = data.getString(breedColumnIndex);
            int gender = data.getInt(genderColumnIndex);
            int weight = data.getInt(weightColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(Integer.toString(weight));
            
            switch (gender) {
                case PetsContract.PetsEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetsContract.PetsEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0); // Select "Unknown" gender
    }
}
