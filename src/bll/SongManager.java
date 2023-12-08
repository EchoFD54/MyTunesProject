package bll;

import be.Song;
import dal.IPlaylistDAO;
import dal.ISongDAO;
import dal.PlaylistDAO;
import dal.SongDAO;

import java.util.List;

public class SongManager {
    ISongDAO songDAO = new SongDAO();
    IPlaylistDAO playlistDAO = new PlaylistDAO();

    public void createSong(Song s) {
        songDAO.createSong(s);
    }

    public void updateSong(Song s){
        songDAO.updateSong(s);
    }

    public void deleteSong(String title){
        songDAO.deleteSong(title);
    }

    public List<Song> getAllSongs() {
        return songDAO.getAllSongs();
    }

    /*
    public List<Song> getSongsFromPlaylist(Playlist playlist) {
        return petDAO.getPetsFromOwner(selected.getId());
    }

     */

}
