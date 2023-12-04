import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Song {
    private StringProperty name;
    private StringProperty artist;
    private StringProperty genre;
    private StringProperty time;

    public Song(String name, String artist, String genre, String time) {
        this.name = new SimpleStringProperty(name);
        this.artist = new SimpleStringProperty(artist);
        this.genre = new SimpleStringProperty(genre);
        this.time = new SimpleStringProperty(time);
    }

    public StringProperty nameProperty() {
        return name;
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

    public void setName(String name) {
        this.name.set(name);
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
}
