package br.ufpe.cin.if710.podcast.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PodcastDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "podcasts";
    public static final String DATABASE_TABLE = "episodes";
    private static final int DB_VERSION = 1;

    private PodcastDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    private static PodcastDBHelper db;

    public static PodcastDBHelper getInstance(Context c) {
        if (db==null) {
            db = new PodcastDBHelper(c.getApplicationContext());
        }
        return db;
    }


    final private static String CREATE_CMD =
            "CREATE TABLE "+DATABASE_TABLE+" ("
                    + PodcastProviderContract.EPISODE_LINK + " TEXT PRIMARY KEY NOT NULL, "
                    + PodcastProviderContract.EPISODE_TITLE + " TEXT NOT NULL, "
                    + PodcastProviderContract.EPISODE_DATE + " TEXT NOT NULL, "
                    + PodcastProviderContract.EPISODE_DESC + " TEXT NOT NULL, "
                    + PodcastProviderContract.EPISODE_DOWNLOAD_LINK + " TEXT NOT NULL, "
                    + PodcastProviderContract.EPISODE_FILE_URI + " TEXT NOT NULL)";



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        throw new RuntimeException("inutilizado");
    }
}
