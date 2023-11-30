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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
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
    private ListView<String> songListView;
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private List<Media> songList = new ArrayList<>();
    private int songIndex = 0;
    private double currentVolume = 0.5;
    private boolean canPlaySong = false;



    public void initialize() {
        // Set a listener for handling song selection
        songListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int selectedSongIndex = songListView.getSelectionModel().getSelectedIndex();
            if (selectedSongIndex >= 0) {
                songIndex = selectedSongIndex;
                playNextSong();
            }
        });

        // Set the event handler for the addSongsBtn button
        addSongsBtn.setOnAction(this::clickAddSongsBtn);

        if (songList.isEmpty()) {
            return;
        }

        timerLabel.setText("0:00");
        playNextSong();
        System.out.println("Number of songs in the playlist: " + songList.size());




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
                songText.setStyle("-fx-font-size: 40.0;"); // Set the font size

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

    private void playCurrentSong(){
        if (canPlaySong){
            mediaPlayer.play();
        }
    }

    @FXML
    public void clickAddSongsBtn(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Songs");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(new Stage());

        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                Media newMedia = new Media(file.toURI().toString());
                songList.add(newMedia);

                try {
                    String decodedName = URLDecoder.decode(file.getName(), "UTF-8");
                    ObservableList<String> songNames = songListView.getItems();
                    songNames.add(decodedName);
                    songListView.setItems(songNames);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            if (mediaPlayer == null) {
                playNextSong();
            }
        }
    }
}
