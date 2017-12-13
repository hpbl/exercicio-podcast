package br.ufpe.cin.if710.podcast.ui;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.db.PodcastDatabase;
import br.ufpe.cin.if710.podcast.domain.ItemFeedRoom;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;

/**
 * Created by Pintor on 14/10/17.
 */

public class DownloadXMLIntentService extends IntentService {

    public static final String FEED_EXTRA = "RSSFeed";
    private String TAG = "DownloadXMLIntentService";

    public DownloadXMLIntentService() {
        super("DownloadXMLIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        List<ItemFeedRoom> itemList = new ArrayList<>();
        try {
            String feedURL = intent.getStringExtra(FEED_EXTRA);
            itemList = XmlFeedParser.parse(getRssFeed(feedURL));

            PodcastDatabase db = PodcastDatabase.getInstance(getApplicationContext());
            for (ItemFeedRoom item : itemList) {
                db.podcastDao().insert(item);
            }

            // sinalizando fim da tarefa
            Intent finishedDownloadingIntent = new Intent();
            finishedDownloadingIntent.setAction(MainActivity.FinishedDownloadingReceiver.ACTION_FINISHED_DOWNLOADING_XML);
            finishedDownloadingIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(finishedDownloadingIntent);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

    }

    //TODO Opcional - pesquise outros meios de obter arquivos da internet
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }
}