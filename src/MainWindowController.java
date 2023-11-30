import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.FileChooser;

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
    @FXML
    public Label songLabel;
    @FXML
    private ListView<String> songListView;
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private List<Media> songList = new ArrayList<>();
    private int songIndex = 0;
    private double currentVolume = 0.5; // Set a default volume (0.5 represents 50% volume)
    private boolean canPlaySong = false;



    public void initialize() {
        File folder = new File("C:\\Users\\aaron\\Music\\Mytunes");
        File[] songs = folder.listFiles(((dir, name) -> name.endsWith(".mp3")));

        if (songs != null) {
            for (File file : songs) {
                songList.add(new Media(file.toURI().toString()));
            }
        }

        if (songList.isEmpty()) {
            return;
        }

        timerLabel.setText("0:00");
        playNextSong();
        System.out.println("Number of songs in the playlist: " + songList.size());

        // Populate the ListView with song names
        ObservableList<String> songNames = FXCollections.observableArrayList();
        for (Media media : songList) {
            try {
                String decodedName = URLDecoder.decode(new File(media.getSource()).getName(), "UTF-8");
                songNames.add(decodedName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        songListView.setItems(songNames);

        // Set a listener for handling song selection
        songListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int selectedSongIndex = songListView.getSelectionModel().getSelectedIndex();
            if (selectedSongIndex >= 0) {
                songIndex = selectedSongIndex;
                playNextSong();
            }
        });
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

            try {
                String decodedName = URLDecoder.decode(new File(songList.get(songIndex).getSource()).getName(), "UTF-8");

                Text songText = new Text(decodedName);
                songText.setStyle("-fx-font-size: 40.0;"); // Set the initial font size

                songTextFlow.getChildren().clear();
                songTextFlow.getChildren().add(songText);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            // All songs have been played, loop back to the first song
            songIndex = 0;
            playNextSong();
        }
    }

    public void clickPlayBtn(ActionEvent actionEvent) {
        canPlaySong =true;
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
                double duration = mediaPlayer.getTotalDuration().toMillis();
                double currentTime = songProgress.getValue() * duration / 100.0;
                mediaPlayer.seek(new Duration(currentTime));
            }
        });

        // Update the progress slider as the media plays
        songProgress.valueChangingProperty().setValue(null);
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!songProgress.isValueChanging()) {
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

    private void playCurrentSong(){
        if (canPlaySong){
            mediaPlayer.play();
        }
    }
}
