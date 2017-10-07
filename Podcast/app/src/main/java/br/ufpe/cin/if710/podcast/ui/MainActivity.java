package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    //TODO teste com outros links de podcast

    private ListView itemsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        itemsListView = (ListView) findViewById(R.id.items);

        Stetho.initializeWithDefaults(this);
    }

    // função para conferir se usuário tem conexão com a a internet
    private boolean hasNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
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

        /* conferindo se o usuário tem conexão com a internet
           para saber se deve ser feito o parse do XML */
        if (this.hasNetworkConnection()) {
            new DownloadXmlTask().execute(RSS_FEED);
            Log.d("MAINACTIVITY", "COM INTERNET");
        } else {
            Log.d("MAINACTIVITY", "SEM INTERNET");
            new DataBaseTask().execute();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        PodcastItemAdapter adapter = (PodcastItemAdapter) itemsListView.getAdapter();

        // adapter vai ser null quando o usuário abrir o app pela primeira vez sem internet
        if (adapter != null) {
            adapter.clear();
        }
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

                    ItemFeed item = new ItemFeed(title, link, date, description, downloadLink);
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

    private class DownloadXmlTask extends AsyncTask<String, Void, List<ItemFeed>> {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getApplicationContext(), "lendo XML", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected List<ItemFeed> doInBackground(String... params) {
            List<ItemFeed> itemList = new ArrayList<>();
            try {
                itemList = XmlFeedParser.parse(getRssFeed(params[0]));

                for (ItemFeed item : itemList) {
                    ContentValues content = new ContentValues();

                    content.put(PodcastProviderContract.EPISODE_TITLE, item.getTitle());
                    content.put(PodcastProviderContract.EPISODE_DATE, item.getPubDate());
                    content.put(PodcastProviderContract.EPISODE_LINK, item.getLink());
                    content.put(PodcastProviderContract.EPISODE_DESC, item.getDescription());
                    content.put(PodcastProviderContract.EPISODE_DOWNLOAD_LINK, item.getDownloadLink());
                    content.put(PodcastProviderContract.EPISODE_FILE_URI, "");

                    Uri uri = getContentResolver().insert(PodcastProviderContract.EPISODE_LIST_URI,
                            content);

                    if (uri != null) {
                        Log.d("Main Activity", "Item inseridos com sucesso");
                    } else {
                        Log.e("Main Activity", "Falha na inserção do item: " + item.toString());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }

            return itemList;
        }

        @Override
        protected void onPostExecute(List<ItemFeed> feed) {
            // uma vez atualizado o conteúdo do banco, use o mesmo para povoar a listVew
            new DataBaseTask().execute();
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
