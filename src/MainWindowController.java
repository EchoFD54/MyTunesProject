
import be.Playlist;
import be.Song;
import bll.PlaylistManager;
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
import javafx.scene.input.MouseEvent;
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
    public TableColumn titleColumn,artistColumn,genreColumn,timeColumn,playlistName,songs,time, titleCol, artistCol, genreCol, timeCol;
    @FXML
    public Button editBtn;
    @FXML
    public TableView<Song> songsInPlaylist;
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
    private AddSongWindowController addSongController;

    SongManager songManager = new SongManager();
    PlaylistManager playlistManager = new PlaylistManager();


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

        // Shows all songs saved on the Database
        for(Song s : songManager.getAllSongs()){
            songTableView.getItems().add(s);
        }

        // Set up the columns in the TableView
        TableColumn<Playlist, String> playlistName = (TableColumn<Playlist, String>) playlistList.getColumns().get(0);
        TableColumn<Playlist, String> songs = (TableColumn<Playlist, String>) playlistList.getColumns().get(1);
        TableColumn<Playlist, String> time = (TableColumn<Playlist, String>) playlistList.getColumns().get(2);

        // Define cell value factories for each column
        playlistName.setCellValueFactory(cellData -> cellData.getValue().getName());
        songs.setCellValueFactory(cellData -> cellData.getValue().getSongs());
        time.setCellValueFactory(cellData -> cellData.getValue().getTime());

        // Set up the columns in the TableView
        TableColumn<Song, String> titleCol = (TableColumn<Song, String>) songsInPlaylist.getColumns().get(0);
        TableColumn<Song, String> artistCol = (TableColumn<Song, String>) songsInPlaylist.getColumns().get(1);
        TableColumn<Song, String> genreCol = (TableColumn<Song, String>) songsInPlaylist.getColumns().get(2);
        TableColumn<Song, String> timeCol = (TableColumn<Song, String>) songsInPlaylist.getColumns().get(3);

        // Define cell value factories for each column
        titleCol.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        artistCol.setCellValueFactory(cellData -> cellData.getValue().artistProperty());
        genreCol.setCellValueFactory(cellData -> cellData.getValue().genreProperty());
        timeCol.setCellValueFactory(cellData -> cellData.getValue().timeProperty());

        // Shows all playlists saved on the Database
        for(Playlist p : playlistManager.getAllPlaylists()){
            playlistList.getItems().add(p);
        }

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

            // Set up the end of media handler
            newMediaPlayer.setOnEndOfMedia(() -> {
                songIndex++;
                playNextSong();
            });

            // Set up the ready event handler to update the duration
            newMediaPlayer.setOnReady(() -> {
                Duration duration = newMediaPlayer.getMedia().getDuration();
                String formattedTime = formatDuration(duration);

                // Update the current song's duration in the TableView
                Song currentSong = songTableView.getItems().get(songIndex);
                currentSong.setTime(formattedTime);

                // Update the TextFlow with the current song name
                updateTextFlow();
            });

            mediaPlayer = newMediaPlayer;
            mediaView.setMediaPlayer(mediaPlayer);
            setSongProgress();
            setVolumeSlider();

            playCurrentSong();
            updateTimeLabel();
        } else {
            // All songs have been played, loop back to the first song
            songIndex = 0;
            //playNextSong();
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
            stage.setTitle("Add/Edit Song");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Song createSongFromMediaFile(File file) {
        return new Song(file.getName(), "Unknown Artist", "Unknown Genre", "0:00");
    }

    public void updateSongProperties(String title, String artist, String genre, String time, String filePath) {
        // Check if the song is already in the TableView
        boolean songExists = false;
        Song existingSong = null;

        for (Song song : songTableView.getItems()) {
            if (song.getFilePath().equals(filePath)) {
                // Modify existing song's properties
                existingSong = song;
                existingSong.setTitle(title);
                existingSong.setArtist(artist);
                existingSong.setGenre(genre);
                existingSong.setTime(time);
                songExists = true;
                break;
            }
        }

        // If the song is not in the TableView, add a new one
        if (!songExists) {
            // Create a new Song object with the actual time
            Song newSong = new Song(title, artist, genre, time, filePath);

            // Add the newMedia to the songList
            songList.add(new Media(new File(filePath).toURI().toString()));

            // Add the newSong to the TableView
            songTableView.getItems().add(newSong);
        }

        // If mediaPlayer is null or the existingSong is being played, start playing the new song
        if (mediaPlayer == null || (existingSong != null && mediaPlayer.getMedia().getSource().equals(existingSong.getFilePath()))) {
            playNextSong();
        }

        // Set the current song name
        currentSongName = title;

        // Update the TextFlow with the current song name
        updateTextFlow();

        // Close the AddSongWindow
        if (addSongController != null) {
            addSongController.closeAddSongWindow();
        }

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
        // Check if a song is selected in the TableView
        Song selectedSong = songTableView.getSelectionModel().getSelectedItem();

        if (selectedSong != null) {
            // Open the AddSongWindowController with the selected song's properties
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddSongWindow.fxml"));
            Parent root;
            try {
                root = loader.load();

                // Get the controller instance
                AddSongWindowController addSongController = loader.getController();

                // Pass the reference of MainWindowController to AddSongWindowController
                addSongController.setMainWindowController(this);

                // Set the fields in AddSongWindowController with the selected song's properties
                addSongController.titleField.setText(selectedSong.titleProperty().get());
                addSongController.artistField.setText(selectedSong.artistProperty().get());
                addSongController.genreField.setText(selectedSong.genreProperty().get());
                addSongController.fileField.setText(selectedSong.filePathProperty().get());

                // Create a new stage for the AddSongWindow
                Stage stage = new Stage();
                stage.setTitle("Edit Song");
                stage.setScene(new Scene(root));

                // Set the stage to AddSongWindowController
                addSongController.setStage(stage);

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Show an alert or message indicating that no song is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Song Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a song from the playlist to edit.");
            alert.showAndWait();
        }

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
        Integer PlaylistId = playlistList.getSelectionModel().getSelectedItem().getId().get();
        Integer SongsId = songTableView.getSelectionModel().getSelectedItem().songIdProperty().get();
        if(PlaylistId != null && SongsId != null){
            playlistManager.CreateSongsOfPlaylist(PlaylistId, SongsId);
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

    public void songsInPlayList(MouseEvent mouseEvent) {
        songsInPlaylist.getItems().clear();
        for(Song s : playlistManager.getAllSongsOfPlaylist(playlistList.getSelectionModel().getSelectedItem().getId().get())){
            songsInPlaylist.getItems().add(s);
        }
    }

    @FXML
    private void moveSongUp(ActionEvent actionEvent){
        int selectedIndex = songsInPlaylist.getSelectionModel().getSelectedIndex();
        if (selectedIndex > 0){
            Song selectedSong = songsInPlaylist.getItems().remove(selectedIndex);
            songsInPlaylist.getItems().add(selectedIndex - 1, selectedSong);
            songsInPlaylist.getSelectionModel().select(selectedIndex - 1);
        }
    }

    @FXML
    private void moveSongDown(ActionEvent actionEvent){
        int selectedIndex = songsInPlaylist.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < songsInPlaylist.getItems().size() -1){
            Song selectedSong = songsInPlaylist.getItems().remove(selectedIndex);
            songsInPlaylist.getItems().add(selectedIndex + 1, selectedSong);
            songsInPlaylist.getSelectionModel().select(selectedIndex + 1);
        }
    }
}

