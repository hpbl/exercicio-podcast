Utilizando Architechture Components
=============

Realizamos a refatoração do código, com o objetivo de eliminar o uso do SQLiteOpenHelper e ContentProvider (bem como o seu Contract) no contexto de persistência dos dados dos podcasts na aplicação. Tal código removido corresponde às classes PodcastDBHelper, PodcastProvider e PodcastProviderContract.

Para tal, utilizamos a biblioteca de persistência Room, no qual seguimos as coordenadas indicadas pelo artigo https://medium.com/@ajaysaini.official/building-database-with-room-persistence-library-ecf7d0b8f3e9. Os passos para implementar a sua utlizização foram:

- Criação do DAO (Data Access Object), o qual define os métodos de acesso do banco de dados, usando *annotation* para acoplar o SQL a cada um dos métodos;

```Java
@Dao
public interface PodcastDao {
    @Query("SELECT * FROM " + ItemFeedRoom.table_name)
    List<ItemFeedRoom> getAllItemFeedRoom();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(ItemFeedRoom itemFeedRoom);

    @Update
    void update(ItemFeedRoom itemFeedRoom);
}
```

- Criação da classe de banco de dados, o qual de fato usa a implementação do Room para lidar com a criação de uma instância do banco de dados. Para tal, foi utilizado o padrão de projeto *Singleton*;

```Java
@Database(entities = {ItemFeedRoom.class}, version = 4)
public abstract class PodcastDatabase extends RoomDatabase {
    public static PodcastDatabase instance;

    public abstract PodcastDao podcastDao();

    public static PodcastDatabase getInstance(Context context) {
        if (instance == null) {
            return Room.databaseBuilder(
                context.getApplicationContext(),
                PodcastDatabase.class,
                "podcasts.db")
                .allowMainThreadQueries()
                .build();
        }
        return instance;
    }
}
```

- Alteração do modelo *ItemFeed*, adaptando-o para a utilização em conjunto com o DAO. A partir de então, o nome da classe passou a se chamar *ItemFeedRoom*.

```Java
@Entity(tableName = "episodes")
public class ItemFeedRoom {

	public static final String table_name = "episodes";
	public static final String NO_URI = "SEMURI";

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

	public String getTitle()        { return title; }
    public String getLink()         { return link; }
    public String getPubDate()      { return pubDate; }
    public String getDescription()  { return description; }
    public String getDownloadLink() { return downloadLink; }
    public String getDownloadUri()  { return downloadUri; }
    public int getPlaybackTime()    { return playbackTime; }

    public void setDownloadUri(String downloadUri) {
        this.downloadUri = downloadUri;
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
```

