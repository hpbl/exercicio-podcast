package br.ufpe.cin.if710.podcast.ui;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;
import br.ufpe.cin.if710.podcast.ui.adapter.PodcastItemAdapter;

public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";
    //COMPLETED teste com outros links de podcast

    private ListView itemsListView;
    public FinishedDownloadingReceiver broadcastReceiver;
    private String TAG = "Main Activity";
    private Boolean isInForeground = true;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsListView = (ListView) findViewById(R.id.items);

        Stetho.initializeWithDefaults(this);

        checkDownloadPodcastsPermissions(this);

        // registrando broadcast receiver
        this.registerReceiver();

        // conferindo conexão com a internet
        new NetworkCheckTask().execute();

        Log.d(this.TAG, "hpbl ON CREATE");
    }

    public static void checkDownloadPodcastsPermissions(Activity activity) {
        // Solicitar permissões para salvar arquivos no dispositivo
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // carregando dados do banco
        if (!this.isInForeground) {
            new DataBaseTask().execute();
        }

        Log.d(this.TAG, "hpbl ON START");
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.isInForeground = true;

        Log.d(this.TAG, "hpbl ON RESUME");
    }


    @Override
    protected void onStop() {
        super.onStop();
        PodcastItemAdapter adapter = (PodcastItemAdapter) itemsListView.getAdapter();

        // adapter vai ser null quando o usuário abrir o app pela primeira vez sem internet
        if (adapter != null) {
            adapter.clear();
        }

        this.isInForeground = false;

        Log.d(this.TAG, "hpbl ON STOP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        this.unregisterReceiver(this.broadcastReceiver);
    }

    // AssyncTask para pegar podcasts do banco de dados
    private class DataBaseTask extends  AsyncTask<Void, Void, Cursor> {
        @Override
        protected void onPreExecute() {
            // indicativo visual que o carregamento está sendo do banco
            Toast.makeText(getApplicationContext(), "consultando banco", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {

            // fazendo consulta (query) do banco
            Cursor cursor = getContentResolver()
                    .query(PodcastProviderContract.EPISODE_LIST_URI,
                            null,
                            null,
                            null,
                            null);

            return cursor;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            // caso não tenha nada no banco
            if ((cursor == null) || (cursor.getCount() == 0)) {
                // informe ao usuário que ele precisa de internet
                Toast.makeText(getApplicationContext(), "Conecte-se à internet", Toast.LENGTH_LONG).show();
            // caso tenha conteúdo no banco
            } else {
                Toast.makeText(getApplicationContext(), "terminando...", Toast.LENGTH_SHORT).show();

                List<ItemFeed> items = new ArrayList<>();

                while (cursor.moveToNext()) {
                    String title = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_TITLE));
                    String link = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_LINK));
                    String date = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_DATE));
                    String description = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_DESC));
                    String downloadLink = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_DOWNLOAD_LINK));
                    String localUri = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_FILE_URI));
                    String playbackTime = cursor.getString(cursor.getColumnIndex(PodcastProviderContract.EPISODE_PLAYBACK_TIME));

                    ItemFeed item = new ItemFeed(title, link, date, description, downloadLink);
                    item.setLocalURI(localUri);

                    if (playbackTime != null) {
                        int time = Integer.parseInt(playbackTime);
                        item.setPlaybackTime(time);
                    } else {
                        item.setPlaybackTime(0);
                    }

                    items.add(item);
                }

                //Adapter Personalizado
                PodcastItemAdapter adapter = new PodcastItemAdapter(getApplicationContext(),
                                                                    R.layout.itemlista,
                                                                    items);

                //atualizar o list view
                itemsListView.setAdapter(adapter);
                itemsListView.setTextFilterEnabled(true);
            }
        }
    }

    // assync task para testar conexão com a internet
    private class NetworkCheckTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "checando conexão", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            if (this.isNetworkAvailable()) {
                // fazendo ping do google para ver se a conexão está funcionando
                try {
                    HttpURLConnection urlc = (HttpURLConnection)
                            (new URL("http://clients3.google.com/generate_204")
                                    .openConnection());
                    urlc.setRequestProperty("User-Agent", "Android");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    return (urlc.getResponseCode() == 204 &&
                            urlc.getContentLength() == 0);
                } catch (IOException e) {
                    Log.w("NetworkCheckTask", "Error checking internet connection", e);
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            /* conferindo se o usuário tem conexão com a internet
           para saber se deve ser feito o parse do XML */
            if (result) {
                Log.d("MAINACTIVITY", "COM INTERNET");
                Toast.makeText(getApplicationContext(), "Lendo Feed", Toast.LENGTH_SHORT).show();

                // iniciando IntentService que vai fazer o parse do XML e salvar no banco
                Intent downloadXML = new Intent(getApplicationContext(), DownloadXMLIntentService.class);
                downloadXML.putExtra(DownloadXMLIntentService.FEED_EXTRA, RSS_FEED);
                getApplicationContext().startService(downloadXML);

            } else {
                Log.d("MAINACTIVITY", "SEM INTERNET");
                new DataBaseTask().execute();
            }

        }

        // função para conferir se usuário está com conexão ativa
        private boolean isNetworkAvailable() {
            ConnectivityManager cm =
                    (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            return isConnected;
        }
    }



    public class FinishedDownloadingReceiver extends BroadcastReceiver {
        public static final String ACTION_FINISHED_DOWNLOADING_XML = "ACTION_FINISHED_DOWNLOADING_XML";

        @Override
        public void onReceive(Context context, Intent intent) {
            // se o app estiver em foreground,
            if (isInForeground) {
                // atualize a interface com os dados do banco
                new MainActivity.DataBaseTask().execute();
            } else {
                // notifique o usuário
                Log.d("DownloadingReceiver", "em background");
                this.sendNotification();
            }
        }

        public void sendNotification() {
            // acessando NotificationManager
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setContentTitle("Seus podcasts estão prontos")
                            .setContentText("Abra o app para ver o que tem de novo");


            // acessando serviço
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            // notificando
            mNotificationManager.notify(001, mBuilder.build());
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter(FinishedDownloadingReceiver.ACTION_FINISHED_DOWNLOADING_XML);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        this.broadcastReceiver = new FinishedDownloadingReceiver();
        registerReceiver(this.broadcastReceiver, filter);
    }
}

