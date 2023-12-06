package dal;

import be.Song;

import java.util.List;

public interface ISongDAO {

    public Song getSong(int id);
    public void deleteSong(int id);
    public void updateSong(Song s);
    public void createSong(Song s);
    public List<Song> getAllSongs();
    public List<Song> getSongsFromPlaylist(int playlistId);

}
