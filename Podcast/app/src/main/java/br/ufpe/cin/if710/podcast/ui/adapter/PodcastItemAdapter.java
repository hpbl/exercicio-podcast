package br.ufpe.cin.if710.podcast.ui.adapter;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import br.ufpe.cin.if710.podcast.R;
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
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), linkResource, null);
            holder = new ViewHolder();
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_date = (TextView) convertView.findViewById(R.id.item_date);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // item atual
        final ItemFeed item = getItem(position);
        holder.item_title.setText(item.getTitle());
        holder.item_date.setText(item.getPubDate());

        // ouvindo clicks (tem que ser aqui pois tem um botão na lista)
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

        return convertView;
    }

}