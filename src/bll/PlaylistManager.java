package bll;

import be.Playlist;
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
     * Need UPDATE: change List<Song> for List<Playlist>
     * @return a list of playlists
     * Gets all the Playlists saved on the Database
     */
    public List<Playlist> getAllPlaylists() {
        return playlistDAO.getAllPlaylists();
    }

    public List<Song> CreateSongsOfPlaylist(int PlaylistId, int SongsId){
        return playlistDAO.CreateSongsOfPlaylist(PlaylistId, SongsId);
    }

    public List<Song> getAllSongsOfPlaylist(int PlaylistId){
        return playlistDAO.getAllSongsOfPlaylist(PlaylistId);
    }

    public void removeSongFromPlaylist(int playlistId, int songId){
        List<Song> songsInPlaylist = getAllSongsOfPlaylist(playlistId);
        songsInPlaylist.removeIf(song -> song.getSongId() == songId);
        Playlist playlist = playlistDAO.getPlaylistById(playlistId);
        playlist.setSongs(songsInPlaylist.toString());
        playlistDAO.updatePlaylist(playlist);
    }

}
