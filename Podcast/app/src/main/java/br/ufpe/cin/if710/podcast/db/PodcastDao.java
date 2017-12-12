package br.ufpe.cin.if710.podcast.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import br.ufpe.cin.if710.podcast.domain.ItemFeedRoom;

/**
 * Created by Ricardo R Barioni on 11/12/2017.
 */

@Dao
public interface PodcastDao {
    @Query("SELECT * FROM " + ItemFeedRoom.table_name)
    LiveData<List<ItemFeedRoom>> getAllItemFeedRoom();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ItemFeedRoom itemFeedRoom);

    @Update
    void update(ItemFeedRoom itemFeedRoom);
}
