package com.nelolik.pocketvocabulary.Utility;

import android.provider.BaseColumns;

public class TranslationsDBContract implements BaseColumns {
    public static final String TABLE_NAME = "translations";
    public static final String COLUMN_ORIGIN = "origin";
    public static final String COLUMN_TRANSLATION = "translation";
    public static final String COLUMN_DATE = "date";
    public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + _ID + " INTEGER PRIMARY KEY, " +
            COLUMN_ORIGIN + " TEXT NOT NULL, " +
            COLUMN_TRANSLATION + " TEXT NOT NULL, " +
            COLUMN_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
