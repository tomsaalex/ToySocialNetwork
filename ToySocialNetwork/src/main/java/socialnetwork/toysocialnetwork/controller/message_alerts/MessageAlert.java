package socialnetwork.toysocialnetwork.controller.message_alerts;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class MessageAlert {
    /**
     * Displays an information alert with the given parameters.
     * @param owner The stage that owns the alert.
     * @param type The type of alert used.
     * @param header The header for the alert.
     * @param text The message of the alert.
     */
    public static void showMessage(Stage owner, Alert.AlertType type, String header, String text)
    {
        Alert message = new Alert(type);
        message.initOwner(owner);
        message.setHeaderText(header);
        message.setContentText(text);

        message.showAndWait();
    }

    /**
     * Displays an error alert with the given parameters.
     * @param owner The stage that owns the alert.
     * @param text The message of the alert.
     */
    public static void showErrorMessage(Stage owner, String text)
    {
        Alert errorMessage = new Alert(Alert.AlertType.ERROR);
        errorMessage.initOwner(owner);
        errorMessage.setContentText(text);
        errorMessage.setTitle("Mesaj eroare");

        errorMessage.showAndWait();
    }
}
