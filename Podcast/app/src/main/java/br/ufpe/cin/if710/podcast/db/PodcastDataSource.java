package br.ufpe.cin.if710.podcast.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.util.List;

import br.ufpe.cin.if710.podcast.domain.ItemFeedRoom;

/**
 * Created by Ricardo R Barioni on 12/12/2017.
 */

public class PodcastDataSource extends AndroidViewModel {
    private LiveData<List<ItemFeedRoom>> podcasts;

    public PodcastDataSource(Application application) {
        super(application);
    }

    public LiveData<List<ItemFeedRoom>> getAllItemFeedRoom(Application application) {
        if (podcasts == null) {
            podcasts = new MutableLiveData<>();
            podcasts = PodcastDatabase.getInstance(application.getApplicationContext()).podcastDao().getAllItemFeedRoom();
        }
        return podcasts;
    }
}
