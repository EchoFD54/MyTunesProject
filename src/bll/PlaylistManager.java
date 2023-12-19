package bll;

import be.Playlist;
import be.Song;
import dal.IPlaylistDAO;
import dal.PlaylistDAO;

import java.util.List;

public class PlaylistManager {
    IPlaylistDAO playlistDAO = new PlaylistDAO();

    /**
     * Creates a Playlist on the Database
     */
    public void createPlaylist(Playlist playlist) {
        playlistDAO.createPlaylist(playlist);
    }

    /**
     * Updates a Playlist on the Database by the ID of the same
     */
    public void updatePlaylist(String newPlaylistName, String playlistName) {
        playlistDAO.updatePlaylist(newPlaylistName, playlistName);
    }

    /**
     * Deletes a Playlist on the Database by the ID
     */
    public void deletePlaylist(int playlistId) {
        playlistDAO.deletePlaylist(playlistId);
    }

    /**
     * @return a list of playlists
     * Gets all the Playlists saved on the Database
     */
    public List<Playlist> getAllPlaylists() {
        return playlistDAO.getAllPlaylists();
    }

    public List<Song> createSongsOfPlaylist(int PlaylistId, int SongsId){
        return playlistDAO.createSongsOfPlaylist(PlaylistId, SongsId);
    }

    public List<Song> getAllSongsOfPlaylist(int PlaylistId){
        return playlistDAO.getAllSongsOfPlaylist(PlaylistId);
    }

    public void deleteSongFromPlaylist(int songId, int playlistId){
        playlistDAO.deleteSongFromPlaylist(songId, playlistId);
    }

}
