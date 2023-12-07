package dal;

import be.Song;

import java.util.List;

public interface ISongDAO {

    public void deleteSong(String title);
    public void updateSong(Song s);
    public void createSong(Song s);
    public List<Song> getAllSongs();
    public List<Song> getSongsFromPlaylist(int playlistId);

}
