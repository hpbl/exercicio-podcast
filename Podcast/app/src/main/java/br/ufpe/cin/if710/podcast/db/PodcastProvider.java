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
        // acessando o banco com permissão de escrita
        final SQLiteDatabase db = this.mPodcastDBHelper.getWritableDatabase();

        // deletando do banco
        int numDeleted = db.delete(PodcastProviderContract.EPISODE_TABLE,
                        selection,
                        selectionArgs);


        if (numDeleted != 0) {
            // notificando remoção
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numDeleted;
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

        // tentando fazer update, mas se não existir o item
        String primaryKey = PodcastProviderContract.EPISODE_LINK;
        long id = db.update(PodcastProviderContract.EPISODE_TABLE,
                values,
                primaryKey + "= \"" + values.getAsString(primaryKey) + "\"",
                null);

        // insere ele no banco
        if (id == 0) {
            values.put(PodcastProviderContract.EPISODE_FILE_URI, PodcastProviderContract.NO_URI);
            db.insert(PodcastProviderContract.EPISODE_TABLE,
                    null,
                    values);
        }
        return ContentUris.withAppendedId(PodcastProviderContract.EPISODE_LIST_URI, id);
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
        // COMPLETED: Implement this to handle requests to update one or more rows.
        // pegando referência ao banco com permissão de escrita
        final SQLiteDatabase db = this.mPodcastDBHelper.getWritableDatabase();

        int numUpdated = db.update(PodcastProviderContract.EPISODE_TABLE,
                values,
                selection,
                selectionArgs);

        if (numUpdated != 0) {
            // notificando modificação
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }
}
