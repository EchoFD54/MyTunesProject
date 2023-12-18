package dal;

import be.Playlist;
import be.Song;

import java.util.List;

public interface IPlaylistDAO {

    /**
     * Deletes a Playlist on the Database by the ID
     */
    public void deletePlaylist(int playlistId);

    /**
     * Updates a Playlist on the Database by the ID
     * Values to update: Name
     */
    public void updatePlaylist(String newPlaylistName, String playlistName);

    /**
     * Creates a Playlist on the Database
     * Values to create: Name & Time (Time is automatically calculated?)
     */
    public void createPlaylist(Playlist playlist);

    /**
     * Need UPDATE: change List<Song> for List<Playlist>
     * @return a list of playlists
     */
    public List<Playlist> getAllPlaylists();

    public List<Song> CreateSongsOfPlaylist(int PlaylistId, int SongsId);

    public List<Song> getAllSongsOfPlaylist(int PlaylistId);

    Playlist getPlaylistById(int playlistId);

    void updatePlaylist(Playlist playlist);
}
