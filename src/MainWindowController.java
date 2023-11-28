import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

public class MainWindowController {
    @FXML
    public Slider volumeSlider;
    @FXML
    public Button playBtn;
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;

    public void initialize(){
        Media media = new Media(getClass().getResource("Undertale OST_ 068 - Death by Glamour.mp3").toExternalForm());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);

        volumeSlider.setValue(mediaPlayer.getVolume() * 100); // set initial value
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
        });

        playBtn.setOnAction(event -> {
            if(mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING){
                mediaPlayer.pause();
                playBtn.setText("Play");}
            else {
                mediaPlayer.play();
                playBtn.setText("Pause");
            }
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.ZERO);
            playBtn.setText("Play");
    });
    }


    public void clickPlay(ActionEvent actionEvent) {
    }
}
