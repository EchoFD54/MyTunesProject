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
     * Values to create: Name & Time
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
                int id          = rs.getInt("PlaylistId");
                String name    = rs.getString("Name");
                String time   = rs.getString("Time");

                Playlist p = new Playlist(id, name, time);
                playlists.add(p);
            }
            return playlists;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Song> createSongsOfPlaylist(int PlaylistId, int SongsId) {
        try(Connection con = cm.getConnection())
        {
            String sql = "INSERT INTO songInPlaylist(playlistid, songsid) VALUES (?,?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, PlaylistId);
            pstmt.setInt(2, SongsId);
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public List<Song> getAllSongsOfPlaylist(int PlaylistId) {
        List<Song> SongsInPlaylist = new ArrayList<>();
        try(Connection con = cm.getConnection())
        {
            String sql = "select *\n" +
                    "from Songs s\n" +
                    "inner join songInPlaylist sp on s.SongsId = sp.SongsId\n" +
                    "where PlaylistId = (?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, PlaylistId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int id            = rs.getInt("SongsId");
                String title      = rs.getString("Title");
                String artist     = rs.getString("Artist");
                String genre      = rs.getString("Genre");
                String time       = rs.getString("Time");
                String filePath   = rs.getString("FilePath");

                Song s = new Song(id,title, artist, genre, time, filePath);
                SongsInPlaylist.add(s);
            }
            return SongsInPlaylist;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Playlist getPlaylistById(int playlistId){
        try(Connection con = cm.getConnection())
        {
            String sql = "SELECT * FROM Playlists WHERE PlaylistId=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, playlistId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                int id          = rs.getInt("PlaylistId");
                String name     = rs.getString("Name");
                String time     = rs.getString("Time");

                return new Playlist(id, name, time);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void updatePlaylist(Playlist playlist) {

    }

    public void deleteSongFromPlaylist(int songId, int playlistId){
        try(Connection con = cm.getConnection())
        {
            String sql = "DELETE FROM songInPlaylist WHERE SongsId=? AND PlaylistId=?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, songId);
            pstmt.setInt(2, playlistId);
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
