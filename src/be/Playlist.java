package be;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {

    private IntegerProperty id;
    private StringProperty name;
    private StringProperty time;
    private StringProperty songs;



    public Playlist(String name){
        this.name = new SimpleStringProperty(name);
    }

    public Playlist(String name, String time){
        this.name = new SimpleStringProperty(name);
        this.time = new SimpleStringProperty(time);
    }

    public Playlist(int id, String name, String time){
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.time = new SimpleStringProperty(time);
    }

    public Playlist(String name, String time, String songs){
        this.name = new SimpleStringProperty(name);
        this.time = new SimpleStringProperty(time);
        this.songs = new SimpleStringProperty(songs);
    }

    public Playlist(int id, String name, String time, String songs){
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.time = new SimpleStringProperty(time);
        this.songs = new SimpleStringProperty(songs);
    }

    public IntegerProperty getId(){
        return id;
    }

    public StringProperty getName(){
        return name;
    }

    public StringProperty getTime(){
        return time;
    }

    public StringProperty getSongs(){
        return songs;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public void setSongs(String songs) {
        this.songs.set(songs);
    }

    @Override
    public String toString(){
        return "Playlist{" +
                "name=" + name.get() +
                '}';
    }

}