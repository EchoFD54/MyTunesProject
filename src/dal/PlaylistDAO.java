package dal;

import be.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO implements IPlaylistDAO {

    private final ConnectionManager cm = new ConnectionManager();

    /**
     * Deletes a Playlist on the Database by the ID
     */
    @Override
    public void deletePlaylist(int playlistId) {
        try(Connection con = cm.getConnection())
        {
            String sql = "DELETE FROM Playlists WHERE PlaylistId=?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, playlistId);
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates a Playlist on the Database by the ID
     * Values to update: Name
     */
    @Override
    public void updatePlaylist(int playlistId, String playlistName) {
        try(Connection con = cm.getConnection())
        {
            String sql = "UPDATE Playlists SET Name=? WHERE PlaylistId=?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, playlistName);
            pstmt.setInt(2, playlistId);
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a Playlist on the Database
     * Values to create: Name & Time (Time is automatically calculated?)
     */
    @Override
    public void createPlaylist(String playlistName, String playlistTime) {
        try(Connection con = cm.getConnection())
        {
            String sql = "INSERT INTO Playlists(Name, Time) VALUES (?,?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, playlistName);
            pstmt.setString(2, playlistTime);
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Need UPDATE: change List<Song> for List<Playlist>
     * @return a list of playlists
     */
    @Override
    public List<Song> getAllPlaylists() {
        List<Song> songs = new ArrayList<>();

        try(Connection con = cm.getConnection())
        {
            String sql = "SELECT * FROM Playlists";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                int id          = rs.getInt("PlaylistId");
                String title    = rs.getString("Name");
                String time   = rs.getString("Time");

                //code needs to be updated for -> Playlist
                //Song s = new Song(title, artist, genre, time, filePath);
                //songs.add(s);
            }
            return songs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
