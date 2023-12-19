package bll;

import be.Song;
import dal.ISongDAO;
import dal.SongDAO;
import java.util.List;

public class SongManager {
    ISongDAO songDAO = new SongDAO();

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

}
