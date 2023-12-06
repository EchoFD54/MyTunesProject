package dal;

import be.Song;

import java.sql.*;
import java.util.ArrayList;
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
            String sql = "INSERT INTO Songs(Title, Artist, Genre, Time, FilePath) VALUES (?,?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, s.titleProperty().get());
            pstmt.setString(2, s.artistProperty().get());
            pstmt.setString(3, s.genreProperty().get());
            pstmt.setString(4, s.timeProperty().get());
            pstmt.setString(5, s.filePathProperty().get());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();

        try(Connection con = cm.getConnection())
        {
            String sql = "SELECT * FROM Songs";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                //int id          = rs.getInt("SongId");
                String title    = rs.getString("Title");
                String artist   = rs.getString("Artist");
                String genre   = rs.getString("Genre");
                String time   = rs.getString("Time");
                String filePath   = rs.getString("FilePath");

                Song s = new Song(title, artist, genre, time, filePath);
                songs.add(s);
            }
            return songs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Song> getSongsFromPlaylist(int playlistId) {
        return null;
    }
}
