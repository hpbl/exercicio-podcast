package br.ufpe.cin.if710.podcast.ui.adapter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.ui.EpisodeDetailActivity;

public class PodcastItemAdapter extends ArrayAdapter<ItemFeed> {

    // Constantes para o intent
    public static final String TITLE_EXTRA = "Title";
    public static final String PUBDATE_EXTRA = "PubDate";
    public static final String DESCRIPTION_EXTRA = "Description";
    public static final String DOWNLOAD_LINK_EXTRA = "DownloadLink";

    int linkResource;

    public PodcastItemAdapter(Context context, int resource, List<ItemFeed> objects) {
        super(context, resource, objects);
        linkResource = resource;
    }


    //http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
    static class ViewHolder {
        TextView item_title;
        TextView item_date;
        Button downloadButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), linkResource, null);
            holder = new ViewHolder();
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_date = (TextView) convertView.findViewById(R.id.item_date);
            holder.downloadButton = (Button) convertView.findViewById(R.id.item_action);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // item atual
        final ItemFeed item = getItem(position);
        holder.item_title.setText(item.getTitle());
        holder.item_date.setText(item.getPubDate());

        // ouvindo clicks na lista (tem que ser aqui pois tem um botão na lista)
        // https://issuetracker.google.com/issues/36908602
        holder.item_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // criando intent para trocar de tela
                Context context = getContext();
                Intent detailIntent = new Intent(context, EpisodeDetailActivity.class);

                // passando dados pela intent
                detailIntent.putExtra(TITLE_EXTRA, item.getTitle());
                detailIntent.putExtra(PUBDATE_EXTRA, item.getPubDate());
                detailIntent.putExtra(DESCRIPTION_EXTRA, item.getDescription());
                detailIntent.putExtra(DOWNLOAD_LINK_EXTRA, item.getDownloadLink());

                // chamando trasição de tela
                context.startActivity(detailIntent);
            }
        });

        // ouvindo clicks no botão
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DownloadPodcast(getContext(), item).execute();
            }
        });

        return convertView;
    }
}

class DownloadPodcast extends AsyncTask<Void, Integer, String> {
    private Context context;
    private String TAG = "DOWNLOAD TASK";
    private ItemFeed item;
    File outputFile = null;

    public DownloadPodcast(Context context, ItemFeed item) {
        this.context = context;
        this.item = item;
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(this.context, "Baixando episódio", Toast.LENGTH_LONG).show();
    }

    @Override
    protected String doInBackground(Void ...voids) {
        try {
            // pegando url de download
            URL url = new URL(this.item.getDownloadLink());

            // abrindo conexão com método GET
            HttpURLConnection c = (HttpURLConnection) url.openConnection(); //
            c.setRequestMethod("GET");
            c.connect();

            // log se algo der errado com a conexão
            if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
                        + " " + c.getResponseMessage());

            }

            File apkStorage = null;

            // conferindo se tem cartão SD
            if (this.isSDCardPresent()) {
                apkStorage = new File(
                        // local onde ficará salvo o arquivo
                        Environment.getExternalStorageDirectory() + "/"
                                + "PodcastsdeHilton");
            } else {
                Toast.makeText(context, "Sem cartão SD", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Sem cartão SD");
            }


            // se não já existir o diretório, criar ele
            if (!apkStorage.exists()) {
                apkStorage.mkdir();
                Log.d(TAG, "Diretório criado" + apkStorage.getAbsolutePath());
            }
            Log.d(TAG, "Diretório existente" + apkStorage.getAbsolutePath());

            // criando arquivo com nome escolhido
            String fileName = this.item.getTitle() + ".mp3";
            outputFile = new File(apkStorage, fileName);

            // se o arquivo não existe no sistema, cria
            if (!outputFile.exists()) {
                Log.d(TAG, "Arquivo criado");
                outputFile.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            InputStream inputStream = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len1);
            }

            item.setLocalURI(outputFile.getPath());
            fileOutputStream.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            outputFile = null;
            Log.e(TAG, "Download Error Exception " + e.getMessage());
        }

        return null;
    }


    private boolean isSDCardPresent() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(String s) {
        if (outputFile == null) {
            Log.e(TAG, "Erro no download!");
            Toast.makeText(this.context, "Erro no download!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Terminou de baixar!");
            Toast.makeText(this.context, "fim do download...", Toast.LENGTH_SHORT).show();

            // adicionar URI de download no banco
            ContentValues content = new ContentValues();
            content.put(PodcastProviderContract.EPISODE_FILE_URI, item.getLocalURI());

            //fazer update
            context.getContentResolver().update(PodcastProviderContract.EPISODE_LIST_URI,
                    content,
                    PodcastProviderContract.EPISODE_LINK + "= \"" + item.getLink() + "\"",
                    null);
        }
    }
}