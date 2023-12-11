import be.Playlist;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewPlaylistController {
    @FXML
    private TextField playlistNameField;
    private MainWindowController mainWindowController;
    public void setMainWindowController(MainWindowController controller){
        this.mainWindowController = controller;
    }

    @FXML
    public void savePlaylist(){
        String playlistName = playlistNameField.getText().trim();
        Playlist playlist = new Playlist(playlistName);
        if (playlistName != null){
            mainWindowController.createPlaylist(playlist);
            mainWindowController.getPlaylist(playlist);
            closeWindow();
        }
    }



    @FXML
    public void cancelAction(){
        closeWindow();
    }

    private void closeWindow(){
        Stage stage = (Stage) playlistNameField.getScene().getWindow();
        stage.close();
    }

}

