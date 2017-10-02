package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;

public class EpisodeDetailActivity extends Activity {
    private TextView mTitleTV;
    private TextView mDescriptionTV;
    private TextView mPubDateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_detail);

        // Mostrando botão de voltar (up)
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //COMPLETED preencher com informações do episódio clicado na lista...
        this.mTitleTV = findViewById(R.id.title_tv);
        this.mDescriptionTV = findViewById(R.id.description_tv);
        this.mPubDateTV = findViewById(R.id.pubDate_tv);

        this.mTitleTV.setText(this.getIntent().getExtras().getString(XmlFeedAdapter.TITLE_EXTRA));
        this.mDescriptionTV.setText(this.getIntent().getExtras().getString(XmlFeedAdapter.DESCRIPTION_EXTRA));
        this.mPubDateTV.setText(this.getIntent().getExtras().getString(XmlFeedAdapter.PUBDATE_EXTRA));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // botão de voltar
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
