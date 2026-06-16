package hr.algebra.controllers;

import hr.algebra.exceptions.ValidateException;
import hr.algebra.models.*;
import hr.algebra.repository.IActorRepository;
import hr.algebra.repository.IDirectorRepository;
import hr.algebra.repository.ISeriesRepository;
import hr.algebra.repository.RepositoryFactory;
import hr.algebra.utils.AlertUtil;
import hr.algebra.utils.SessionManager;
import hr.algebra.utils.XmlActivityLogger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class SeriesFormController {
    @FXML public Label lblError;
    private Series seriesForEdit = null;
    @FXML private Label lblPosterPath;
    @FXML private Label lblTitle;
    @FXML private TextField txtName;
    @FXML private TextField txtSeasons;
    @FXML private TextField txtEpisodes;
    @FXML private TextField txtYearStart;
    @FXML private TextField txtYearEnd;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<Director> cmbDirector;
    @FXML private ImageView ivPoster;
    @FXML private ListView<Genre> listGenres;
    @FXML private ListView<StreamingPlatform> listPlatforms;
    @FXML private ListView<Actor> listAllActors;
    @FXML private ListView<Actor> listCast;
    private String selectedPosterPath;
    private final ISeriesRepository seriesRepo = RepositoryFactory.series();
    private final IActorRepository actorRepo = RepositoryFactory.actors();
    private final IDirectorRepository directorRepo = RepositoryFactory.directors();
    private static final DataFormat ACTOR_FORMAT = new DataFormat("application/x-actor");
    private static final String ERROR_MESSAGE = "Error";

    @FXML
    public void initialize() {
        setupDirectorComboBox();
        setupGenresList();
        setupPlatformsList();
        setupActorsList();
        setupDragAndDrop();
    }

    @FXML
    private void onSelectPoster() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Series Poster");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

            File selectedFile = fileChooser.showOpenDialog(txtName.getScene().getWindow());
            if (selectedFile == null) {
                return;
            }
            Path assetsDir = Paths.get("assets");
            if (!Files.exists(assetsDir)) {
                Files.createDirectories(assetsDir);
            }

            String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
            Path targetPath = assetsDir.resolve(fileName);
            Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            this.selectedPosterPath = targetPath.toString().replace("\\", "/");
            lblPosterPath.setText(fileName);
            ivPoster.setImage(new Image(selectedFile.toURI().toString()));

            XmlActivityLogger.logActivity(SessionManager.getCurrentUser().getFullName(), "User selected image: " + fileName, "INFO");

        } catch (Exception e) {
            AlertUtil.showError(ERROR_MESSAGE, "Error saving image: " + e.getMessage());
        }
    }

    private void setupDirectorComboBox() {
        List<Director> directors = directorRepo.getAll();
        cmbDirector.setItems(FXCollections.observableArrayList(directors));
        cmbDirector.setConverter(new StringConverter<>() {
            @Override
            public String toString(Director d) {
                return d == null ? "" : d.getFullName();
            }

            @Override
            public Director fromString(String s) {
                return null;
            }
        });
    }

    private void setupGenresList() {
        listGenres.setItems(FXCollections.observableArrayList(Genre.values()));
        listGenres.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listGenres.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Genre item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDescription());
                }
            }
        });
    }

    private void setupPlatformsList() {
        listPlatforms.setItems(FXCollections.observableArrayList(StreamingPlatform.values()));
        listPlatforms.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listPlatforms.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(StreamingPlatform item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.name());
                }
            }
        });
    }

    private void setupActorsList() {
        List<Actor> actors = actorRepo.getAll();
        listAllActors.setItems(FXCollections.observableArrayList(actors));
        listAllActors.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Actor a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) {
                    setText(null);
                } else {
                    setText(a.getName() + " " + a.getSurname());
                }
            }
        });
        listCast.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Actor a, boolean empty) {
                super.updateItem(a, empty);
                if (empty || a == null) {
                    setText(null);
                } else {
                    setText(a.getName() + " " + a.getSurname());
                }
            }
        });
    }

    private void setupDragAndDrop() {
        listAllActors.setOnDragDetected(event -> {
            Actor selected = listAllActors.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }
            Dragboard dragboard = listAllActors.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            content.put(ACTOR_FORMAT, selected.getId());
            dragboard.setContent(content);
            event.consume();
        });

        listCast.setOnDragOver(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasContent(ACTOR_FORMAT)) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        listCast.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;

            if (dragboard.hasContent(ACTOR_FORMAT)) {
                int actorId = (int) dragboard.getContent(ACTOR_FORMAT);
                actorRepo.getById(actorId).ifPresent(actor -> {
                    if (!listCast.getItems().contains(actor)) {
                        listCast.getItems().add(actor);
                    } else {
                        AlertUtil.showWarning("Duplicate", actor.getName() + " " + actor.getSurname() + " is already in cast.");
                    }
                });
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    @FXML
    private void onRemoveFromCast() {
        Actor selected = listCast.getSelectionModel().getSelectedItem();
        if (selected != null) {
            listCast.getItems().remove(selected);
        }
    }

    public void setSeriesForEdit(Series series) {
        this.seriesForEdit = series;
        lblTitle.setText("Edit series:");

        txtName.setText(series.getName());
        txtSeasons.setText(String.valueOf(series.getNumberOfSeasons()));
        txtEpisodes.setText(String.valueOf(series.getNumberOfEpisodes()));
        txtYearStart.setText(String.valueOf(series.getYearOfRelease()));
        if (series.getEndYear() != null) {
            txtYearEnd.setText(String.valueOf(series.getEndYear()));
        }
        txtDescription.setText(series.getDescription());
        cmbDirector.setValue(series.getDirector());

        if (series.getPosterPath() != null && !series.getPosterPath().isEmpty()) {
            this.selectedPosterPath = series.getPosterPath();
            File imgFile = new File(selectedPosterPath);
            if (imgFile.exists()) {
                ivPoster.setImage(new Image(imgFile.toURI().toString()));
                lblPosterPath.setText(imgFile.getName());
            }
        }

        for (Genre genre : series.getGenres()) {
            int genreIndex = listGenres.getItems().indexOf(genre);
            if (genreIndex >= 0) listGenres.getSelectionModel().select(genreIndex);
        }

        for (StreamingPlatform platform : series.getStreamingPlatforms()) {
            int platformIndex = listPlatforms.getItems().indexOf(platform);
            if (platformIndex >= 0) listPlatforms.getSelectionModel().select(platformIndex);
        }

        listCast.setItems(FXCollections.observableArrayList(series.getActors()));
    }

    @FXML
    private void onSave() {
        try {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                throw new ValidateException(ERROR_MESSAGE, "Name cannot be empty.");
            }

            int seasons;
            int episodes;
            int yearStart;

            try {
                seasons = Integer.parseInt(txtSeasons.getText().trim());
            } catch (NumberFormatException e) {
                AlertUtil.showError(ERROR_MESSAGE, "Number of seasons must be a whole number.");
                return;
            }

            try {
                episodes = Integer.parseInt(txtEpisodes.getText().trim());
            } catch (NumberFormatException e) {
                AlertUtil.showError(ERROR_MESSAGE, "Number of episodes must be a whole number.");
                return;
            }

            try {
                yearStart = Integer.parseInt(txtYearStart.getText().trim());
            } catch (NumberFormatException e) {
                AlertUtil.showError(ERROR_MESSAGE, "Year when started emitting must be a whole number.");
                return;
            }

            Integer yearEnd = null;
            if (!txtYearEnd.getText().trim().isEmpty()) {
                try {
                    yearEnd = Integer.parseInt(txtYearEnd.getText().trim());
                } catch (NumberFormatException e) {
                    AlertUtil.showError(ERROR_MESSAGE, "Year when finished emitting must be a whole number.");
                    return;
                }
            }

            Series series;
            if (seriesForEdit == null) {
                series = new Series(name, seasons, episodes, yearStart, yearEnd, txtDescription.getText().trim());
            } else {
                series = seriesForEdit;
                series.setGenres(new ArrayList<>());
                series.setStreamingPlatforms(new ArrayList<>());
                series.setActors(new ArrayList<>());
                series.setName(name);
                series.setNumberOfSeasons(seasons);
                series.setNumberOfEpisodes(episodes);
                series.setYearOfRelease(yearStart);
                series.setEndYear(yearEnd);
                series.setDescription(txtDescription.getText().trim());
            }

            series.setPosterPath(this.selectedPosterPath);
            series.setDirector(cmbDirector.getValue());

            for (Genre genre : listGenres.getSelectionModel().getSelectedItems()) {
                series.addGenre(genre);
            }
            for (StreamingPlatform platform : listPlatforms.getSelectionModel().getSelectedItems()) {
                series.addStreamingPlatform(platform);
            }
            for (Actor actor : listCast.getItems()) {
                series.addActor(actor);
            }

            seriesRepo.save(series);

            XmlActivityLogger.logActivity(SessionManager.getCurrentUser().getFullName(), "Saved series: " + series.getName(), "INFO");
            closeWindow();

        } catch (ValidateException e) {
            AlertUtil.showError(ERROR_MESSAGE, e.getMessage());
        } catch (NumberFormatException e) {
            AlertUtil.showError(ERROR_MESSAGE, "Seasons, episodes, and years must be numbers.");
        }
    }
    @FXML
    private void onCancel() {
        closeWindow();
    }
    private void closeWindow() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }
}