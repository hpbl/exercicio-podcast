package br.ufpe.cin.if710.podcast.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteAbortException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class PodcastProvider extends ContentProvider {
    // instância do helper que vai ser utilizada nos métodos do provider
    private PodcastDBHelper mPodcastDBHelper;

    public PodcastProvider() {
    }

    @Override
    public boolean onCreate() {
        // COMPLETED: Implement this to initialize your content provider on startup.
        Context context = this.getContext();
        this.mPodcastDBHelper = PodcastDBHelper.getInstance(context);
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //  Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // COMPLETED: Implement this to handle requests to insert a new row.
        // pegando referência ao banco com permissão de escrita
        final SQLiteDatabase db = this.mPodcastDBHelper.getWritableDatabase();

        Uri returnUri;
        Long id = db.insert(PodcastProviderContract.EPISODE_TABLE, null, values);

        if (id > 0) {
            //inserção feita com sucesso
            returnUri = ContentUris.withAppendedId(PodcastProviderContract.EPISODE_LIST_URI, id);
            Log.d("PodcastProvider", returnUri.toString());
        } else {
            throw new android.database.SQLException("falha na inserção em: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // COMPLETED: Implement this to handle query requests from clients.
        // pegando referência ao banco com permissão de leitura
        final SQLiteDatabase db = this.mPodcastDBHelper.getReadableDatabase();

        // consultando o banco repassando os parâmetros
        Cursor cursor = db.query(PodcastProviderContract.EPISODE_TABLE,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);

        // alertando o cursor se houverem mudanças na URI
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
