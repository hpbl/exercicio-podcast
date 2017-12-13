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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastDatabase;
import br.ufpe.cin.if710.podcast.db.PodcastProviderContract;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.ItemFeedRoom;
import br.ufpe.cin.if710.podcast.ui.EpisodeDetailActivity;

public class PodcastItemAdapter extends ArrayAdapter<ItemFeedRoom> {

    // Constantes para o intent
    public static final String TITLE_EXTRA = "Title";
    public static final String PUBDATE_EXTRA = "PubDate";
    public static final String DESCRIPTION_EXTRA = "Description";
    public static final String DOWNLOAD_LINK_EXTRA = "DownloadLink";

    private String TAG = "Podcast Adapter";

    int linkResource;

    public PodcastDatabase db;

    public PodcastItemAdapter(Context context, int resource, List<ItemFeedRoom> objects) {
        super(context, resource, objects);
        linkResource = resource;
        this.db = PodcastDatabase.getInstance(this.getContext());
    }


    //http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
    static class ViewHolder {
        TextView item_title;
        TextView item_date;
        Button downloadButton;

        ItemFeedRoom item;
        MediaPlayer mediaPlayer;

        public static final String baixar = "baixar";
        public static final String tocar = "tocar";
        public static final String pausar = "pausar";
        public static final String retomar = "retomar";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
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
        holder.item = getItem(position);
        holder.item_title.setText(holder.item.getTitle());
        holder.item_date.setText(holder.item.getPubDate());

        // se a URI não for vazia, significa que o podcast pode ser tocado
        if (!holder.item.getDownloadUri().equals(PodcastProviderContract.NO_URI)) {
            holder.downloadButton.setText(ViewHolder.tocar);
        }

        // configurando botão e mediaplayer caso o
        // podcast já tenha começado a ser ouvido antes
        if (holder.item.getPlaybackTime() > 0) {
            holder.downloadButton.setText(ViewHolder.retomar);

            holder.mediaPlayer = MediaPlayer.create(getContext(),
                      Uri.parse(holder.item.getDownloadUri()));

            holder.mediaPlayer.setLooping(false);

            holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    File podcast = new File(holder.item.getDownloadUri());

                    if (podcast.delete()) {
                        holder.item.setPlaybackTime(0);
                        holder.item.setDownloadUri(PodcastProviderContract.NO_URI);

                        db.podcastDao().update(holder.item);

                        holder.downloadButton.setText(ViewHolder.baixar);
                    } else {
                        Log.e(TAG, "ERRO NA HORA DE APAGAR PODCAST");
                    }
                }
            });
        }

        // ouvindo clicks na lista (tem que ser aqui pois tem um botão na lista)
        // https://issuetracker.google.com/issues/36908602
        holder.item_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // criando intent para trocar de tela
                Context context = getContext();
                Intent detailIntent = new Intent(context, EpisodeDetailActivity.class);

                detailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // passando dados pela intent
                detailIntent.putExtra(TITLE_EXTRA, holder.item.getTitle());
                detailIntent.putExtra(PUBDATE_EXTRA, holder.item.getPubDate());
                detailIntent.putExtra(DESCRIPTION_EXTRA, holder.item.getDescription());
                detailIntent.putExtra(DOWNLOAD_LINK_EXTRA, holder.item.getDownloadLink());

                // chamando trasição de tela
                context.startActivity(detailIntent);
            }
        });

        // ouvindo clicks no botão
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    // confere qual o estado atual do botão,
                    // faz a ação correspondente no media player
                    // e atualiza o título do botão,
                    // ou baixa o episódio
                    Button button = (Button) view;

                    switch (button.getText().toString()) {

                        case ViewHolder.baixar:
                            new DownloadPodcast(getContext(), holder).execute();
                            break;

                        case ViewHolder.tocar:
                            holder.mediaPlayer = MediaPlayer.create(getContext(),
                                    Uri.parse(holder.item.getDownloadUri()));

                            holder.mediaPlayer.setLooping(false);
                            holder.mediaPlayer.start();

                            holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    File podcast = new File(holder.item.getDownloadUri());

                                    if (podcast.delete()) {
                                        holder.item.setPlaybackTime(0);
                                        holder.item.setDownloadUri(PodcastProviderContract.NO_URI);

                                        db.podcastDao().update(holder.item);

                                        holder.downloadButton.setText(ViewHolder.baixar);
                                    } else {
                                        Log.e(TAG, "ERRO NA HORA DE APAGAR PODCAST");
                                    }
                                }
                            });

                            button.setText(ViewHolder.pausar);
                            break;

                        case ViewHolder.pausar:
                            holder.mediaPlayer.pause();

                            int currentPosition = holder.mediaPlayer.getCurrentPosition();
                            Log.d(TAG, "current position: " + currentPosition);
                            holder.item.setPlaybackTime(currentPosition);

                            db.podcastDao().update(holder.item);

                            button.setText(ViewHolder.retomar);
                            break;

                        case ViewHolder.retomar:
                            // avança para o último momento ouvido
                            Log.d(TAG, String.valueOf(holder.item.getPlaybackTime()));

                            holder.mediaPlayer.seekTo(holder.item.getPlaybackTime());
                            holder.mediaPlayer.start();

                            button.setText(ViewHolder.pausar);
                    }
                }
        });

        return convertView;
    }
}

class DownloadPodcast extends AsyncTask<Void, Integer, String> {
    private Context context;
    private String TAG = "DOWNLOAD TASK";
    private PodcastItemAdapter.ViewHolder holder;
    File outputFile = null;

    public PodcastDatabase db;

    public DownloadPodcast(Context context, PodcastItemAdapter.ViewHolder holder) {
        this.context = context;
        this.holder = holder;
        this.db = PodcastDatabase.getInstance(this.context);
    }

    @Override
    protected void onPreExecute() {
        Toast.makeText(this.context, "Baixando episódio", Toast.LENGTH_LONG).show();
    }

    @Override
    protected String doInBackground(Void ...voids) {
        try {
            // pegando url de download
            URL url = new URL(this.holder.item.getDownloadLink());

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
            String fileName = this.holder.item.getTitle() + ".mp3";
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

//            holder.item.setLocalURI(outputFile.getPath());
            holder.item.setDownloadUri(outputFile.getPath());
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

            db.podcastDao().update(holder.item);

            //atualizar botão
            holder.downloadButton.setText(PodcastItemAdapter.ViewHolder.tocar);
        }
    }
}