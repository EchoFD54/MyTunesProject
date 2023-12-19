package gui.controllers;

import be.Playlist;
import bll.PlaylistManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditPlaylistController {
    @FXML
    private TextField editPlaylistNameField;
    private MainWindowController mainWindowController;
    private Playlist selectedPlaylistName;
    private PlaylistManager playlistManager = new PlaylistManager();

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
                playlistManager.updatePlaylist(newPlaylistName, selectedPlaylistName.getName().get());
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

