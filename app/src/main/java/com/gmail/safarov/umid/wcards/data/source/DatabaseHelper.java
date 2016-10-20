package com.gmail.safarov.umid.wcards.data.source;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PACKS = "packs";
    public static final String COLUMN_PACKS_ID = "id";
    public static final String COLUMN_PACKS_NAME = "name";
    public static final String COLUMN_PACKS_TEXT = "text";
    public static final String COLUMN_PACKS_PAGE_INDEX = "page_index";
    public static final String COLUMN_PACKS_IS_COMPLETED = "is_completed";

    public static final String TABLE_WORDS = "cards";
    public static final String COLUMN_WORDS_ID = "id";
    public static final String COLUMN_WORDS_PACK_ID = "pack_id";
    public static final String COLUMN_WORDS_EN_TEXT = "en_text";
    public static final String COLUMN_WORDS_RU_TEXT = "ru_text";
    public static final String COLUMN_WORDS_EN_CANVAS_FILE_NAME = "en_canvas_uri";
    public static final String COLUMN_WORDS_RU_CANVAS_FILE_NAME = "ru_canvas_uri";
    public static final String COLUMN_WORDS_VOICE_FILE_NAME = "voice_uri";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        final String createPacksQuery = "create table " + TABLE_PACKS
                + "(" + COLUMN_PACKS_ID + " integer primary key autoincrement, "
                + COLUMN_PACKS_NAME + " text default '',"
                + COLUMN_PACKS_TEXT + " text default '',"
                + COLUMN_PACKS_PAGE_INDEX + " integer default 0,"
                + COLUMN_PACKS_IS_COMPLETED + " integer default 0);";
        database.execSQL(createPacksQuery);
        final String createWordsQuery = "create table " + TABLE_WORDS
                + "(" + COLUMN_WORDS_ID + " integer primary key autoincrement, "
                + COLUMN_WORDS_PACK_ID + " integer, "
                + COLUMN_WORDS_EN_TEXT + " text default '',"
                + COLUMN_WORDS_RU_TEXT + " text default '',"
                + COLUMN_WORDS_EN_CANVAS_FILE_NAME + " text default '',"
                + COLUMN_WORDS_RU_CANVAS_FILE_NAME + " text default '',"
                + COLUMN_WORDS_VOICE_FILE_NAME + " text default '');";
        database.execSQL(createWordsQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
