import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DeletePlaylistController { public Button btnConfirmDelete;
    private MainWindowController mainWindowController;

    private int PlaylistId;

    public void setMainWindowController(MainWindowController controller){
        this.mainWindowController = controller;
    }
    public void cancelAction(ActionEvent actionEvent) {
        Stage stage = (Stage) btnConfirmDelete.getScene().getWindow();
        stage.close();
    }

    public void confirmDelete(ActionEvent actionEvent) {
        mainWindowController.deletePlaylist(PlaylistId);
        Stage stage = (Stage) btnConfirmDelete.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void setSelectedId(int PlaylistId){
        this.PlaylistId = PlaylistId;
    }

}
