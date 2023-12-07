import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class MainWindowController {
    @FXML
    public Slider volumeSlider;
    @FXML
    public Button playBtn;
    @FXML
    public Slider songProgress;
    @FXML
    public Button nextBtn;
    @FXML
    public Button previousBtn;
    @FXML
    public Label timerLabel;
    @FXML
    public TextFlow songTextFlow;
    public ListView playlistList;
    @FXML
    public Button addSongsBtn;
    @FXML
    public TableView<Song> songTableView;
    @FXML
    public TableColumn titleColumn;
    @FXML
    public TableColumn artistColumn;
    @FXML
    public TableColumn genreColumn;
    @FXML
    public TableColumn timeColumn;
    @FXML
    private ListView<String> songListView;
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private List<Media> songList = new ArrayList<>();
    private int songIndex = 0;
    private double currentVolume = 0.5;
    private boolean canPlaySong = false;
    private String currentSongName = "";


    public void initialize() {
        // Set up the columns in the TableView
        TableColumn<Song, String> titleColumn = (TableColumn<Song, String>) songTableView.getColumns().get(0);
        TableColumn<Song, String> artistColumn = (TableColumn<Song, String>) songTableView.getColumns().get(1);
        TableColumn<Song, String> genreColumn = (TableColumn<Song, String>) songTableView.getColumns().get(2);
        TableColumn<Song, String> timeColumn = (TableColumn<Song, String>) songTableView.getColumns().get(3);

        // Define  value for each column
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());

        // Set a listener for handling song selection
        songTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int selectedSongIndex = songTableView.getSelectionModel().getSelectedIndex();
            if (selectedSongIndex >= 0) {
                songIndex = selectedSongIndex;
                playNextSong();
            }
        });

        // Set the event handler for the addSongs button
        addSongsBtn.setOnAction(this::clickAddSongsBtn);

        if (songList.isEmpty()) {
            return;
        }

        timerLabel.setText("0:00");
        playNextSong();
        System.out.println("Number of songs in the playlist: " + songList.size());
        System.out.println("My Change");


    }


    private void playNextSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        if (songIndex < songList.size()) {
            MediaPlayer newMediaPlayer = new MediaPlayer(songList.get(songIndex));

            // Set the volume to the value from the previous MediaPlayer
            if (mediaPlayer != null) {
                newMediaPlayer.setVolume(mediaPlayer.getVolume());
            }

            mediaPlayer = newMediaPlayer;

            // Set up the end of media handler
            mediaPlayer.setOnEndOfMedia(() -> {
                songIndex++;
                playNextSong();
            });

            mediaView.setMediaPlayer(mediaPlayer);
            setSongProgress();
            setVolumeSlider();
            playCurrentSong();
            updateTimeLabel();
            updateTextFlow();

        } else {
            // All songs have been played, loop back to the first song
            songIndex = 0;
            playNextSong();
        }
    }

    public void clickPlayBtn(ActionEvent actionEvent) {
        canPlaySong = true;
        setSongProgress();
        if (mediaPlayer == null) {
            // Initialize MediaPlayer
            if (!songList.isEmpty()) {
                mediaPlayer = new MediaPlayer(songList.get(0));
                mediaView.setMediaPlayer(mediaPlayer);
                setVolumeSlider();  // Initialize volume slider
                playBtn.setText("Pause");
                playCurrentSong();
            }
        } else {
            // Toggle between play and pause
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playBtn.setText("Play");
            } else {
                playCurrentSong();
                playBtn.setText("Pause");
            }
        }
    }

    public void clickNextSong(ActionEvent actionEvent) {
        if (songIndex < songList.size()) {
            songIndex++;
            playNextSong();
        }
    }

    public void clickPreviousSong(ActionEvent actionEvent) {
        if (songIndex > 0) {
            songIndex--;
            playNextSong();
        }
    }

    public void setSongProgress() {
        // Set up the progress slider for the new song
        songProgress.setValue(0);
        songProgress.valueChangingProperty().setValue(null);
        songProgress.valueChangingProperty().addListener((observable, oldValue, isChanging) -> {
            if (!isChanging) {
                playBtn.setText("Play");
                double duration = mediaPlayer.getTotalDuration().toMillis();
                double currentTime = songProgress.getValue() * duration / 100.0;
                mediaPlayer.seek(new Duration(currentTime));
            }
        });

        // Update the progress slider as the media plays
        songProgress.valueChangingProperty().setValue(null);
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!songProgress.isValueChanging()) {
                playBtn.setText("Pause");
                double progress = (newValue.toMillis() / mediaPlayer.getTotalDuration().toMillis()) * 100.0;
                songProgress.setValue(progress);
            }
        });
    }

    public void setVolumeSlider() {
        if (mediaPlayer != null) {
            volumeSlider.setValue(mediaPlayer.getVolume() * 100);

            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                currentVolume = newValue.doubleValue() / 100.0;
                mediaPlayer.setVolume(currentVolume);
            });
        }
    }

    private void updateTimeLabel() {
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            Duration currentTime = mediaPlayer.getCurrentTime();
            String formattedTime = formatDuration(currentTime);
            timerLabel.setText(formattedTime);
        });
    }

    private String formatDuration(Duration duration) {
        int minutes = (int) duration.toMinutes();
        int seconds = (int) (duration.toSeconds() % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    private void playCurrentSong() {
        if (canPlaySong) {
            mediaPlayer.play();
        }
    }

    @FXML
    public void clickAddSongsBtn(ActionEvent actionEvent) {
        // Open the AddSongWindowController
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AddSongWindow.fxml"));
        Parent root;
        try {
            root = loader.load();

            // Get the controller instance
            AddSongWindowController addSongController = loader.getController();

            // Pass the reference of MainWindowController to AddSongWindowController
            addSongController.setMainWindowController(this);

            // Create a new stage for the AddSongWindow
            Stage stage = new Stage();
            stage.setTitle("Add Song");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateSongProperties(String title, String artist, String genre, String filePath) {
        // Create a new Media object from the selected file path
        Media newMedia = new Media(new File(filePath).toURI().toString());

        // Create a new Song object
        Song newSong = new Song(title, artist, genre, "0:00");

        // Add the newMedia to the songList
        songList.add(newMedia);

        // Add the newSong to the TableView
        songTableView.getItems().add(newSong);

        // If mediaPlayer is null, start playing the new song
        if (mediaPlayer == null) {
            playNextSong();

        }
        // Set the current song name
        currentSongName = title;

    }

    private void updateTextFlow() {
        Text songText = new Text(currentSongName);
        songText.setStyle("-fx-font-size: 40.0;");

        songTextFlow.getChildren().clear();
        songTextFlow.getChildren().add(songText);
    }

}
