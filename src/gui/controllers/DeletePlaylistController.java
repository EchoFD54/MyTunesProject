package gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DeletePlaylistController {
    public Button btnConfirmDelete;
    private MainWindowController mainWindowController;

    public void setMainWindowController(MainWindowController controller){
        this.mainWindowController = controller;
    }
    public void cancelAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnConfirmDelete.getScene().getWindow();
        stage.close();
    }

    public void confirmDelete(ActionEvent actionEvent) {
        mainWindowController.deletePlaylist();
        Stage stage = (Stage) btnConfirmDelete.getScene().getWindow();
        stage.close();
    }

}

