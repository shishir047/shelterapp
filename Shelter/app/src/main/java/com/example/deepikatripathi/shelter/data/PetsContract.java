package com.example.deepikatripathi.shelter.data;

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

/**
 * Created by deepika tripathi on 4/8/2018.
 */

public final class PetsContract {

    public static final class PetsEntry implements BaseColumns{

        private PetsEntry(){}

        public final static String Table_Name ="Pets";

        public final static String _ID =BaseColumns._ID;
        public final static String COLUMN_PET_NAME ="name";
        public final static String COLUMN_PET_BREED ="breed";
        public final static String COLUMN_PET_GENDER="gender";
        public final static String COLUMN_PET_WEIGHT ="weight";

        /* for constants */
        public final static int GENDER_UNKNOWN=0 ;
        public final static int GENDER_MALE=1;
        public final static int GENDER_FEMALE=2;

        //for PetProvider class
        public final static String CONTENT_AUTHORITY="com.example.deepikatripathi.shelter";
        public final static Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
        public final static String PATH_PETS ="Pets";

        public static final Uri CONTENT_URI =Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PETS);


        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;}


        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of pets.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;


    }
}
