package hr.algebra.services;

import hr.algebra.models.Actor;
import hr.algebra.models.Director;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;
import java.util.List;
import java.util.Optional;

public final class ChoiceDialogService {
    private ChoiceDialogService() {
    }
    public static Optional<Actor> chooseActor(List<Actor> actors, String title, String message) {
        if (actors == null || actors.isEmpty()) {
            return Optional.empty();
        }

        ChoiceDialog<Actor> dialog = new ChoiceDialog<>(actors.getFirst(), actors);
        dialog.setTitle(title);
        dialog.setHeaderText(message);

        ComboBox<Actor> comboBox = (ComboBox<Actor>) dialog.getDialogPane().lookup(".combo-box");
        if (comboBox != null) {
            comboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Actor actor) {
                    return actor == null ? "" : actor.getFullName();
                }
                @Override
                public Actor fromString(String string) {
                    return null;
                }
            });
        }
        return dialog.showAndWait();
    }

    public static Optional<Director> chooseDirector(List<Director> directors, String title, String header) {
        if (directors == null || directors.isEmpty()) {
            return Optional.empty();
        }

        ChoiceDialog<Director> dialog = new ChoiceDialog<>(directors.getFirst(), directors);
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        ComboBox<Director> comboBox = (ComboBox<Director>) dialog.getDialogPane().lookup(".combo-box");
        if (comboBox != null) {
            comboBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Director director) {
                    return director == null ? "" : director.getFullName();
                }
                @Override
                public Director fromString(String string) {
                    return null;
                }
            });
        }
        return dialog.showAndWait();
    }
}
