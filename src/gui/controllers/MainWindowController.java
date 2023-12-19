package gui.controllers;

import be.Playlist;
import be.Song;
import bll.PlaylistManager;
import bll.SongManager;

import javafx.beans.property.SimpleIntegerProperty;
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
    private Slider volumeSlider, songProgress;
    @FXML
    private Button playBtn, addSongsBtn, filterBtn;
    @FXML
    private Label timerLabel;
    @FXML
    private TextFlow songTextFlow;
    @FXML
    private TableView<Song> songTableView, songsInPlaylist;
    public TableView<Playlist> playlistList;
    @FXML
    private TextField filterTextField;
    @FXML
    private MediaView mediaView;
    @FXML
    private MediaPlayer mediaPlayer;
    @FXML
    private List<Media> songList = new ArrayList<>();

    private Playlist selectedPlaylist;
    private int songIndex = 0;
    private double currentVolume = 0.5;
    private boolean canPlaySong = false;
    private String currentSongName = "";
    private AddSongWindowController addSongController;
    private SongManager songManager = new SongManager();
    private PlaylistManager playlistManager = new PlaylistManager();
    private boolean isFilterActive = false;


    public void initialize() {
        setSongsTableView();
        setUpPlaylistTableView();
        setUpSongsInPlaylistTableView();

        // Show all songs saved on the Database
        for(Song s : songManager.getAllSongs()){
            songTableView.getItems().add(s);
            // Add the songs into songList to play them
            if (!songList.contains(s)) {
                songList.add(new Media(new File(s.getFilePath()).toURI().toString()));
            }
        }

        // Shows all playlists saved on the Database
        for(Playlist p : playlistManager.getAllPlaylists()){
            playlistList.getItems().add(p);
        }
        setButtonAndFilter();
    }


    private void playNextSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        if (songIndex < songList.size()) {
            MediaPlayer newMediaPlayer = new MediaPlayer(songList.get(songIndex));
            if (mediaPlayer != null) {
                newMediaPlayer.setVolume(mediaPlayer.getVolume());
            }

            // Set up the end of media handler
            newMediaPlayer.setOnEndOfMedia(() -> {
                songIndex++;
                playNextSong();
            });

            newMediaPlayer.setOnReady(() -> {
                Duration duration = newMediaPlayer.getMedia().getDuration();
                String formattedTime = formatDuration(duration);
                Song currentSong = songTableView.getItems().get(songIndex);
                currentSong.setTime(formattedTime);
                currentSongName = currentSong.titleProperty().get();
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
                playBtn.setText("⏸");
                playCurrentSong();
            }
        } else {
            // Toggle between play and pause
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playBtn.setText("▶");
            } else {
                playCurrentSong();
                playBtn.setText("⏸");
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
                playBtn.setText("▶");
                double duration = mediaPlayer.getTotalDuration().toMillis();
                double currentTime = songProgress.getValue() * duration / 100.0;
                mediaPlayer.seek(new Duration(currentTime));
            }
        });

        // Update the progress slider as the media plays
        songProgress.valueChangingProperty().setValue(null);
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!songProgress.isValueChanging()) {
                playBtn.setText("⏸");
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

    public void deletePlaylist(){
        playlistManager.deletePlaylist(playlistList.getSelectionModel().getSelectedItem().getId().get());
        refreshPlaylistTableView();
    }

    public void editPlaylist(String selectedPlaylistName, Playlist newPlaylistName){
        newPlaylistName.setName(selectedPlaylistName);
        ObservableList<Playlist> playlists = playlistList.getItems();
        int index = playlists.indexOf(selectedPlaylistName);
        if (index != -1){
            playlists.set(index, newPlaylistName);  // Update the name of the playlist
            playlistList.setItems(playlists);  // Update the playlistList view
        }
        refreshPlaylistTableView();
    }

    @FXML
    private void openNewPlaylistWindow() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/NewPlaylist.fxml"));
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/AddSongWindow.fxml"));
        Parent root;
        try {
            root = loader.load();
            AddSongWindowController addSongController = loader.getController();
            addSongController.setMainWindowController(this);
            Stage stage = new Stage();
            stage.setTitle("Add/Edit Song");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateSongProperties(String title, String artist, String genre, String time, String filePath) {
        // Check if the song is already in the TableView
        boolean songExists = false;
        Song existingSong = null;

        // Modify existing song's properties
        for (Song song : songTableView.getItems()) {
            if (song.getFilePath().equals(filePath)) {
                existingSong = song;
                existingSong.setTitle(title);
                existingSong.setArtist(artist);
                existingSong.setGenre(genre);
                existingSong.setTime(time);
                // Updates the song properties in the database
                songManager.updateSong(existingSong);
                songExists = true;
                break;
            }
        }

        // If the song is not in the TableView, add a new one
        if (!songExists) {
            Song newSong = new Song(title, artist, genre, time, filePath);
            // Adds a new song in the database
            songManager.createSong(newSong);

            songList.add(new Media(new File(filePath).toURI().toString()));

            songTableView.getItems().add(newSong);
        }

        // If mediaPlayer is null or the existingSong is being played, start playing the new song
        if (mediaPlayer == null || (existingSong != null && mediaPlayer.getMedia().getSource().equals(existingSong.getFilePath()))) {
            playNextSong();
        }

        currentSongName = title;
        updateTextFlow();

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
        // Check if a song is selected in the TableView
        Song selectedSong = songTableView.getSelectionModel().getSelectedItem();

        if (selectedSong != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete the selected song?");
            alert.setContentText("This action cannot be undone.");

            // Handle the users response
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    // User clicked OK, delete the songf
                    int selectedIndex = songTableView.getSelectionModel().getSelectedIndex();

                    // Remove the song from the Database, TableView and songList
                    songManager.deleteSong(selectedSong.songIdProperty().getValue());
                    songTableView.getItems().remove(selectedIndex);
                    songList.remove(selectedSong);
                    refreshPlaylistTableView();


                    // Stop the current song if it is the one being deleted
                    if (mediaPlayer != null && mediaPlayer.getMedia().getSource().equals(selectedSong.getFilePath())) {
                        mediaPlayer.stop();
                        mediaPlayer.dispose();
                    }

                    // Reset the MediaPlayer and MediaView if the song being deleted is the current one
                    if (songIndex == selectedIndex) {
                        mediaPlayer = null;
                        mediaView.setMediaPlayer(null);
                    }

                    // Reset the current song name
                    currentSongName = "";

                    // Update the TextFlow with the current song name
                    updateTextFlow();
                }
            });
        } else {
            // Show a message saying that no song is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Song Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a song from the playlist to delete.");
            alert.showAndWait();
        }

    }

    public void clickEditBtn(ActionEvent actionEvent) {
        // Check if a song is selected in the TableView
        Song selectedSong = songTableView.getSelectionModel().getSelectedItem();

        if (selectedSong != null) {
            // Open the AddSongWindowController with the selected song's properties
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/AddSongWindow.fxml"));
            Parent root;
            try {
                root = loader.load();

                AddSongWindowController addSongController = loader.getController();

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
    }

    @FXML
    private void openDeletePlaylistWindow(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/DeletePlaylist.fxml"));
            Parent root = loader.load();

            DeletePlaylistController deleteController = loader.getController();
            deleteController.setMainWindowController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Delete Playlist");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addSelectedSongToPlaylist(){
        Integer playlistId = playlistList.getSelectionModel().getSelectedItem().getId().get();
        Integer songId = songTableView.getSelectionModel().getSelectedItem().songIdProperty().get();
        if (playlistId != null && songId != null) {
            playlistManager.createSongsOfPlaylist(playlistId, songId);
        }
        refreshPlaylistTableView();
    }
    @FXML
    private void openEditPlaylistWindow(){
        Playlist selectedPlaylistName = playlistList.getSelectionModel().getSelectedItems().get(0);  // Retrieve the selected playlist name

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/views/EditPlaylist.fxml"));
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

    public void toggleFilterBtn(ActionEvent actionEvent) {
        if (isFilterActive) {
            clearFilter();
        } else {
            applyFilter();
        }
    }

    private void applyFilter() {
        String filterQuery = filterTextField.getText().toLowerCase();
        ObservableList<Song> filteredSongs = FXCollections.observableArrayList();

        for (Song song : songManager.getAllSongs()) {
            if (song.titleProperty().get().toLowerCase().contains(filterQuery) || song.artistProperty().get().toLowerCase().contains(filterQuery)) {
                filteredSongs.add(song);
            }
        }

        songTableView.setItems(filteredSongs);
        filterBtn.setText("Clear");
        isFilterActive = true;
    }

    private void clearFilter() {
        songTableView.setItems(FXCollections.observableArrayList(songManager.getAllSongs()));
        filterTextField.clear();
        filterBtn.setText("Filter");
        isFilterActive = false;
    }

    @FXML
    private void deleteSongFromPlaylist(ActionEvent actionEvent){
        Song selectedSong = songsInPlaylist.getSelectionModel().getSelectedItem();
        if (selectedSong != null){
            Playlist selectedPlaylist = playlistList.getSelectionModel().getSelectedItem();
            playlistManager.deleteSongFromPlaylist(selectedSong.songIdProperty().get(), selectedPlaylist.getId().get());
            songsInPlaylist.getItems().remove(selectedSong);
            refreshPlaylistTableView();
        }
    }

    private void setSongsTableView(){
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

    }

    private void setButtonAndFilter(){
        // Set a listener for handling song selection
        songTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            int selectedSongIndex = songTableView.getSelectionModel().getSelectedIndex();
            if (selectedSongIndex >= 0) {
                songIndex = selectedSongIndex;
                currentSongName = newValue.titleProperty().get();
                updateTextFlow();
                playNextSong();
            }
        });

        // Set the event handler for the addSongsBtn button
        addSongsBtn.setOnAction(this::clickAddSongsBtn);

        //event handler for the filter function
        filterTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                clearFilter();
            }
        });

        timerLabel.setText("0:00");
        playNextSong();
    }

    private int numberSongsInPlaylist(int playlistId){
        if(playlistId > 0){
            return playlistManager.getAllSongsOfPlaylist(playlistId).size();
        }
        else return -1;
    }

    private void setUpPlaylistTableView(){
        // Set up the columns in the TableView
        TableColumn<Playlist, String> playlistName = (TableColumn<Playlist, String>) playlistList.getColumns().get(0);
        TableColumn<Playlist, Integer> songs = (TableColumn<Playlist, Integer>) playlistList.getColumns().get(1);
        TableColumn<Playlist, String> time = (TableColumn<Playlist, String>) playlistList.getColumns().get(2);

        // Define cell value factories for each column
        playlistName.setCellValueFactory(cellData -> cellData.getValue().getName());
        songs.setCellValueFactory(cellData -> {
            int playlistId = cellData.getValue().getId().get();
            int totalSongs = numberSongsInPlaylist(playlistId);
            return new SimpleIntegerProperty(totalSongs).asObject();
        });
        time.setCellValueFactory(cellData -> cellData.getValue().getTime());
    }

    public void refreshPlaylistTableView() {
        playlistList.getItems().clear();
        List<Playlist> allPlaylists = playlistManager.getAllPlaylists();
        playlistList.getItems().addAll(allPlaylists);
    }

    private void setUpSongsInPlaylistTableView(){
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
    }
}

