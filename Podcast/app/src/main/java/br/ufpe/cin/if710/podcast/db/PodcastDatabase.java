package br.ufpe.cin.if710.podcast.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import br.ufpe.cin.if710.podcast.domain.ItemFeedRoom;

/**
 * Created by Ricardo R Barioni on 11/12/2017.
 */

@Database(entities = {ItemFeedRoom.class}, version = 4)
public abstract class PodcastDatabase extends RoomDatabase {
    public static PodcastDatabase instance;

    public abstract PodcastDao podcastDao();

    public static PodcastDatabase getInstance(Context context) {
        if (instance == null) {
            return Room.databaseBuilder(
                context.getApplicationContext(),
                PodcastDatabase.class,
                "podcasts.db")
                .allowMainThreadQueries()
                .build();
        }
        return instance;
    }

}
