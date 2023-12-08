package dal;

import be.Song;

import java.util.List;

public interface IPlaylistDAO {
    public void deletePlaylist(int playlistId);
    public void updatePlaylist(int playlistId, String playlistName);
    public void createPlaylist(String playlistName, String playlistTime);
    public List<Song> getAllPlaylists();

}
