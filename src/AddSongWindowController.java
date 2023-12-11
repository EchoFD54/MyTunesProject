import be.Song;
import bll.PlaylistManager;
import bll.SongManager;
import dal.ISongDAO;
import dal.SongDAO;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AddSongWindowController {
    public TextField fileField;
    public TextField genreField;
    public TextField artistField;
    public TextField titleField;
    private MainWindowController mainWindowController;

    SongManager songManager = new SongManager();


    public void addSong(ActionEvent actionEvent) {
        // Retrieve the song properties from the text fields
        String title = titleField.getText();
        String artist = artistField.getText();
        String genre = genreField.getText();
        String filePath = fileField.getText();

        // Update the song properties in the MainWindowController
        mainWindowController.updateSongProperties(title, artist, genre, filePath);


        for(Song song : songManager.getAllSongs()){
            System.out.println("Songs: " + song);
        }

        Song s = new Song(title, artist, genre, "0:00", filePath);
        songManager.createSong(s);



        // Close the AddSongWindow
        ((Stage) titleField.getScene().getWindow()).close();
    }

    public void browseFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Audio File");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            // Set the selected file path to the fileField
            fileField.setText(selectedFile.getAbsolutePath());
        }
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }
}
