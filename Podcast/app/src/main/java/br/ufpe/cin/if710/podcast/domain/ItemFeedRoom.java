package br.ufpe.cin.if710.podcast.domain;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by Ricardo R Barioni on 11/12/2017.
 */

@Entity(tableName = "episodes")
public class ItemFeedRoom {

	public static final String table_name = "episodes";

	@ColumnInfo(name = "title")
	@NonNull
	private String title;

	@PrimaryKey
	@ColumnInfo(name = "link")
	@NonNull
	private String link;

	@ColumnInfo(name = "pubDate")
	@NonNull
	private String pubDate;

	@ColumnInfo(name = "description")
	@NonNull
	private String description;

	@ColumnInfo(name = "downloadLink")
	@NonNull
	private String downloadLink;

	@ColumnInfo(name = "downloadUri")
	@NonNull
	private String downloadUri;

	@ColumnInfo(name = "playbackTime")
	private int playbackTime;

	public ItemFeedRoom(String title, String link, String pubDate, String description, String downloadLink, String downloadUri, int playbackTime) {
		this.title = title;
		this.link = link;
		this.pubDate = pubDate;
		this.description = description;
		this.downloadLink = downloadLink;
		this.downloadUri = downloadUri;
		this.playbackTime = playbackTime;
	}

	public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public String getDownloadUri() {
        return downloadUri;
    }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
    }

    public int getPlaybackTime() {
        return playbackTime;
    }

    public void setPlaybackTime(int playbackTime) {
        this.playbackTime = playbackTime;
    }

    @Override
    public String toString() {
        return title + "\n" +
            link + "\n" +
            pubDate + "\n" +
            description + "\n" +
            downloadLink + "\n";
    }
}

/*
@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @ColumnInfo(name = "userid")
    private String mId;

    @ColumnInfo(name = "username")
    private String mUserName;

    @ColumnInfo(name = "last_update")
    private Date mDate;

    @Ignore
    public User(String userName) {
        mId = UUID.randomUUID().toString();
        mUserName = userName;
        mDate = new Date(System.currentTimeMillis());
    }

    public User(String id, String userName, Date date) {
        this.mId = id;
        this.mUserName = userName;
        this.mDate = date;
    }
...
}
*/