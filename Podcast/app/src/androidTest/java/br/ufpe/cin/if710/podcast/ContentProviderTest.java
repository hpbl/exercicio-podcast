package br.ufpe.cin.if710.podcast;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.ufpe.cin.if710.podcast.db.PodcastDBHelper;
import br.ufpe.cin.if710.podcast.db.PodcastProvider;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;

/**
 * Created by Pintor on 11/12/17.
 */

@RunWith(AndroidJUnit4.class)
public class ContentProviderTest extends ProviderTestCase2<PodcastProvider> {

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        ContentValues cv = new ContentValues();
        cv.put(PodcastProviderContract.EPISODE_TITLE, "hpbl e rrb");
        cv.put(PodcastProviderContract.EPISODE_DATE, "Tue, 12 Jun 1915 10:40:05 GMT");
        cv.put(PodcastProviderContract.EPISODE_LINK, "http://cin.ufpe.br/");
        cv.put(PodcastProviderContract.EPISODE_DESC, "Ótimos alunos");
        cv.put(PodcastProviderContract.EPISODE_DOWNLOAD_LINK, "https://hpbl.github.io/nota10.mp3");

        getMockContentResolver().insert(PodcastProviderContract.EPISODE_LIST_URI, cv);
    }

    public ContentProviderTest() {
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

    @Test
    public void removeItemFeed_correctly() {
        String where = PodcastProviderContract.EPISODE_TITLE + "=?";
        String[] args = {"hpbl e rrb"};

        int remove_resut = getMockContentResolver().delete(
                PodcastProviderContract.EPISODE_LIST_URI,
                where,
                args);

        Assert.assertEquals(1, remove_resut);
    }

    @Test
    public void queryItemFeed_correctly() {
        String selection = PodcastProviderContract.EPISODE_DESC + "=?";
        String[] args = {"Ótimos alunos"};

        Cursor queryResult = getMockContentResolver().query(
                                PodcastProviderContract.EPISODE_LIST_URI,
                                null,
                                selection,
                                args,
                                null);

        Assert.assertEquals(1, queryResult.getCount());
    }

    @Test
    public void queryItemFeed_incorrectly() {
        String selection = PodcastProviderContract.EPISODE_DESC + "=?";
        String[] args = {"Péssimos alunos"};

        Cursor queryResult = getMockContentResolver().query(
                PodcastProviderContract.EPISODE_LIST_URI,
                null,
                selection,
                args,
                null);

        Assert.assertEquals(0, queryResult.getCount());
    }

    @Test
    public void updateItemFeed_correctly() {
        ContentValues cv = new ContentValues();
        cv.put(PodcastProviderContract.EPISODE_TITLE, "Alunos nota 10");

        String where = PodcastProviderContract.EPISODE_DESC + "=?";
        String[] args = {"Ótimos alunos"};


        int update_count = getMockContentResolver().update(
                PodcastProviderContract.EPISODE_LIST_URI,
                cv,
                where,
                args);

        Assert.assertEquals(1, update_count);
    }

    @Test
    public void notInserting_duplicateItemFeed() {
        ContentValues cv = new ContentValues();
        cv.put(PodcastProviderContract.EPISODE_TITLE, "hpbl e rrb");
        cv.put(PodcastProviderContract.EPISODE_DATE, "Tue, 12 Jun 1915 10:40:05 GMT");
        cv.put(PodcastProviderContract.EPISODE_LINK, "http://cin.ufpe.br/");
        cv.put(PodcastProviderContract.EPISODE_DESC, "Ótimos alunos");
        cv.put(PodcastProviderContract.EPISODE_DOWNLOAD_LINK, "https://hpbl.github.io/nota10.mp3");

        Uri insert_result = getMockContentResolver().insert(PodcastProviderContract.EPISODE_LIST_URI, cv);

        assertNotNull(insert_result);

        String selection = PodcastProviderContract.EPISODE_DESC + "=?";
        String[] args = {"Ótimos alunos"};

        Cursor queryResult = getMockContentResolver().query(
                PodcastProviderContract.EPISODE_LIST_URI,
                null,
                selection,
                args,
                null);

        Assert.assertEquals(1, queryResult.getCount());
    }
}
