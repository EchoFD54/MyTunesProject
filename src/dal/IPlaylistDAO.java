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
     * @return a list of playlists
     */
    List<Playlist> getAllPlaylists();

    List<Song> createSongsOfPlaylist(int PlaylistId, int SongsId);

    List<Song> getAllSongsOfPlaylist(int PlaylistId);

    Playlist getPlaylistById(int playlistId);

    void updatePlaylist(Playlist playlist);

    void deleteSongFromPlaylist(int songId, int playlistId);
}
