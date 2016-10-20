package com.gmail.safarov.umid.wcards.data.source;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.gmail.safarov.umid.wcards.data.models.Pack;
import com.gmail.safarov.umid.wcards.data.models.Word;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class DataSource {

    public static final int WORDS_FLAG_ALL = 1;
    public static final int WORDS_FLAG_TRANSLATED = 2;
    public static final int WORDS_FLAG_UNTRANSLATED = 4;
    public static final int WORDS_FLAG_DRAWN = 8;
    public static final int WORDS_FLAG_UNDRAWN = 16;

    @IntDef(flag = true, value = {
            WORDS_FLAG_ALL,
            WORDS_FLAG_TRANSLATED,
            WORDS_FLAG_UNTRANSLATED,
            WORDS_FLAG_DRAWN,
            WORDS_FLAG_UNDRAWN
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface WordFlags {
    }


    private static DataSource _instance;

    @NonNull
    public static DataSource getInstance(@NonNull Context context) {
        if (_instance == null) {
            _instance = new DataSource(context);
        }
        return _instance;
    }

    //---------instance
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDatabase;
    // counts active connections
    private int mConnectionsCount;

    public DataSource(@NonNull Context context) {
        mDbHelper = new DatabaseHelper(context);
        mConnectionsCount = 0;
    }

    @NonNull
    private SQLiteDatabase getDatabase() {
        //active connections count 0 means database closed
        if (mConnectionsCount == 0)
            mDatabase = mDbHelper.getWritableDatabase();
        mConnectionsCount++;
        return mDatabase;
    }

    private void closeDatabase() {
        //close database if there is no active connections
        mConnectionsCount--;
        if (mConnectionsCount == 0)
            mDatabase.close();
    }

    //-------------packs

    /**
     * Returns list of Packs
     *
     * @param activePacks set true if you need to get active packs only. false otherwise
     */
    @NonNull
    public List<Pack> getPacks(boolean activePacks) {
        List<Pack> packs = new ArrayList<Pack>();
        SQLiteDatabase db = getDatabase();

        String[] projection = {
                DatabaseHelper.COLUMN_PACKS_ID,
                DatabaseHelper.COLUMN_PACKS_NAME,
                DatabaseHelper.COLUMN_PACKS_TEXT,
                DatabaseHelper.COLUMN_PACKS_PAGE_INDEX,
                DatabaseHelper.COLUMN_PACKS_IS_COMPLETED
        };

        Cursor c = db.query(DatabaseHelper.TABLE_PACKS, projection, DatabaseHelper.COLUMN_PACKS_IS_COMPLETED + " = ?", new String[]{activePacks ? "0" : "1"}, null, null, null);

        Pack pack;
        if (c != null) {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    pack = new Pack(c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PACKS_ID)), c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PACKS_NAME)),
                            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PACKS_TEXT)), c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PACKS_PAGE_INDEX)),
                            c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PACKS_IS_COMPLETED)) == 1);
                    packs.add(pack);
                }
            }
            c.close();
        }

        closeDatabase();
        return packs;
    }

    public Pack getPack(long id) {
        SQLiteDatabase db = getDatabase();

        String[] projection = {
                DatabaseHelper.COLUMN_PACKS_NAME,
                DatabaseHelper.COLUMN_PACKS_TEXT,
                DatabaseHelper.COLUMN_PACKS_PAGE_INDEX,
                DatabaseHelper.COLUMN_PACKS_IS_COMPLETED
        };

        Cursor c = db.query(
                DatabaseHelper.TABLE_PACKS, projection, DatabaseHelper.COLUMN_PACKS_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        Pack pack = null;

        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                pack = new Pack(id, c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PACKS_NAME)), c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PACKS_TEXT)),
                        c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PACKS_PAGE_INDEX)), c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PACKS_IS_COMPLETED)) == 1);
            }
            c.close();
        }

        closeDatabase();

        return pack;
    }

    /**
     * Saves Pack to database
     *
     * @param pack Pack to save
     * @return ID of row created
     */
    public long createPack(@NonNull Pack pack) {
        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PACKS_NAME, pack.getName());
        values.put(DatabaseHelper.COLUMN_PACKS_TEXT, pack.getText());
        values.put(DatabaseHelper.COLUMN_PACKS_PAGE_INDEX, pack.getPageIndex());
        values.put(DatabaseHelper.COLUMN_PACKS_IS_COMPLETED, pack.isComplete() ? 1 : 0);

        long result = db.insert(DatabaseHelper.TABLE_PACKS, null, values);

        closeDatabase();

        return result;
    }

    public void setPackName(long id, @NonNull String name) {
        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PACKS_NAME, name);

        db.update(DatabaseHelper.TABLE_PACKS, values, DatabaseHelper.COLUMN_PACKS_ID + " = ?", new String[]{String.valueOf(id)});

        closeDatabase();
    }

    public void setPackPageIndex(long id, int pageIndex) {
        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PACKS_PAGE_INDEX, pageIndex);

        db.update(DatabaseHelper.TABLE_PACKS, values, DatabaseHelper.COLUMN_PACKS_ID + " = ?", new String[]{String.valueOf(id)});

        closeDatabase();
    }

    public void setPackIsComplete(long id, boolean value) {
        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_PACKS_IS_COMPLETED, value);

        db.update(DatabaseHelper.TABLE_PACKS, values, DatabaseHelper.COLUMN_PACKS_ID + " = ?", new String[]{String.valueOf(id)});

        closeDatabase();
    }

    public void deletePack(long id) {
        SQLiteDatabase db = getDatabase();

        db.delete(DatabaseHelper.TABLE_WORDS, DatabaseHelper.COLUMN_WORDS_PACK_ID + " = ?", new String[]{String.valueOf(id)});
        db.delete(DatabaseHelper.TABLE_PACKS, DatabaseHelper.COLUMN_PACKS_ID + " = ?", new String[]{String.valueOf(id)});

        closeDatabase();
    }

    //-------------words
    @NonNull
    public List<Word> getWords(long packId) {
        return getWords(packId, WORDS_FLAG_ALL);
    }

    @NonNull
    public List<Word> getWords(long packId, @WordFlags int flags) {
        List<Word> words = new ArrayList<Word>();
        SQLiteDatabase db = getDatabase();

        String[] projection = {
                DatabaseHelper.COLUMN_WORDS_ID,
                DatabaseHelper.COLUMN_WORDS_EN_TEXT,
                DatabaseHelper.COLUMN_WORDS_RU_TEXT,
                DatabaseHelper.COLUMN_WORDS_EN_CANVAS_FILE_NAME,
                DatabaseHelper.COLUMN_WORDS_RU_CANVAS_FILE_NAME,
                DatabaseHelper.COLUMN_WORDS_VOICE_FILE_NAME
        };

        String whereClause = DatabaseHelper.COLUMN_WORDS_PACK_ID + " = ?";

        if ((flags & WORDS_FLAG_TRANSLATED) == WORDS_FLAG_TRANSLATED)
            whereClause = whereClause.concat(" and not " + DatabaseHelper.COLUMN_WORDS_RU_TEXT + " = ''");
        else if ((flags & WORDS_FLAG_UNTRANSLATED) == WORDS_FLAG_UNTRANSLATED)
            whereClause = whereClause.concat(" and " + DatabaseHelper.COLUMN_WORDS_RU_TEXT + " = ''");
        if ((flags & WORDS_FLAG_DRAWN) == WORDS_FLAG_DRAWN)
            whereClause = whereClause.concat(" and not " + DatabaseHelper.COLUMN_WORDS_EN_CANVAS_FILE_NAME + " = ''");
        else if ((flags & WORDS_FLAG_UNDRAWN) == WORDS_FLAG_UNDRAWN)
            whereClause = whereClause.concat(" and " + DatabaseHelper.COLUMN_WORDS_EN_CANVAS_FILE_NAME + " = ''");

        Cursor c = db.query(DatabaseHelper.TABLE_WORDS, projection, whereClause, new String[]{String.valueOf(packId)}, null, null, null);

        Word word;
        if (c != null) {
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    word = new Word(c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PACKS_ID)), packId,
                            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORDS_EN_TEXT)), c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORDS_RU_TEXT)),
                            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORDS_EN_CANVAS_FILE_NAME)), c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORDS_RU_CANVAS_FILE_NAME)),
                            c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORDS_VOICE_FILE_NAME)));
                    words.add(word);
                }
            }
            c.close();
        }

        closeDatabase();
        return words;
    }

    public Word getWord(long id) {
        SQLiteDatabase db = getDatabase();

        String[] projection = {
                DatabaseHelper.COLUMN_WORDS_PACK_ID,
                DatabaseHelper.COLUMN_WORDS_EN_TEXT,
                DatabaseHelper.COLUMN_WORDS_RU_TEXT,
                DatabaseHelper.COLUMN_WORDS_EN_CANVAS_FILE_NAME,
                DatabaseHelper.COLUMN_WORDS_RU_CANVAS_FILE_NAME,
                DatabaseHelper.COLUMN_WORDS_VOICE_FILE_NAME
        };

        Cursor c = db.query(
                DatabaseHelper.TABLE_WORDS, projection, DatabaseHelper.COLUMN_WORDS_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);

        Word word = null;

        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                word = new Word(id, c.getLong(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORDS_PACK_ID)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORDS_EN_TEXT)), c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORDS_RU_TEXT)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORDS_EN_CANVAS_FILE_NAME)), c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORDS_RU_CANVAS_FILE_NAME)),
                        c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORDS_VOICE_FILE_NAME)));
            }
            c.close();
        }

        closeDatabase();

        return word;
    }

    /**
     * Saves Word to database
     *
     * @param word Word to save
     * @return ID of row created
     */
    public long createWord(@NonNull Word word) {
        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_WORDS_PACK_ID, word.getPackId());
        values.put(DatabaseHelper.COLUMN_WORDS_EN_TEXT, word.getEnText());
        values.put(DatabaseHelper.COLUMN_WORDS_RU_TEXT, word.getRuText());
        values.put(DatabaseHelper.COLUMN_WORDS_EN_CANVAS_FILE_NAME, word.getEnCanvasFileName());
        values.put(DatabaseHelper.COLUMN_WORDS_RU_CANVAS_FILE_NAME, word.getRuCanvasFileName());
        values.put(DatabaseHelper.COLUMN_WORDS_VOICE_FILE_NAME, word.getVoiceFileName());

        long result = db.insert(DatabaseHelper.TABLE_WORDS, null, values);

        closeDatabase();

        return result;
    }

    /**
     * Sets new value of the word and clears voice file name
     */
    public void replaceWord(long id, @NonNull String enText) {
        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_WORDS_EN_TEXT, enText);
        values.put(DatabaseHelper.COLUMN_WORDS_VOICE_FILE_NAME, "");

        db.update(DatabaseHelper.TABLE_WORDS, values, DatabaseHelper.COLUMN_PACKS_ID + " = ?", new String[]{String.valueOf(id)});

        closeDatabase();
    }

    public void setWordRuText(long id, @NonNull String value) {
        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_WORDS_RU_TEXT, value);

        db.update(DatabaseHelper.TABLE_WORDS, values, DatabaseHelper.COLUMN_PACKS_ID + " = ?", new String[]{String.valueOf(id)});

        closeDatabase();
    }

    public void setWordEnCanvasFileName(long id, @NonNull String value) {
        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_WORDS_EN_CANVAS_FILE_NAME, value);

        db.update(DatabaseHelper.TABLE_WORDS, values, DatabaseHelper.COLUMN_PACKS_ID + " = ?", new String[]{String.valueOf(id)});

        closeDatabase();
    }

    public void setWordRuCanvasFileName(long id, @NonNull String value) {
        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_WORDS_RU_CANVAS_FILE_NAME, value);

        db.update(DatabaseHelper.TABLE_WORDS, values, DatabaseHelper.COLUMN_PACKS_ID + " = ?", new String[]{String.valueOf(id)});

        closeDatabase();
    }

    public void setWordVoiceFileName(long id, @NonNull String value) {
        SQLiteDatabase db = getDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_WORDS_VOICE_FILE_NAME, value);

        db.update(DatabaseHelper.TABLE_WORDS, values, DatabaseHelper.COLUMN_PACKS_ID + " = ?", new String[]{String.valueOf(id)});

        closeDatabase();
    }

    public void deleteWord(long id) {
        SQLiteDatabase db = getDatabase();

        db.delete(DatabaseHelper.TABLE_WORDS, DatabaseHelper.COLUMN_WORDS_ID + " = ?", new String[]{String.valueOf(id)});

        closeDatabase();
    }

    /**
     * Deletes all words match to the given params
     *
     * @return returns true if request affected some words
     */
    public boolean deleteWord(long packId, @NonNull String word) {
        SQLiteDatabase db = getDatabase();

        int result = db.delete(DatabaseHelper.TABLE_WORDS, DatabaseHelper.COLUMN_WORDS_PACK_ID + " = ? and " +
                        DatabaseHelper.COLUMN_WORDS_EN_TEXT + " = ? and " + DatabaseHelper.COLUMN_WORDS_VOICE_FILE_NAME +
                        " = '' and " + DatabaseHelper.COLUMN_WORDS_EN_CANVAS_FILE_NAME + " = '' and " + DatabaseHelper.COLUMN_WORDS_RU_CANVAS_FILE_NAME + " = ''",
                new String[]{String.valueOf(packId), word});

        closeDatabase();

        return result > 0;
    }

    //-------------------fields
    public int getPacksCount(boolean activePacks) {

        SQLiteDatabase db = getDatabase();

        Cursor c = db.rawQuery("select count(*) from " + DatabaseHelper.TABLE_PACKS + " where " +
                        DatabaseHelper.COLUMN_PACKS_IS_COMPLETED + " = ?",
                new String[]{String.valueOf(activePacks ? "0" : "1")});

        int result = 0;

        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                result = c.getInt(0);
            }
            c.close();
        }

        closeDatabase();

        return result;

    }

    public int getWordsCount(long packId) {
        return getWordsCount(packId, WORDS_FLAG_ALL);
    }

    public int getWordsCount(long packId, @WordFlags int flags) {
        SQLiteDatabase db = getDatabase();

        String whereCluse = DatabaseHelper.COLUMN_WORDS_PACK_ID + " = ?";

        if ((flags & WORDS_FLAG_TRANSLATED) == WORDS_FLAG_TRANSLATED)
            whereCluse = whereCluse.concat(" and not " + DatabaseHelper.COLUMN_WORDS_RU_TEXT + " = ''");
        else if ((flags & WORDS_FLAG_UNTRANSLATED) == WORDS_FLAG_UNTRANSLATED)
            whereCluse = whereCluse.concat(" and " + DatabaseHelper.COLUMN_WORDS_RU_TEXT + " = ''");
        if ((flags & WORDS_FLAG_DRAWN) == WORDS_FLAG_DRAWN)
            whereCluse = whereCluse.concat(" and not " + DatabaseHelper.COLUMN_WORDS_EN_CANVAS_FILE_NAME + " = ''");
        else if ((flags & WORDS_FLAG_UNDRAWN) == WORDS_FLAG_UNDRAWN)
            whereCluse = whereCluse.concat(" and " + DatabaseHelper.COLUMN_WORDS_EN_CANVAS_FILE_NAME + " = ''");

        Cursor c = db.rawQuery("select count(*) from " + DatabaseHelper.TABLE_WORDS + " where " + whereCluse, new String[]{String.valueOf(packId)});

        int result = 0;

        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                result = c.getInt(0);
            }
            c.close();
        }

        closeDatabase();

        return result;
    }

    public boolean isWordExist(long packId, @NonNull String word) {
        SQLiteDatabase db = getDatabase();

        Cursor c = db.rawQuery("select " + DatabaseHelper.COLUMN_WORDS_ID + " from " + DatabaseHelper.TABLE_WORDS + " where " +
                        DatabaseHelper.COLUMN_WORDS_PACK_ID + " = ? and " + DatabaseHelper.COLUMN_WORDS_EN_TEXT + " = ?",
                new String[]{String.valueOf(packId), word});

        boolean result = false;

        if (c != null) {
            result = c.getCount() > 0;
            c.close();
        }

        closeDatabase();

        return result;
    }
}
