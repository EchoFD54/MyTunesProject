package dal;

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
    public void updatePlaylist(int playlistId, String playlistName);

    /**
     * Creates a Playlist on the Database
     * Values to create: Name & Time (Time is automatically calculated?)
     */
    public void createPlaylist(String playlistName, String playlistTime);

    /**
     * Need UPDATE: change List<Song> for List<Playlist>
     * @return a list of playlists
     */
    public List<Song> getAllPlaylists();

}
