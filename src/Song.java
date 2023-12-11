import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Song {
    private StringProperty title;
    private StringProperty artist;
    private StringProperty genre;
    private StringProperty time;
    private StringProperty filePath;

    public Song(String title, String artist, String genre, String time, String filePath) {
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.genre = new SimpleStringProperty(genre);
        this.time = new SimpleStringProperty(time);
        this.filePath = new SimpleStringProperty(filePath);
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty artistProperty() {
        return artist;
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public StringProperty timeProperty() {
        return time;
    }
    public StringProperty filePathProperty() {
        return filePath;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public void setArtist(String artist) {
        this.artist.set(artist);
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }
}
