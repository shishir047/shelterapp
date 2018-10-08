package com.example.deepikatripathi.shelter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.deepikatripathi.shelter.data.PetsContract;

/**
 * Created by deepika tripathi on 7/26/2018.
 */

public class PetCursorAdapter extends CursorAdapter {

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView nameTextView =(TextView)view.findViewById(R.id.name);
        TextView breedTextView =(TextView)view.findViewById(R.id.summary);

        int nameColIndex=cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_NAME);
        int breedColIndex=cursor.getColumnIndex(PetsContract.PetsEntry.COLUMN_PET_BREED);

        String petname=cursor.getString(nameColIndex);
        String petbreed=cursor.getString(breedColIndex);

        nameTextView.setText(petname);
        breedTextView.setText(petbreed);
    }
}
