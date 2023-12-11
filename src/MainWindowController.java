
import be.Playlist;
import be.Song;
import bll.SongManager;
import dal.ISongDAO;
import dal.SongDAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
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
    @FXML
    public Button addSongsBtn;
    @FXML
    public TableView<Song> songTableView;
    @FXML
    public TableView<Playlist> playlistList;
    @FXML
    public TableColumn titleColumn;
    @FXML
    public TableColumn artistColumn;
    @FXML
    public TableColumn genreColumn;
    @FXML
    public TableColumn timeColumn;
    @FXML
    public TableColumn playlistName;
    @FXML
    public TableColumn songs;
    @FXML
    public TableColumn time;
    @FXML
    private ListView<String> songListView;
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private List<Media> songList = new ArrayList<>();

    private Playlist selectedPlaylist;
    private int songIndex = 0;
    private double currentVolume = 0.5;
    private boolean canPlaySong = false;
    private String currentSongName = "";


    ISongDAO songDAO = new SongDAO();
    SongManager songManager = new SongManager();



    public void initialize() {
        // Set up the columns in the TableView
        TableColumn<Song, String> titleColumn = (TableColumn<Song, String>) songTableView.getColumns().get(0);
        TableColumn<Song, String> artistColumn = (TableColumn<Song, String>) songTableView.getColumns().get(1);
        TableColumn<Song, String> genreColumn = (TableColumn<Song, String>) songTableView.getColumns().get(2);
        TableColumn<Song, String> timeColumn = (TableColumn<Song, String>) songTableView.getColumns().get(3);

        // Define cell value factories for each column
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        artistColumn.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        genreColumn.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        timeColumn.setCellValueFactory(cellData -> cellData.getValue().timeProperty());


        TableColumn<Playlist, String> playlistName = (TableColumn<Playlist, String>) playlistList.getColumns().get(0);
        TableColumn<Playlist, String> songs = (TableColumn<Playlist, String>) playlistList.getColumns().get(1);
        TableColumn<Playlist, String> time = (TableColumn<Playlist, String>) playlistList.getColumns().get(2);

        // Define cell value factories for each column
        playlistName.setCellValueFactory(cellData -> cellData.getValue().getName());
        songs.setCellValueFactory(cellData -> cellData.getValue().getSongs());
        time.setCellValueFactory(cellData -> cellData.getValue().getTime());

        // Set a listener for handling song selection
        songTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int selectedSongIndex = songTableView.getSelectionModel().getSelectedIndex();
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

            // Update the TextFlow with the current song name
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

    public void deletePlaylist(int selectedPlaylistId){
        ObservableList<Playlist> playlists = playlistList.getItems();
        playlists.remove(selectedPlaylistId);
    }
    public void editPlaylist(String selectedPlaylistName, Playlist newPlaylistName){
        newPlaylistName.setName(selectedPlaylistName);
        ObservableList<Playlist> playlists = playlistList.getItems();
        int index = playlists.indexOf(selectedPlaylistName);
        if (index != -1){
            playlists.set(index, newPlaylistName);  // Update the name of the playlist
            playlistList.setItems(playlists);  // Update the playlistList view
        }
    }
    @FXML
    private void openNewPlaylistWindow() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewPlaylist.fxml"));
            Parent root = loader.load();

            NewPlaylistController newWindowController = loader.getController();
            newWindowController.setMainWindowController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Create New Playlist");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateTimeLabel() {
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            Duration currentTime = mediaPlayer.getCurrentTime();
            String formattedTime = formatDuration(currentTime);
            timerLabel.setText(formattedTime);
        });
    }

    public void getPlaylist(Playlist playlist) {
        this.selectedPlaylist = playlist;
    }

    public void createPlaylist(Playlist playlistName){
        ObservableList<Playlist> playlists = playlistList.getItems();
        playlists.add(playlistName);  // Add the new playlist name to the list
        playlistList.setItems(playlists);  // Update the playlist view

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

    private Song createSongFromMediaFile(File file) {
        return new Song(file.getName(), "Unknown Artist", "Unknown Genre", "0:00");
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


    public void clickDeleteBtn(ActionEvent actionEvent) {
        Song s = songTableView.getSelectionModel().getSelectedItem();
        songManager.deleteSong(s.songIdProperty().get());
    }

    public void clickEditBtn(ActionEvent actionEvent) {
        Song s = new Song("Memo", "Totest", "Crazy");
        if(!songTableView.getSelectionModel().getSelectedItem().equals(s))
            songManager.updateSong(s);
    }
    @FXML
    private void openDeletePlaylistWindow(){
        int selectedPlayListId = playlistList.getSelectionModel().getSelectedIndex();
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("DeletePlaylist.fxml"));
            Parent root = loader.load();

            DeletePlaylistController deleteController = loader.getController();
            deleteController.setMainWindowController(this);
            deleteController.setSelectedId(selectedPlayListId);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Delete Playlist");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateSongsInPlaylist(){
        if (selectedPlaylist != null) {
            //songTableView.setItems(selectedPlaylist.getSongs());
        }
        else {
            // Clear the song list view if no playlist is selected
            songTableView.setItems(FXCollections.observableArrayList());
        }
    }

    public void addSelectedSongToPlaylist(){
        if (songListView != null && selectedPlaylist != null){
            String selectedSong = songListView.getSelectionModel().getSelectedItems().toString();
            System.out.println(selectedPlaylist.getName().getValue());
            if (selectedSong != null) {
                //selectedPlaylist.addSong(selectedSong);
                updateSongsInPlaylist();
            }
        }
    }

    @FXML
    private void openEditPlaylistWindow(){
        Playlist selectedPlaylistName = playlistList.getSelectionModel().getSelectedItems().get(0);  // Retrieve the selected playlist name

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditPlaylist.fxml"));
            Parent root = loader.load();

            EditPlaylistController editPlaylistController = loader.getController();
            editPlaylistController.setMainWindowController(this);
            editPlaylistController.setSelectedPlaylistName(selectedPlaylistName); // Pass the selected playlist name

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Playlist");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

