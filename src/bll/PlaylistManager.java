package bll;

import be.Song;
import dal.IPlaylistDAO;
import dal.ISongDAO;
import dal.PlaylistDAO;
import dal.SongDAO;

import java.util.List;

public class PlaylistManager {
    ISongDAO songDAO = new SongDAO();
    IPlaylistDAO playlistDAO = new PlaylistDAO();

    /**
     * Creates a Playlist on the Database
     */
    public void createPlaylist(String playlistName, String playlistTime) {
        playlistDAO.createPlaylist(playlistName, playlistTime);
    }

    /**
     * Updates a Playlist on the Database by the ID of the same
     */
    public void updatePlaylist(int playlistId, String playlistName) {
        playlistDAO.updatePlaylist(playlistId, playlistName);
    }

    /**
     * Deletes a Playlist on the Database by the ID
     */
    public void deletePlaylist(int playlistId) {
        playlistDAO.deletePlaylist(playlistId);
    }

    /**
     * Need UPDATE: change List<Song> for List<Playlist>
     * @return a list of playlists
     * Gets all the Playlists saved on the Database
     */
    public List<Song> getAllPlaylists() {
        return playlistDAO.getAllPlaylists();
    }

}
