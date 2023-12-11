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

    /**
     * Creates a Song on the Database
     */
    public void createSong(Song s) {
        songDAO.createSong(s);
    }

    /**
     * Updates a Song on the Database
     */
    public void updateSong(Song s){
        songDAO.updateSong(s);
    }

    /**
     * Deletes a Song on the Database
     */
    public void deleteSong(int songId){
        songDAO.deleteSong(songId);
    }

    /**
     * @return a list of all Songs saved on the Database
     */
    public List<Song> getAllSongs() {
        return songDAO.getAllSongs();
    }

    /**
     * @return a list of all Songs saved on the Database of a specific Playlist
     */
    /*
    public List<Song> getSongsFromPlaylist(Playlist playlist) {
        return petDAO.getPetsFromOwner(selected.getId());
    }
     */

}
