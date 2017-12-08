package br.ufpe.cin.if710.podcast;

import android.content.ContentValues;
import android.net.Uri;
import android.test.ProviderTestCase2;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.ufpe.cin.if710.podcast.db.PodcastProvider;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;

/**
 * Created by Ricardo R Barioni on 08/12/2017.
 */


public class DatabaseTest extends ProviderTestCase2<PodcastProvider> {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setContext(InstrumentationRegistry);
    }

    public DatabaseTest() {
        super(PodcastProvider.class, "br.ufpe.cin.if710.podcast.feed");
    }

    @Test
    public void insertItemFeed_correctly() {
        ContentValues cv = new ContentValues();
        cv.put(PodcastProviderContract.EPISODE_TITLE, "Oi tudo bom com você");
        cv.put(PodcastProviderContract.EPISODE_DATE, "Sun, 20 Jun 2010 10:40:05 GMT");
        cv.put(PodcastProviderContract.EPISODE_LINK, "http://frontdaciencia.ufrgs.br/#1");
        cv.put(PodcastProviderContract.EPISODE_DESC, "Programa 1");
        cv.put(PodcastProviderContract.EPISODE_DOWNLOAD_LINK, "https://hpbl.github.io/hub42_APS/audio/Oi%20Tudo%20Bom.mp3");

        Uri insert_result = getMockContentResolver().insert(PodcastProviderContract.EPISODE_LIST_URI, cv);

        assertNotNull(insert_result);
    }

}
