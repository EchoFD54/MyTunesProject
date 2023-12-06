package dal;

import be.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SongDAO implements ISongDAO {

    private final ConnectionManager cm = new ConnectionManager();

    @Override
    public Song getSong(int id) {
        return null;
    }

    @Override
    public void deleteSong(int id) {

    }

    @Override
    public void updateSong(Song s) {

    }

    @Override
    public void createSong(Song s) {
        try(Connection con = cm.getConnection())
        {
            String sql = "INSERT INTO Songs(Title, Artist, Genre, Time, FilePath, PlaylistId) VALUES (?,?,?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, s.titleProperty().get());
            pstmt.setString(5, s.filePathProperty().get());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Song> getAllSongs() {
        return null;
    }

    @Override
    public List<Song> getSongsFromPlaylist(int playlistId) {
        return null;
    }
}
