package gui.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class AddSongWindowController {
    public TextField fileField;
    public TextField genreField;
    public TextField artistField;
    public TextField titleField;
    public TextField timeField;
    private MainWindowController mainWindowController;
    private Stage stage;
    private Duration totalSongDuration;


    public void addSong(ActionEvent actionEvent) {
        // Retrieve the song properties from the text fields
        String title = titleField.getText();
        String artist = artistField.getText();
        String genre = genreField.getText();
        String filePath = fileField.getText();

        // Get the actual duration of the song
        Media newMedia = new Media(new File(filePath).toURI().toString());
        Duration duration = newMedia.getDuration();
        String formattedTime = formatDuration(duration);

        // Update the song properties in the MainWindowController
        mainWindowController.updateSongProperties(title, artist, genre, formattedTime, filePath);

        ((Stage) titleField.getScene().getWindow()).close();
    }

    public void browseFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Audio File");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            fileField.setText(selectedFile.getAbsolutePath());
            //Display the duration of the song
            Media newMedia = new Media(selectedFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(newMedia);
            mediaPlayer.setOnReady(() -> {
                totalSongDuration= mediaPlayer.getTotalDuration();
                String formattedTime = formatDuration(totalSongDuration);
                timeField.setText(formattedTime);
                mediaPlayer.dispose();
            });
        }
    }

    public void setMainWindowController(MainWindowController mainWindowController) {
        this.mainWindowController = mainWindowController;
    }

    private String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void closeAddSongWindow() {
        if (stage != null) {
            stage.close();
        }
    }
}
