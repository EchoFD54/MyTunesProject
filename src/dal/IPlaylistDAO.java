package dal;

import be.Playlist;
import be.Song;

import java.util.List;

public interface IPlaylistDAO {

    /**
     * Deletes a Playlist on the Database by the ID
     */
    void deletePlaylist(int playlistId);

    /**
     * Updates a Playlist on the Database by the ID
     * Values to update: Name
     */
    void updatePlaylist(String newPlaylistName, String playlistName);

    /**
     * Creates a Playlist on the Database
     * Values to create: Name & Time
     */
    void createPlaylist(Playlist playlist);

    /**
     * @return a list of playlists saved on the database
     */
    List<Playlist> getAllPlaylists();

    /**
     * Associates a song with a playlist in the database
     */
    List<Song> createSongsOfPlaylist(int PlaylistId, int SongsId);

    /**
     * @return a list of songs from a specific playlist saved on the database
     */
    List<Song> getAllSongsOfPlaylist(int PlaylistId);

    Playlist getPlaylistById(int playlistId);

    /**
     * Deletes a song in a playlist
     */
    void deleteSongFromPlaylist(int songId, int playlistId);
}
