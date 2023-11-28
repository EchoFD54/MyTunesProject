import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import java.io.File;
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
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private List<Media> songList = new ArrayList<>();
    private int songIndex = 0;


    public void initialize(){
        File folder = new File("C:\\Users\\aaron\\Music\\Mytunes");
        File[] songs = folder.listFiles(((dir, name) -> name.endsWith(".mp3")));

        if(songs !=null){
            for (File file:songs){
                songList.add(new Media(file.toURI().toString()));
            }
        }
        System.out.println("Number of songs in the playlist: " + songList.size());
        mediaPlayer = new MediaPlayer(songList.get(songIndex));
        mediaView.setMediaPlayer(mediaPlayer);

        //volume
        volumeSlider.setValue(mediaPlayer.getVolume() * 50); // set initial value
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
        });

        //set the song progress
        songProgress.valueChangingProperty().addListener((observable, oldValue, isChanging) -> {
            if (!isChanging && mediaPlayer != null) {
                double duration = mediaPlayer.getTotalDuration().toMillis();
                double currentTime = songProgress.getValue() * duration / 100.0;
                mediaPlayer.seek(new Duration(currentTime));
            }
        });

        // Update the song progress as the media plays
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!songProgress.isValueChanging()) {
                double progress = (newValue.toMillis() / mediaPlayer.getTotalDuration().toMillis()) * 100.0;
                songProgress.setValue(progress);
            }
        });
    }

    private void playNextSong(){
        if (songIndex < songList.size()) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }

            mediaPlayer = new MediaPlayer(songList.get(songIndex));

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

            // Set up the end of media handler
            mediaPlayer.setOnEndOfMedia(() -> {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED) {
                    songIndex++;
                    playNextSong();
                }
            });

            // Auto-play the media
            mediaPlayer.setAutoPlay(true);
            mediaView.setMediaPlayer(mediaPlayer);
        } else {
            // All songs have been played, loop back to the first song
            songIndex = 0;
            playNextSong();
        }
    }

    public void clickPlayBtn(ActionEvent actionEvent) {
        if (mediaPlayer == null) {
            // Initialize MediaPlayer and play the first song
            if (!songList.isEmpty()) {
                mediaPlayer = new MediaPlayer(songList.get(0));
                mediaView.setMediaPlayer(mediaPlayer);
                playBtn.setText("Pause");
                mediaPlayer.play();
            }
        } else {
            // Toggle between play and pause
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playBtn.setText("Play");
            } else {
                mediaPlayer.play();
                playBtn.setText("Pause");
            }
        }
    }

    public void clickNextSong(ActionEvent actionEvent) {
        if (songIndex<songList.size()){
            songIndex++;
            playNextSong();
        }
    }

    public void clickPreviousSong(ActionEvent actionEvent) {
        if (songIndex>0){
            songIndex--;
            playNextSong();
        }
    }

}
