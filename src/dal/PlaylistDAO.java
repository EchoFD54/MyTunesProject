package dal;

import be.Playlist;
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
    public void updatePlaylist(String newPlaylistName, String playlistName) {
        try(Connection con = cm.getConnection())
        {
            String sql = "UPDATE Playlists SET Name=? WHERE Name=?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, newPlaylistName);
            pstmt.setString(2, playlistName);
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
    public void createPlaylist(Playlist playlist) {
        try(Connection con = cm.getConnection())
        {
            String sql = "INSERT INTO Playlists(Name) VALUES (?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, playlist.getName().get());
            //pstmt.setString(2, playlist.getTime().get());
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
    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = new ArrayList<>();

        try(Connection con = cm.getConnection())
        {
            String sql = "SELECT * FROM Playlists";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()){
                //int id          = rs.getInt("PlaylistId");
                String name    = rs.getString("Name");
                String time   = rs.getString("Time");

                Playlist p = new Playlist(name, time);
                playlists.add(p);
            }
            return playlists;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
