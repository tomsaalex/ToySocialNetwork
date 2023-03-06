package socialnetwork.toysocialnetwork.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.toysocialnetwork.MainGUI;
import socialnetwork.toysocialnetwork.controller.message_alerts.MessageAlert;
import socialnetwork.toysocialnetwork.service.FriendshipsService;
import socialnetwork.toysocialnetwork.service.MessagesService;
import socialnetwork.toysocialnetwork.service.UsersService;

import java.io.IOException;
import java.net.URL;

public class RegisterController {

    public TextField usernameTextField;
    public PasswordField passwordTextField;
    public TextField lastNameTextField;
    public TextField firstNameTextField;
    UsersService usersService;
    FriendshipsService friendshipsService;
    MessagesService messagesService;
    Stage mainStage;

    public void setupController(UsersService usersService, FriendshipsService friendshipsService, Stage mainStage) {
        this.usersService = usersService;
        this.friendshipsService = friendshipsService;
        this.mainStage = mainStage;
    }

    @FXML
    private void loadLoginWindow() throws IOException {
        URL url = MainGUI.class.getResource("views/login-view.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(url);

        Scene loginScene = new Scene(fxmlLoader.load(), 325, 475);

        LoginController loginController = fxmlLoader.getController();
        loginController.setupController(usersService, friendshipsService, messagesService, mainStage);

        mainStage.setScene(loginScene);
    }

    @FXML
    private void registerUser()
    {
        String username = usernameTextField.getText();
        //String password = passwordTextField.getText();
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();


        boolean addResult = usersService.serviceAddUser(firstName, lastName, username);
        if(addResult)
        {
            MessageAlert.showMessage(mainStage, Alert.AlertType.INFORMATION, "INFORMATION", "Registration successful!");
        }
        else
        {
            MessageAlert.showErrorMessage(mainStage, "Could not register a user with this data.");
        }
    }
}
