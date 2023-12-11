import be.Playlist;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditPlaylistController {
    @FXML
    private TextField editPlaylistNameField;
    private MainWindowController mainWindowController;
    private Playlist selectedPlaylistName;

    public void setMainWindowController(MainWindowController controller){
        this.mainWindowController = controller;
    }

    @FXML
    public void setSelectedPlaylistName(Playlist playlistName){
        this.selectedPlaylistName = playlistName;
        editPlaylistNameField.setText(playlistName.getName().getValue());
    }

    @FXML
    public void saveEditedPlaylist(){
        if (selectedPlaylistName != null) {
            String newPlaylistName = editPlaylistNameField.getText().trim();

            if (!newPlaylistName.isEmpty()) {
                mainWindowController.editPlaylist(newPlaylistName, selectedPlaylistName);
                closeEditedWindow();
            }
        }
    }
    private void closeEditedWindow(){
        Stage stage = (Stage) editPlaylistNameField.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void cancelAction(){
        closeEditedWindow();
    }
}

