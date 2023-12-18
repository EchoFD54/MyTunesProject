package dal;

import be.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDAO implements ISongDAO {

    private final ConnectionManager cm = new ConnectionManager();

    /**
     * Deletes a Song on the Database (on table Songs and songInPlaylist) by the ID
     */
    @Override
    public void deleteSong(int songId) {
        try(Connection con = cm.getConnection())
        {
            String sql = "DELETE FROM songInPlaylist WHERE SongsId=?";
            String sql1 = "DELETE FROM Songs WHERE SongsId=?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            PreparedStatement pstmt1 = con.prepareStatement(sql1);
            pstmt.setInt(1, songId);
            pstmt.execute();
            pstmt1.setInt(1, songId);
            pstmt1.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates a Song on the Database
     * Values to update: Title, Artist and Genre
     */
    @Override
    public void updateSong(Song s) {
        try(Connection con = cm.getConnection())
        {
            String sql = "UPDATE Songs SET Title=?, Artist=?, Genre=? WHERE SongsId=?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, s.titleProperty().get());
            pstmt.setString(2, s.artistProperty().get());
            pstmt.setString(3, s.genreProperty().get());
            pstmt.setInt(4, s.songIdProperty().get());
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a Song on the Database
     * Values to add: Title, Artist, Genre, Time and FilePath
     */
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

    /**
     * @return a list of all Songs saved on the Database
     */
    @Override
    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();

        try(Connection con = cm.getConnection())
        {
            String sql = "SELECT * FROM Songs";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                int id            = rs.getInt("SongsId");
                String title      = rs.getString("Title");
                String artist     = rs.getString("Artist");
                String genre      = rs.getString("Genre");
                String time       = rs.getString("Time");
                String filePath   = rs.getString("FilePath");

                Song s = new Song(id,title, artist, genre, time, filePath);
                songs.add(s);
            }
            return songs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return a list of all Songs saved on the Database of a specific Playlist
     */

    public List<Song> getSongsFromPlaylist(int playlistId) {
        List<Song> songs = new ArrayList<>();

        try(Connection con = cm.getConnection())
        {
            String sql = "SELECT * FROM Songs WHERE PlaylistId=?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, playlistId);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                int id            = rs.getInt("SongId");
                String title      = rs.getString("Title");
                String artist     = rs.getString("Artist");
                String genre      = rs.getString("Genre");
                String time       = rs.getString("Time");
                String filePath   = rs.getString("FilePath");

                Song s = new Song(title, artist, genre, time, filePath);
                songs.add(s);
            }
            return songs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
