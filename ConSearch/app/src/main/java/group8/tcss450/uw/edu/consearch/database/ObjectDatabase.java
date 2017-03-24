package group8.tcss450.uw.edu.consearch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import group8.tcss450.uw.edu.consearch.R;
import group8.tcss450.uw.edu.consearch.model.Artist;
import group8.tcss450.uw.edu.consearch.model.ConSearchObject;

/**
 * @author Team 8
 * @version 1.0
 * A database to store objects in Local SQLite.
 */
public class ObjectDatabase {

    private static final int DB_VERSION = 1;
    private final String ARTIST_TABLE;

    private SQLiteDatabase mSQLiteDatabase;

    private final String[] ARTIST_COLUMN_NAMES;

    /**
     * Creates the database associated with the context.
     * @param context the Accompanying Context.
     */
    public ObjectDatabase(Context context) {
        ARTIST_COLUMN_NAMES = context.getResources().getStringArray(R.array.ARTIST_DB_COLUMN_NAMES);

        String DB_NAME = context.getString(R.string.DB_NAME);
        ARTIST_TABLE = context.getString(R.string.ARTIST_TABLE_NAME);

        ObjectsDBHelper mObjectDBHelper = new ObjectsDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mObjectDBHelper.getWritableDatabase();
    }

    /**
     * Add the artist to SQLite
     * @param object the Artist Object
     * @return true if the artist was successfully added to SQLite.
     */
    public boolean addArtist(Artist object) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ARTIST_COLUMN_NAMES[0], object.getID());//id
        contentValues.put(ARTIST_COLUMN_NAMES[1], object.getName());//name

        long rowId = mSQLiteDatabase.insert(ARTIST_TABLE, null, contentValues);
        return rowId != -1;
    }

    /**
     * Checks if the Artist Exists in local database.
     * @param name the name of the artist.
     * @return true of the artist exist, false otherwise.
     */
    public boolean isArtistExist(String name)  {
        return getArtist(name) != null;
    }

    /**
     * Returns the artist with the name null of the artist doesn't exist.
     * @param name the name of the artist.
     * @return the Artist Object.
     */
    public Artist getArtist(String name) {
        String selectQuery = "SELECT  * FROM " + ARTIST_TABLE + " WHERE name = '" + name+"'";
        Artist artist = null;
        Cursor cursor = mSQLiteDatabase.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            String id =  cursor.getString(0);
            String artistName = cursor.getString(1);
            artist = new Artist(artistName, Integer.valueOf(id), false);
        }
        cursor.close();
        return artist;
    }

    /**
     * Retunrs all the artists in the local database.
     * @return an arraylist of all the artist in the Database.
     */
    public ArrayList<ConSearchObject> getAllArtists() {
        ArrayList<ConSearchObject> result = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + ARTIST_TABLE;
        Cursor cursor = mSQLiteDatabase.rawQuery(selectQuery, null);

        if ( cursor.moveToFirst()) {
            do {
                int id; String name;
                id = cursor.getInt(0);
                name = cursor.getString(1);
                result.add(new Artist(name, id, false));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }

    /**
     * Local SQliteOpenHelper
     */
    private class ObjectsDBHelper extends SQLiteOpenHelper {

        private final String CREATE_ARTIST_SQL;
        private final String CREATE_VENUE_SQL;

        private final String DROP_ARTIST_SQL;
        private final String DROP_VENUE_SQL;

        /**
         * Constructor initialized the ObjectDBHelper.
         * @param context the Associated context.
         * @param name the name of the database.
         * @param factory Cursorfactory
         * @param version DB version.
         */
        ObjectsDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            CREATE_ARTIST_SQL = context.getString(R.string.CREATE_ARTIST_SQL);
            CREATE_VENUE_SQL = context.getString(R.string.CREATE_VENUE_SQL);

            DROP_ARTIST_SQL = context.getString(R.string.DROP_ARTIST_SQL);
            DROP_VENUE_SQL = context.getString(R.string.DROP_VENUE_SQL);
        }

        /**
         * Execute Create Artist and Create Venue SQL
         * @param sqLiteDatabase the SqliteDatabase
         */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_ARTIST_SQL);
            sqLiteDatabase.execSQL(CREATE_VENUE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_ARTIST_SQL);
            sqLiteDatabase.execSQL(DROP_VENUE_SQL);
            onCreate(sqLiteDatabase);
        }
    }
}
