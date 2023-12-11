package dal;

import be.Song;

import java.util.List;

public interface ISongDAO {

    /**
     * Deletes a Song on the Database by the ID
     */
    public void deleteSong(int songId);

    /**
     * Updates a Song on the Database
     */
    public void updateSong(Song s);

    /**
     * Creates a Song on the Database
     */
    public void createSong(Song s);

    /**
     * @return a list of all Songs saved on the Database
     */
    public List<Song> getAllSongs();

    /**
     * @return a list of all Songs saved on the Database of a specific Playlist
     */
    public List<Song> getSongsFromPlaylist(int playlistId);

}
