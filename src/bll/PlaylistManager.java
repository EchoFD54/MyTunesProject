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

    public void createPlaylist(String playlistName, String playlistTime) {
        playlistDAO.createPlaylist(playlistName, playlistTime);
    }

    public void updatePlaylist(int playlistId, String playlistName) {
        playlistDAO.updatePlaylist(playlistId, playlistName);
    }

    public void deletePlaylist(int playlistId) {
        playlistDAO.deletePlaylist(playlistId);
    }

    /**
     * Need UPDATE: change List<Song> for List<Playlist>
     * @return a list of playlists
     */
    public List<Song> getAllPlaylists() {
        return playlistDAO.getAllPlaylists();
    }

}
